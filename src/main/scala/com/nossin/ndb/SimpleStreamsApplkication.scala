package com.nossin.ndb

/**
  * Created by michelnossin on 16-06-17.
  */
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}

object SimpleStreamsApplication extends App {
  implicit val actorSystem = ActorSystem("SimpleStream")
  implicit val actorMaterializer = ActorMaterializer() //Creates the underlying actors implicitely

  val fileList = List("src/main/resources/testfile1.text",
    "src/main/resources/testfile2.txt",
    "src/main/resources/testfile3.txt")

  val stream = Source(fileList)
    .map(new String(_))
    .filter(_.length() != 0)
    .to(Sink.foreach(f => println(s"Absolute path: ${f}")))
  stream.run()
}
