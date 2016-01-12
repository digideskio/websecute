package actors

import akka.actor._
import akka.actor.SupervisorStrategy.escalate

class DockerClientSupervisor() extends Actor with ActorLogging {
  override def supervisorStrategy = OneForOneStrategy() {
    case m: Exception => {
      escalate
    }
  }
  val dockerClientProps = Props(classOf[DockerClient], "1.19", "127.0.0.1:4243")
  val dockerClient = context.actorOf(dockerClientProps)

  def receive = {
    case m => dockerClient forward m
  }
}

class DockerClient(version: String, uri: String) extends Actor with ActorLogging {
  import DockerClientProtocol._

  def receive = {
    case InfoCmd => {
      sender ! InfoRes("The info.")
    }
  }
}

object DockerClientProtocol {
  case object InfoCmd
  case class InfoRes(info: String)
}