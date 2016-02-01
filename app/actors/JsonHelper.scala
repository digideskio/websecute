/**
  * Copyright (C) 2013 Typesafe <http://typesafe.com/>
  * Source: https://github.com/typesafehub/activator/blob/889970aab1f990cc477e4a9e1b3bab6b1897acef/ui/app/activator/JsonHelper.scala
  * Provided under Apache License 2.0
  */
package actors

/** Helper methods to convert between JSON libraries. */
object JsonHelper {
  import java.io._

  import play.api.data.validation.ValidationError
  import play.api.libs.functional.syntax._
  import play.api.libs.json.Json._
  import play.api.libs.json.Reads._
  import play.api.libs.json.Writes._
  import play.api.libs.json._

  implicit object FileWrites extends Writes[File] {
    def writes(file: File) = JsString(file.getPath)
  }

  implicit object FileReads extends Reads[File] {
    def reads(json: JsValue) = json match {
      case JsString(path) => JsSuccess(new File(path))
      case _ => JsError(Seq(JsPath() -> Seq(ValidationError("validate.error.expected.jsstring"))))
    }
  }

  def extractTagged[T](key: String, tag: String)(reads: Reads[T]): Reads[T] =
    (__ \ key).read[String](pattern(tag.r)) ~> reads

  def extractRequest[T](tag: String)(reads: Reads[T]): Reads[T] =
    extractTagged("request", tag)(reads)

  def extractMessage[T](tag: String)(reads: Reads[T]): Reads[T] =
    extractTagged("tag", tag)(reads)

  def emitMessage[T](tag: String)(bodyFunc: T => JsObject): Writes[T] =
    emitTagged("tag", tag)(bodyFunc)

  def extractResponse[T](tag: String)(reads: Reads[T]): Reads[T] =
    extractTagged("response", tag)(reads)

  def emitTagged[T](key: String, tag: String)(bodyFunc: T => JsObject): Writes[T] = new Writes[T] {
    def writes(in: T): JsValue =
      Json.obj(key -> tag) ++ bodyFunc(in)
  }

  def emitTagged[T](tagKey: String, tag: String, subTagKey: String, subTag: String)(bodyFunc: T => JsObject): Writes[T] = new Writes[T] {
    def writes(in: T): JsValue =
      Json.obj(tagKey -> tag) ++ Json.obj(subTagKey -> subTag) ++ bodyFunc(in)
  }

  def emitRequest[T](tag: String)(bodyFunc: T => JsObject): Writes[T] =
    emitTagged("request", tag)(bodyFunc)

  def emitResponse[T](tag: String, subTag: String)(bodyFunc: T => JsObject): Writes[T] =
    emitTagged("type", tag, "subtype", subTag)(bodyFunc)
}
