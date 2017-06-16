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

//akka persistence icw leveldb , local db
libraryDependencies += "com.typesafe.akka" % "akka-persistence_2.11" % "2.4.17"
libraryDependencies += "org.iq80.leveldb"  % "leveldb" % "0.7"
libraryDependencies += "org.fusesource.leveldbjni"  % "leveldbjni-all"   % "1.8"

//akka persistence with  for redis:
//resolvers += Resolver.jcenterRepo
//libraryDependencies += "com.hootsuite" %% "akka-persistence-redis" % "0.6.0"

libraryDependencies +=  "com.typesafe.akka" %% "akka-persistence-query-experimental" % "2.4.17"

//libraryDependencies +=  "com.typesafe.akka" %% "akka-remote_2.11" % "2.4.17"
libraryDependencies += "com.typesafe.akka" %% "akka-remote" % "2.4.17"

libraryDependencies += "com.typesafe.akka" % "akka-cluster-tools_2.11" % "2.4.17"

libraryDependencies += "com.typesafe.akka" % "akka-cluster-sharding_2.11" % "2.4.17"
libraryDependencies += "com.typesafe.akka" % "akka-distributed-data-experimental_2.11" % "2.4.17"

libraryDependencies += "com.typesafe.akka" % "akka-stream_2.11" % "2.4.17"