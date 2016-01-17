package services

import actors.ClientConnection
import actors.DockerClientProtocol._
import actors.DockerClientSupervisor
import akka.actor.{Props, ActorSystem}
import akka.testkit.{DefaultTimeout, ImplicitSender, TestKit}
import com.typesafe.config.ConfigFactory
import org.scalatest.{MustMatchers, WordSpecLike}

class DockerServiceSpec extends TestKit(ActorSystem("testDockerService",
  ConfigFactory.parseString(DockerServiceSpec.config)))
  with WordSpecLike
  with MustMatchers
  with ImplicitSender
  with DefaultTimeout
  with StopSystemAfterAll {
  "The DockerClient" must {
    val dockerClientSupervisor = system.actorOf(Props[DockerClientSupervisor])
    /*"process requests asynchronously" in {
      dockerClientSupervisor ! GetInfo
      dockerClientSupervisor ! GetImages
      dockerClientSupervisor ! GetInfo
      dockerClientSupervisor ! GetImages
      dockerClientSupervisor ! GetInfo
      dockerClientSupervisor ! GetImages
      dockerClientSupervisor ! GetInfo
      dockerClientSupervisor ! GetImages
      val results = receiveWhile() {
        case GetInfoRes(id) if id.charAt(0) == '{' => id
      }

      /*val results = receiveN(8, 5 seconds) {
        case GetInfoRes(m: String) => m
      }*/
      results must be (List(2,3))
    }*/
    "get information from the Docker node" in {
      dockerClientSupervisor ! GetInfo
      expectMsgPF() {
        case GetInfoRes(res: String) => res must include ("kernelVersion")
      }
    }
    "get images from the Docker node" in {
      dockerClientSupervisor ! GetImages
      expectMsgPF() {
        case GetImagesRes(res: String) => res must include ("parentId")
      }
    }
    "get containers from the Docker node" in {
      dockerClientSupervisor ! GetContainers
      expectMsgPF() {
        case GetContainersRes(res: String) => res must include ("ports")
      }
    }
  }
}

object DockerServiceSpec {
  val config =
    """
      |akka {
      |  loglevel = "DEBUG"
      |  actor {
      |    debug {
      |      receive = on
      |      autoreceive = on
      |      lifecycle = off
      |    }
      |  }
      |}
    """.stripMargin
}