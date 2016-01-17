package actors

import scala.concurrent.duration._
import akka.actor.SupervisorStrategy.stop
import akka.actor._

import org.apache.commons.lang3.builder.{ToStringBuilder, ToStringStyle}
import com.github.dockerjava.core.DockerClientConfig


class DockerClientSupervisor() extends Actor {
  override def supervisorStrategy = OneForOneStrategy(maxNrOfRetries = 3, withinTimeRange = 5 seconds) {
    case m: Exception => stop // TODO: Administrator's log
  }

  ToStringBuilder.setDefaultStyle(ToStringStyle.JSON_STYLE)
  val dockerClientProps = Props(classOf[DockerClient], "1.17", "http://127.0.0.1:4243") // TODO: Customizable?
  val dockerClient = context.actorOf(dockerClientProps)

  def receive = {
    case m => {
      dockerClient forward m
    }
  }
}

class DockerClient(version: String, uri: String) extends Actor {
  import DockerClientProtocol._
  import services.DockerService
  import akka.pattern.pipe
  import context.dispatcher

  val config = DockerClientConfig.createDefaultConfigBuilder()
    .withVersion(version)
    .withUri(uri)
    .build()

  val docker = new DockerService(config)

  def receive = {
    case DockerInfoCmd => docker.getInfo pipeTo sender
    case DockerImagesCmd => docker.getImages pipeTo sender
    case DockerListContainersCmd => docker.getContainers pipeTo sender
  }
}

object DockerClientProtocol {
  case object DockerInfoCmd
  case class GetInfoRes(info: String)

  case object DockerImagesCmd
  case class GetImagesRes(images: String)

  case object DockerListContainersCmd
  case class GetContainersRes(containers: String)
}