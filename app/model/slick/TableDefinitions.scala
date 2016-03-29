package model.slick

import java.sql.Timestamp

import slick.driver.JdbcProfile
import slick.lifted.ProvenShape.proveShapeOf

trait TableDefinitions {
  protected val driver: JdbcProfile
  import driver.api._

  case class Host(url: String, status: String) // TODO status should extend Enumerable

  class Hosts(tag: Tag) extends Table[Host](tag, "hosts") {
    def url = column[String]("url", O.PrimaryKey)
    def status = column[String]("status")
    def * = (url, status) <> (Host.tupled, Host.unapply)
  }
  val hosts = TableQuery[Hosts]
}
