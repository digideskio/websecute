package services

import actors.DockerClientProtocol._
import com.github.dockerjava.core.{DockerClientBuilder, DockerClientConfig}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, blocking}

/**
  * This service handles communication with the Docker API using the docker-java library.
  * */
case class DockerService(dockerClientConfig: DockerClientConfig) extends DockerServiceCalls {

  val docker = DockerClientBuilder.getInstance(dockerClientConfig).build()

  override def getInfo: Future[GetInfoRes] = Future {
    blocking {
      GetInfoRes(docker.infoCmd().exec().toString)
    }
  }

  override def getImages: Future[GetImagesRes] = Future {
    blocking {
      GetImagesRes(docker.listImagesCmd().exec().toString)
    }
  }

  override def getContainers: Future[GetContainersRes] = Future {
    blocking {
      GetContainersRes(docker.listContainersCmd().withShowAll(true).exec().toString)
    }
  }

  override def startContainer(id: String): Future[StartContainerRes] = Future {
    blocking {
      docker.startContainerCmd(id).exec()
      StartContainerRes(id)
    }
  }

  override def stopContainer(id: String): Future[StopContainerRes] = Future {
    blocking {
      docker.stopContainerCmd(id).exec()
      StopContainerRes(id)
    }
  }
}

trait DockerServiceCalls {
  def getInfo: Future[GetInfoRes]

  def getImages: Future[GetImagesRes]

  def getContainers: Future[GetContainersRes]

  def startContainer(id: String): Future[StartContainerRes]

  def stopContainer(id: String): Future[StopContainerRes]
}