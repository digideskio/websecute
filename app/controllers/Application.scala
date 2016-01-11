package controllers

import javax.inject.Inject

import actors.ClientConnection
import akka.actor.Props
import play.api.libs.json.JsValue
import play.api.mvc._
import play.api.Play.current

class Application extends Controller {
  def index = Action { implicit req =>
    Ok(views.html.index("Your new application is ready."))
  }

  def stream(email: String) = WebSocket.acceptWithActor[JsValue, JsValue] { _ => upstream =>
    ClientConnection.props("anonymous@google.com", upstream)
  }
}
