name := "Ndb"

organization := "com.nossin"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.11.11"

scalacOptions ++= Seq("-unchecked", "-deprecation")

resolvers ++= Seq(
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
)


libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.2",
  "com.typesafe.akka" %% "akka-slf4j" % "2.5.2",
  "com.typesafe.akka" %% "akka-testkit" % "2.5.2",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "org.scalatest" %% "scalatest" % "3.0.1"
)