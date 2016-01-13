package actors

import scala.concurrent.duration._
import akka.actor._
import akka.actor.SupervisorStrategy.stop
import com.github.dockerjava.core.{DockerClientConfig, DockerClientBuilder}

class DockerClientSupervisor() extends Actor {
  override def supervisorStrategy = OneForOneStrategy(maxNrOfRetries = 3, withinTimeRange = 5 seconds) {
    case m: Exception => stop // TODO: Administrator's log
  }

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

  val config = DockerClientConfig.createDefaultConfigBuilder()
    .withVersion(version)
    .withUri(uri)
    .build()
  val docker = DockerClientBuilder.getInstance(config).build()

  def receive = {
    case DockerInfoCmd => {
      val res = docker.infoCmd().exec()
      sender ! DockerInfoRes(res.toString)
    }
    case DockerImagesCmd => {
      val res = docker.listImagesCmd().exec()
      sender ! DockerImagesRes(res.toString)
    }
  }
}

object DockerClientProtocol {
  case object DockerInfoCmd
  case class DockerInfoRes(info: String)
  case object DockerImagesCmd
  case class DockerImagesRes(images: String)
}