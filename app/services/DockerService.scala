package services

import actors.DockerWSRequest.Filter
import actors.DockerActor._
import com.github.dockerjava.api.model.Filters
import com.github.dockerjava.core.{DockerClientBuilder, DockerClientConfig}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, blocking}

/**
  * This service handles communication with the Docker API using the docker-java library.
  * */
case class DockerService(dockerClientConfig: DockerClientConfig) extends DockerServiceCalls {
  val docker = DockerClientBuilder.getInstance(dockerClientConfig).build()

  override def getInfo: Future[InternalResponse] = Future {
    blocking {
      InternalInfo.response(docker.infoCmd().exec().toString)
    }
  }

  override def getImages: Future[InternalResponse] = Future {
    blocking {
      InternalImages.response(docker.listImagesCmd().exec().toString)
    }
  }

  override def getContainers(filter: Filter): Future[InternalResponse] = Future {
    blocking {
      if (filter.key == "") InternalContainers(filter).response(docker.listContainersCmd().withShowAll(true).exec().toString)
      else InternalContainers(filter).response(docker.listContainersCmd().withFilters(new Filters().withFilter(filter.key, filter.value)).exec().toString)
    }
  }

  override def startContainer(id: String): Future[InternalResponse] = Future {
    blocking {
      docker.startContainerCmd(id).exec()
      InternalStart(id).response(true) // TODO exception handling
    }
  }

  override def stopContainer(id: String): Future[InternalResponse] = Future {
    blocking {
      docker.stopContainerCmd(id).exec()
      InternalStop(id).response(true) // TODO exception handling
    }
  }
}

trait DockerServiceCalls {
  def getInfo: Future[InternalResponse]

  def getImages: Future[InternalResponse]

  def getContainers(filter: Filter): Future[InternalResponse]

  def startContainer(id: String): Future[InternalResponse]

  def stopContainer(id: String): Future[InternalResponse]
}