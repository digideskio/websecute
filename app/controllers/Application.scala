package controllers

import javax.inject.{Inject, Singleton}

import actors.ClientConnection.ClientEvent
import actors.{ClientConnection, DockerClientSupervisor}
import akka.actor._
import play.api.Play.current
import play.api.mvc._

@Singleton
class Application @Inject() (system: ActorSystem) extends Controller {
  val dockerClientSupervisorProps = Props[DockerClientSupervisor]
  val topLevelActor = system.actorOf(dockerClientSupervisorProps)

  def index = Action { implicit req =>
    Ok(views.html.index("Your new application is ready."))
  }

  def containers = Action { implicit req =>
    Ok(views.html.containers())
  }

  def stream(email: String) = WebSocket.acceptWithActor[ClientEvent, ClientEvent] { _ => upstream =>
    ClientConnection.props(topLevelActor, "anonymous@google.com", upstream)
  }
}
