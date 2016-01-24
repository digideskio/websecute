package actors

import akka.event.LoggingReceive

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

  def receive = LoggingReceive {
    case GetInfo => docker.getInfo pipeTo sender
    case GetImages => docker.getImages pipeTo sender
    case GetContainers => docker.getContainers pipeTo sender
    case StartContainer(id: String) => { // Start container & send updated containers
      val that = sender
      docker.startContainer(id) map { startRes =>
        context.parent.tell(GetContainers, that)
        startRes // This result is not used in the UI. TODO: perhaps refactor
      } pipeTo sender
    }
    case StopContainer(id: String) => { // Stop container & send updated containers
      val that = sender
      docker.stopContainer(id) map { stopRes =>
        context.parent.tell(GetContainers, that)
        stopRes // This result is not used in the UI. TODO: perhaps refactor
      } pipeTo sender
    }
  }
}

object DockerClientProtocol {
  case object GetInfo
  case class GetInfoRes(info: String)

  case object GetImages
  case class GetImagesRes(images: String)

  case object GetContainers
  case class GetContainersRes(containers: String)

  case class StartContainer(id: String)
  case class StartContainerRes(id: String)

  case class StopContainer(id: String)
  case class StopContainerRes(id: String)
}