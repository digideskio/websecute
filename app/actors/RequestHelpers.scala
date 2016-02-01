/**
  * Copyright (C) 2013 Typesafe <http://typesafe.com/>
  * Source: https://github.com/typesafehub/activator/blob/889970aab1f990cc477e4a9e1b3bab6b1897acef/ui/app/activator/RequestHelpers.scala
  * Provided under Apache License 2.0
  */
package actors

import play.api.libs.json._

object RequestHelpers {
  import JsonHelper._

  def extractTypeOnly[T](typeName: String, value: T): Reads[T] =
    extractTagged("type", typeName)(Reads[T](_ => JsSuccess(value)))

  def extractType[T](typeName: String)(reads: Reads[T]): Reads[T] =
    extractTagged("type", typeName)(reads)
}
