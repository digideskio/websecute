// inspired by https://github.com/typesafehub/activator/blob/889970aab1f990cc477e4a9e1b3bab6b1897acef/ui/app/activator/NewRelic.scala

package actors

import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.libs.json.Json._


object DockerWSRequest {
  import actors.JsonHelper._
  import actors.RequestHelpers._

  val requestTag = "DockerWSRequest"
  val responseTag = "dockerResponse"
  val responseSubTag = "responseSubTag"

  // Auxiliary data structures

  case class Filter(key: String, value: String)

  implicit val filterReads: Reads[Filter] = (
    (__ \ "filterKey").read[String] ~
      (__ \ "filterValue").read[String]
    )(Filter.apply _)

  implicit val filterWrites: Writes[Filter] = (
    (__ \ "filterKey").write[String] ~
      (__ \ "filterValue").write[String]
    )(unlift(Filter.unapply))

  // EOF auxiliary data structures

  sealed trait Request

  case object Info extends Request {
    def response(result: String): Response = InfoResponse(result, this)
  }

  case object Images extends Request {
    def response(result: String): Response = ImagesResponse(result, this)
  }

  case class Containers(filter: Filter) extends Request {
    def response(result: String): Response = ContainersResponse(result, this)
  }

  case class Start(id: String) extends Request {
    def response(result: Boolean): Response = StartResponse(result, this)
  }

  case class Stop(id: String) extends Request {
    def response(result: Boolean): Response = StopResponse(result, this)
  }

  sealed trait Response {
    def request: Request
  }

  case class InfoResponse(result: String, request: Request) extends Response
  case class ImagesResponse(result: String, request: Request) extends Response
  case class ContainersResponse(result: String, request: Request) extends Response
  case class StartResponse(result: Boolean, request: Request) extends Response
  case class StopResponse(result: Boolean, request: Request) extends Response

  // Request reads & writes

  implicit val dockerWsInfoReads: Reads[Info.type] = extractRequest[Info.type](requestTag)(extractTypeOnly("info", Info))
  implicit val dockerWsInfoWrites: Writes[Info.type] = emitRequest(requestTag)(_ => Json.obj("type" -> "info"))

  implicit val dockerWsImagesReads: Reads[Images.type] = extractRequest[Images.type](requestTag)(extractTypeOnly("images", Images))
  implicit val dockerWsImagesWrites: Writes[Images.type] = emitRequest(requestTag)(_ => Json.obj("type" -> "images"))

  implicit val dockerWsContainersReads: Reads[Containers] = extractRequest[Containers](requestTag)(extractType("containers")((__ \ "filter").read[Filter].map(Containers)))
  implicit val dockerWsContainersWrites: Writes[Containers] = emitRequest(requestTag)(in => Json.obj("type" -> "containers", "filter" -> in.filter))

  implicit val dockerWsStartReads: Reads[Start] = extractRequest[Start](requestTag)(extractType("start")((__ \ "id").read[String].map(Start)))
  implicit val dockerWsStartWrites: Writes[Start] = emitRequest(requestTag)(in => Json.obj("type" -> "start", "id" -> in.id))

  implicit val dockerWsStopReads: Reads[Stop] = extractRequest[Stop](requestTag)(extractType("stop")((__ \ "id").read[String].map(Stop)))
  implicit val dockerWsStopWrites: Writes[Stop] = emitRequest(requestTag)(in => Json.obj("type" -> "stop", "id" -> in.id))

  // EOF request reads & writes

  implicit val dockerWsRequestReads: Reads[Request] = {
    val ifo = dockerWsInfoReads.asInstanceOf[Reads[Request]]
    val igs = dockerWsImagesReads.asInstanceOf[Reads[Request]]
    val cts = dockerWsContainersReads.asInstanceOf[Reads[Request]]
    val sta = dockerWsStartReads.asInstanceOf[Reads[Request]]
    val sto = dockerWsStopReads.asInstanceOf[Reads[Request]]
    extractRequest[Request](requestTag)(ifo.orElse(igs).orElse(cts).orElse(sta).orElse(sto))
  }
  implicit val dockerWsRequestWrites: Writes[Request] =
    Writes {
      case x @ Info => dockerWsInfoWrites.writes(x)
      case x @ Images => dockerWsImagesWrites.writes(x)
      case x: Containers => dockerWsContainersWrites.writes(x)
      case x: Start => dockerWsStartWrites.writes(x)
      case x: Stop => dockerWsStopWrites.writes(x)
    }

  // Response writes
  implicit val dockerWsInfoResponseWrites: Writes[InfoResponse] =
    emitResponse(responseTag, responseSubTag)(in => Json.obj("event" ->
      Json.obj(
        "type" -> "infoResponse",
        "result" -> in.result,
        "request" -> in.request)))

  implicit val dockerWsImagesResponseWrites: Writes[ImagesResponse] =
    emitResponse(responseTag, responseSubTag)(in => Json.obj("event" ->
      Json.obj(
        "type" -> "imagesResponse",
        "result" -> in.result,
        "request" ->in.request
      )))

  implicit val dockerWsContainersResponseWrites: Writes[ContainersResponse] =
    emitResponse(responseTag, responseSubTag)(in => Json.obj("event" ->
    Json.obj(
      "type" -> "containersResponse",
      "result" -> in.result,
      "request" -> in.request
    )))

  implicit val dockerWsStartResponseWrites: Writes[StartResponse] =
    emitResponse(responseTag, responseSubTag)(in => Json.obj("event" ->
    Json.obj(
      "type" -> "startResponse",
      "result" -> in.result,
      "request" -> in.request
    )))

  implicit val dockerWsStopResponseWrites: Writes[StopResponse] =
    emitResponse(responseTag, responseSubTag)(in => Json.obj("event" ->
      Json.obj(
        "type" -> "stopResponse",
        "result" -> in.result,
        "request" -> in.request
      )))

  implicit val dockerWsResponseWrites: Writes[Response] =
    Writes {
      case x: InfoResponse => dockerWsInfoResponseWrites.writes(x)
      case x: ImagesResponse => dockerWsImagesResponseWrites.writes(x)
      case x: ContainersResponse => dockerWsContainersResponseWrites.writes(x)
      case x: StartResponse => dockerWsStartResponseWrites.writes(x)
      case x: StopResponse => dockerWsStopResponseWrites.writes(x)
    }

  def unapply(in: JsValue): Option[Request] = Json.fromJson[Request](in).asOpt
}
