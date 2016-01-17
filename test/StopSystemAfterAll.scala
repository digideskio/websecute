/**
  * From Akka in Action by Ray Roestenburg
  * https://github.com/RayRoestenburg/akka-in-action
  */

package services

import org.scalatest.Suite
import org.scalatest.BeforeAndAfterAll
import akka.testkit.TestKit

trait StopSystemAfterAll extends BeforeAndAfterAll {
  this: TestKit with Suite =>
  override protected def afterAll() {
    super.afterAll()
    system.shutdown()
  }
}