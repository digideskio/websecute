package services

import actors.ClientConnection
import scala.concurrent.duration._
import actors.DockerClientProtocol._
import actors.DockerClientSupervisor
import akka.actor.{Props, ActorSystem}
import akka.testkit.{CallingThreadDispatcher, DefaultTimeout, ImplicitSender, TestKit}
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
    "process requests asynchronously" in {
      (0 until 50).foreach { i =>
        dockerClientSupervisor ! GetInfo
        dockerClientSupervisor ! GetImages
      }
      val t1 = System.currentTimeMillis()
      val results = receiveN(100, 10 seconds).map {
        case GetInfoRes(m: String) => GetInfo
        case GetImagesRes(m: String) => GetImages
      }
      val t2 = System.currentTimeMillis()
      info(results.toString())
      results must have length 100
      info("Time: " + (t2 - t1).toFloat/1000)
    }
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