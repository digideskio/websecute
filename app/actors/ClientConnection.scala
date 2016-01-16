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

  /**
   * Event sent from the client when they have moved
   */
  case class DockerInfo(dummy: String) extends ClientEvent
  case class DockerImages(dummy: String) extends ClientEvent
  case class DockerListContainers(dummy: String) extends ClientEvent

  /*
   * JSON serialisers/deserialisers for the above messages
   */

  object ClientEvent {
    implicit def clientEventFormat: Format[ClientEvent] = Format(
      (__ \ "event").read[String].flatMap {
        case "docker-info-cmd" => DockerInfo.dockerInfoFormat.map(identity)
        case "docker-images-cmd" => DockerImages.dockerImagesFormat.map(identity)
        case "docker-list-containers-cmd" => DockerListContainers.dockerListContainersFormat.map(identity)
        case other => Reads(_ => JsError("Unknown client event: " + other))
      },
      Writes {
        case dinfo: DockerInfo => DockerInfo.dockerInfoFormat.writes(dinfo)
        case dimgs: DockerImages => DockerImages.dockerImagesFormat.writes(dimgs)
        case dlistconts: DockerListContainers => DockerListContainers.dockerListContainersFormat.writes(dlistconts)
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
      (__ \ "event").format[String] ~
        (__ \ "dummy").format[String]
      ).apply({
      case ("docker-info-cmd", dummy) => DockerInfo(dummy)
    }, dockerInfo => ("docker-info-cmd", dockerInfo.dummy))
  }
  object DockerImages {
    implicit def dockerImagesFormat: Format[DockerImages] = (
      (__ \ "event").format[String] ~
        (__ \ "dummy").format[String]
      ).apply({
      case ("docker-images-cmd", dummy) => DockerImages(dummy)
    }, dockerImages => ("docker-images-cmd", dockerImages.dummy))
  }
  object DockerListContainers {
    implicit def dockerListContainersFormat: Format[DockerListContainers] = (
      (__ \ "event").format[String] ~
        (__ \ "dummy").format[String]
      ).apply({
      case ("docker-list-containers-cmd", dummy) => DockerListContainers(dummy)
    }, dockerListContainers => ("docker-list-containers-cmd", dockerListContainers.dummy))
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
    case DockerInfo(dummy: String) => topLevelActor ! DockerInfoCmd
    case res: DockerInfoRes => {
      upstream ! DockerInfo(res.info)
    }

    case DockerImages(dummy: String) => topLevelActor ! DockerImagesCmd
    case res: DockerImagesRes => {
      upstream ! DockerImages(res.images)
    }

    case DockerListContainers(dummy: String) => topLevelActor ! DockerListContainersCmd
    case res: DockerListContainersRes => {
      upstream ! DockerListContainers(res.containers)
    }

    case m: String => log.error("Unknown string message: " + m)
    case _ => log.error("Unknown message")
  }
}