package actors

import actors.DockerClientProtocol._
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import play.api.libs.json.{JsValue, Json}

import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.mvc.WebSocket.FrameFormatter
import play.api.libs.json.Reads._

object ClientConnection {
  def props(topLevelActor: ActorRef, email: String, out: ActorRef) = Props(classOf[ClientConnection], topLevelActor, email, out)

  /**
   * Events to/from the client side
   */
  sealed trait ClientEvent

  case class DockerInfo(info: String) extends ClientEvent
  case class DockerImages(images: String) extends ClientEvent
  case class DockerContainers(containers: String) extends ClientEvent

  /*
   * JSON serialisers/deserialisers for the above messages
   */

  object ClientEvent {
    implicit def clientEventFormat: Format[ClientEvent] = Format(
      (__ \ "message").read[String].flatMap {
        case "DockerInfo" => DockerInfo.dockerInfoFormat.map(identity)
        case "DockerImages" => DockerImages.dockerImagesFormat.map(identity)
        case "DockerContainers" => DockerContainers.dockerContainersFormat.map(identity)
        case other => Reads(_ => JsError("Unknown client event: " + other))
      },
      Writes {
        case dinfo: DockerInfo => DockerInfo.dockerInfoFormat.writes(dinfo)
        case dimgs: DockerImages => DockerImages.dockerImagesFormat.writes(dimgs)
        case dconts: DockerContainers => DockerContainers.dockerContainersFormat.writes(dconts)
      }
    )

    /**
     * Formats WebSocket frames to be ClientEvents.
     */
    implicit def clientEventFrameFormatter: FrameFormatter[ClientEvent] = FrameFormatter.jsonFrame.transform(
      clientEvent => Json.toJson(clientEvent),
      json => Json.fromJson[ClientEvent](json).fold(
        invalid => throw new RuntimeException("Bad client event on WebSocket: " + invalid),
        valid => valid
      )
    )
  }

  object DockerInfo {
    implicit def dockerInfoFormat: Format[DockerInfo] = (
      (__ \ "message").format[String] ~
        (__ \ "data").format[String]
      ).apply({
      case ("DockerInfo", info) => DockerInfo(info)
    }, dockerInfo => ("DockerInfo", dockerInfo.info))
  }
  object DockerImages {
    implicit def dockerImagesFormat: Format[DockerImages] = (
      (__ \ "message").format[String] ~
        (__ \ "data").format[String]
      ).apply({
      case ("DockerImages", images) => DockerImages(images)
    }, dockerImages => ("DockerImages", dockerImages.images))
  }
  object DockerContainers {
    implicit def dockerContainersFormat: Format[DockerContainers] = (
      (__ \ "message").format[String] ~
        (__ \ "data").format[String]
      ).apply({
      case ("DockerContainers", containers) => DockerContainers(containers)
    }, dockerContainers => ("DockerContainers", dockerContainers.containers))
  }
}

/**
 * Represents a client connection
 *
 * @param email The email address of the client
 * @param upstream WebSocket ActorRef
 */
class ClientConnection(topLevelActor: ActorRef, email: String, upstream: ActorRef) extends Actor with ActorLogging {
  import ClientConnection._

  def receive = { // TODO: Refactor unmarshalling. This file is getting too big.
    case DockerInfo(info: String) => topLevelActor ! GetInfo
    case res: GetInfoRes => {
      upstream ! DockerInfo(res.info)
    }

    case DockerImages(images: String) => topLevelActor ! GetImages
    case res: GetImagesRes => {
      upstream ! DockerImages(res.images)
    }

    case DockerContainers(containers: String) => topLevelActor ! GetContainers
    case res: GetContainersRes => {
      upstream ! DockerContainers(res.containers)
    }

    case m: String => log.error("Unknown string message: " + m)
    case _ => log.error("Unknown message")
  }
}