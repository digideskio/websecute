package model.daos

import javax.inject.Inject

import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.Future

class HostDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends DAOSlick {
  import driver.api._

  def list(): Future[Seq[String]] = {
    val q = hosts.map(_.url)
    val action = q.result
    val result: Future[Seq[String]] = db.run(action)
    result
  }
}
