// Source: https://github.com/sbrunk/play-silhouette-slick-seed/blob/8a1fb0776807d888ea03ee35442a7be168a59329/app/models/daos/DAOSlick.scala
package model.daos

import model.slick.TableDefinitions
import slick.driver.JdbcProfile
import play.api.db.slick.HasDatabaseConfigProvider

/**
  * Trait that contains generic slick db handling code to be mixed in with DAOs
  */
trait DAOSlick extends TableDefinitions with HasDatabaseConfigProvider[JdbcProfile]