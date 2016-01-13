package controllers

import javax.inject.{Singleton, Inject}

import actors.ClientConnection.ClientEvent
import actors.{DockerClientSupervisor, ClientConnection}
import akka.actor._
import play.api.libs.json.JsValue
import play.api.mvc._
import play.api.Play.current

@Singleton
class Application @Inject() (system: ActorSystem) extends Controller {
  val dockerClientSupervisorProps = Props[DockerClientSupervisor]
  val topLevelActor = system.actorOf(dockerClientSupervisorProps)

  def index = Action { implicit req =>
    Ok(views.html.index("Your new application is ready."))
  }

  def stream(email: String) = WebSocket.acceptWithActor[ClientEvent, ClientEvent] { _ => upstream =>
    ClientConnection.props(topLevelActor, "anonymous@google.com", upstream)
  }
}
