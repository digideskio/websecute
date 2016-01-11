package actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import play.api.libs.json.{JsValue, Json}

object ClientConnection {
  def props(email: String, out: ActorRef) = Props(new ClientConnection(email, out))
}

/**
 * Represents a client connection
 *
 * @param email The email address of the client
 */
class ClientConnection(email: String, upstream: ActorRef) extends Actor with ActorLogging {

  def receive = {
    case JsValue => {
      log.info("received JsValue")
      upstream ! Json.obj("type" -> "FailResult", "msg" -> "status")
    }
    case _ => {
      log.info("received something else")
      upstream ! Json.obj("type" -> "FailResult", "msg" -> "status")
    }
  }
}