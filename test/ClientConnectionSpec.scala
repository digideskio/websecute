package services

import actors.ClientConnection
import actors.DockerWSRequest.Filter
import scala.concurrent.duration._
import play.api.libs.json._
import actors.DockerClientSupervisor
import actors.DockerWSRequest._
import akka.actor.{Props, ActorSystem}
import actors.JsonHelper._
import akka.testkit.{CallingThreadDispatcher, DefaultTimeout, ImplicitSender, TestKit}
import com.typesafe.config.ConfigFactory
import org.scalatest.{MustMatchers, WordSpecLike}

class ClientConnectionSpec extends TestKit(ActorSystem("testClientConnection",
  ConfigFactory.parseString(ClientConnectionSpec.config)))
  with WordSpecLike
  with MustMatchers
  with ImplicitSender
  with DefaultTimeout
  with StopSystemAfterAll {
  "The ClientConnection" must {
    val dockerClientSupervisorProps = Props[DockerClientSupervisor]
    val topLevelActor = system.actorOf(dockerClientSupervisorProps)
    val clientConnectionProps = Props(classOf[ClientConnection], topLevelActor, "example@gmail.com", testActor)
    val clientConnection = system.actorOf(clientConnectionProps)

    "process requests asynchronously" in {
      (0 until 50).foreach { i =>
        clientConnection ! Json.toJson(actors.DockerWSRequest.Info)
        clientConnection ! Json.toJson(actors.DockerWSRequest.Images)
      }
      val t1 = System.currentTimeMillis()
      val results = receiveN(100, 10 seconds).map {
        case r: JsValue =>
          val typeReads = (__ \ "event" \ "type").read[String]
          r.validate[String](typeReads) match {
            case s: JsSuccess[String] =>
              s.get match {
                case "imagesResponse" => "0"
                case "infoResponse" => "1"
              }
            case f: JsError => alert("Failed to parse JSON.")
          }
        case other => "Received something else than a JsValue."
      }
      val t2 = System.currentTimeMillis()
      info(results.toString())
      results must have length 100
      info("Time: " + (t2 - t1).toFloat/1000)
    }
    "get information from the Docker node" in {
      clientConnection ! Json.toJson(actors.DockerWSRequest.Info)
      expectMsgPF() {
        case r => r.toString must include ("kernelVersion")
      }
    }
    "get images from the Docker node" in {
      clientConnection ! Json.toJson(actors.DockerWSRequest.Images)
      expectMsgPF() {
        case r => r.toString must include ("parentId")
      }
    }
    "get containers from the Docker node" in {
      clientConnection ! Json.toJson(actors.DockerWSRequest.Containers(Filter("", "")))
      expectMsgPF() {
        case r => r.toString must include ("ports")
      }
    }
  }
}

object ClientConnectionSpec {
  val config =
    """
      |akka {
      |  loglevel = "DEBUG"
      |  actor {
      |    debug {
      |      receive = off
      |      autoreceive = on
      |      lifecycle = off
      |    }
      |  }
      |}
    """.stripMargin
}