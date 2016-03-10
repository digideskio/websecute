name := """websecuteDockerManager"""

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.6"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies ++= Seq(
  cache,
  ws,
  specs2 % Test,
  "org.scalatest" % "scalatest_2.11" % "2.2.6",
  "com.typesafe.akka" % "akka-testkit_2.11" % "2.3.13",
  "com.google.code.findbugs" % "jsr305" % "1.3.+",
  "com.github.grantzvolsky" % "docker-java" % "3.0.0-SNAPSHOT",
  "org.webjars" % "bootstrap" % "3.0.0",
  "org.webjars" % "knockout" % "3.4.0",
  "org.webjars" % "requirejs" % "2.1.11-1",
  "org.webjars" % "rjs" % "2.1.11-1-trireme" % "test"
)

libraryDependencies ++= Seq(
  //"com.h2database" % "h2" % "1.0.60",
  "mysql" % "mysql-connector-java" % "5.1.34",
  "com.typesafe.play" %% "play-slick" % "1.1.1",
  "com.typesafe.play" %% "play-slick-evolutions" % "1.1.1"
)

pipelineStages := Seq(rjs, digest, gzip)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
// Sources at https://github.com/grantzvolsky
resolvers += "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"
resolvers += "Typesafe Releases" at "https://repo.typesafe.com/typesafe/releases/"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator


fork in run := true