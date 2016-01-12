package actors

import actors.DockerClientProtocol.{InfoCmd, InfoRes}
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import play.api.libs.json.{JsValue, Json}

object ClientConnection {
  def props(topLevelActor: ActorRef, email: String, out: ActorRef) = Props(classOf[ClientConnection], topLevelActor, email, out)
}

/**
 * Represents a client connection
 *
 * @param email The email address of the client
 * @param upstream WebSocket ActorRef
 */
class ClientConnection(topLevelActor: ActorRef, email: String, upstream: ActorRef) extends Actor with ActorLogging {

  def receive = {
    case jsVal: JsValue => {
      topLevelActor ! InfoCmd
      upstream ! Json.obj("type" -> "JsValue", "msg" -> jsVal.toString)
    }
    case info: InfoRes => {
      upstream ! Json.obj("type" -> "InfoRes", "msg" -> InfoRes.toString)
    }
    case _ => {
      upstream ! Json.obj("type" -> "UnknownMessage", "msg" -> "")
    }
  }
}