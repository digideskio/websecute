package actors

import scala.concurrent.duration._
import scala.reflect.ClassTag
import scala.util._
import akka.actor._
import akka.util._
import akka.pattern.ask
import play.api.libs.json._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

object ClientConnection {
  implicit val timeout = Timeout(30.seconds)
  def props(topLevelActor: ActorRef, email: String, upstream: ActorRef) = Props(classOf[ClientConnection], topLevelActor, email, upstream)
}

class ClientConnection(topLevelActor: ActorRef, email: String, upstream: ActorRef) extends Actor with ActorLogging {
  import ClientConnection._
  import actors.DockerWSRequest._

  def receive = {
    case DockerWSRequest(m: DockerWSRequest.Request) => handleDockerWsRequest(m)
    case other => println("Received unknown message: " + other)
  }

  def handleDockerWsRequest(req: DockerWSRequest.Request): Unit = {
    import DockerActor._
    req match {
      case m @ Info => askDocker[InternalInfoResponse](DockerActor.InternalInfo, m)(r => upstream ! Json.toJson(m.response(r.result)))
      case m @ Images => askDocker[InternalImagesResponse](DockerActor.InternalImages, m)(r => upstream ! Json.toJson(m.response(r.result)))
      case m: Containers => askDocker[InternalContainersResponse](DockerActor.InternalContainers(m.filter), m)(r => upstream ! Json.toJson(m.response(r.result)))
      case m: Start => askDocker[InternalStartResponse](DockerActor.InternalStart(m.id), m){r => upstream ! Json.toJson(m.response(r.result))}
      case m: Stop => askDocker[InternalStopResponse](DockerActor.InternalStop(m.id), m)(r => upstream ! Json.toJson(m.response(r.result)))
    }
  }

  def askDocker[T <: DockerActor.InternalResponse](msg: DockerActor.InternalRequest, originalMsg: DockerWSRequest.Request)(body: T => Unit)(implicit tag: ClassTag[T]): Unit = {
    topLevelActor.ask(msg).mapTo[DockerActor.InternalResponse].onComplete {
      case Success(`tag`(r)) => body(r)
      case Failure(f) => log.error(f, "TODO chyba")
    }
  }
}
