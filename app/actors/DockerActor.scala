package actors

import akka.event.LoggingReceive

import scala.concurrent.duration._
import akka.actor.SupervisorStrategy.stop
import akka.actor._
import org.apache.commons.lang3.builder.{ToStringBuilder, ToStringStyle}
import com.github.dockerjava.core.DockerClientConfig
import actors.DockerWSRequest.Filter
import akka.routing._

object DockerClientSupervisor {
  case class RegisterWorkerRequest(apiVersion: String, url: String, session: String)
}

class DockerClientSupervisor() extends Actor {
  import DockerClientSupervisor._
  import DockerActor._
  override def supervisorStrategy = OneForOneStrategy(maxNrOfRetries = 3, withinTimeRange = 5 seconds) {
    case m: Exception => stop // TODO: Administrator's log
  }

  ToStringBuilder.setDefaultStyle(ToStringStyle.JSON_STYLE)

  var router = Router(RoundRobinRoutingLogic())

  //self ! RegisterWorkerRequest("1.17", "http://127.0.0.1:4243", "session")

  def receive = {
    case RegisterWorkerRequest(api, url, session) =>
      val dockerClientProps = Props(classOf[DockerActor], api, url)
      val dockerClient = context.actorOf(dockerClientProps)
      context watch dockerClient
      router = router.addRoutee(ActorRefRoutee(dockerClient))
    case m: InternalRequest =>
      if (router.routees.isEmpty) sender ! InternalNoWorkersErrorResponse(m)
      router.route(m, sender)
  }
}

object DockerActor {
  def props(apiVersion: String, apiUri: String) = Props(classOf[DockerActor], apiVersion, apiUri)

  sealed trait InternalRequest

  case object InternalInfo extends InternalRequest {
    def response(result: String): InternalResponse = InternalInfoResponse(result, this)
  }

  case object InternalImages extends InternalRequest {
    def response(result: String): InternalResponse = InternalImagesResponse(result, this)
  }

  case class InternalContainers(filter: Filter) extends InternalRequest {
    def response(result: String): InternalResponse = InternalContainersResponse(result, this)
  }

  case class InternalStart(id: String) extends InternalRequest {
    def response(result: Boolean): InternalResponse = InternalStartResponse(result, this)
  }

  case class InternalStop(id: String) extends InternalRequest {
    def response(result: Boolean): InternalResponse = InternalStopResponse(result, this)
  }

  sealed trait InternalResponse {
    def request: InternalRequest
  }

  case class InternalNoWorkersErrorResponse(request: InternalRequest) extends InternalResponse
  case class InternalInfoResponse(result: String, request: InternalRequest) extends InternalResponse
  case class InternalImagesResponse(result: String, request: InternalRequest) extends InternalResponse
  case class InternalContainersResponse(result: String, request: InternalRequest) extends InternalResponse
  case class InternalStartResponse(result: Boolean, request: InternalRequest) extends InternalResponse
  case class InternalStopResponse(result: Boolean, request: InternalRequest) extends InternalResponse
}

class DockerActor(version: String, uri: String) extends Actor {
  import DockerActor._
  import services.DockerService
  import akka.pattern.pipe
  import context.dispatcher

  val config = DockerClientConfig.createDefaultConfigBuilder()
    .withVersion(version)
    .withUri(uri)
    .build()

  val docker = new DockerService(config)

  def receive = LoggingReceive {
    case r @ InternalInfo => docker.getInfo pipeTo sender
    case r @ InternalImages => docker.getImages pipeTo sender
    case InternalContainers(filter) => docker.getContainers(filter) pipeTo sender
    case InternalStart(id) => docker.startContainer(id) pipeTo sender
    case InternalStop(id) => docker.stopContainer(id) pipeTo sender
  }
}