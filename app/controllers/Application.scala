package controllers

import javax.inject.{Singleton, Inject}

import actors.ClientConnection.ClientEvent
import actors.{DockerClientSupervisor, ClientConnection}
import akka.actor._
import com.github.dockerjava.api.model.Container
import com.github.dockerjava.core.DockerClientConfig
import play.api.libs.json.JsValue
import play.api.mvc._
import play.api.Play.current
import services.DockerService

import scala.concurrent.Future
import scala.collection.mutable.Buffer
import scala.concurrent.ExecutionContext.Implicits.global // TODO remove and move API to a separate controller

@Singleton
class Application @Inject() (system: ActorSystem) extends Controller {
  val dockerClientSupervisorProps = Props[DockerClientSupervisor]
  val topLevelActor = system.actorOf(dockerClientSupervisorProps)

  val config = DockerClientConfig.createDefaultConfigBuilder()
    .withVersion("1.17")
    .withUri("http://127.0.0.1:4243")
    .build()

  val docker = new DockerService(config)

  def index = Action { implicit req =>
    Ok(views.html.index("Your new application is ready."))
  }

  def containers = Action { implicit req =>
    Ok(views.html.containers())
  }

  def getContainers = Action.async { implicit request =>
    docker.getContainers.map { containers => Ok(containers unapply)}
  }

  def stream(email: String) = WebSocket.acceptWithActor[ClientEvent, ClientEvent] { _ => upstream =>
    ClientConnection.props(topLevelActor, "anonymous@google.com", upstream)
  }
}
