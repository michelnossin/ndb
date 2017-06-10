package com.nossin.ndb

/**
  * Created by michelnossin on 10-06-17.
  */
import akka.actor.{ActorSystem, Props}
import com.nossin.ndb.messages.Messages.showAddress


object HelloAkkaRemoting1 extends App {
  val actorSystem = ActorSystem("HelloAkkaRemoting1")
}

object HelloAkkaRemoting2 extends App {
  val actorSystem = ActorSystem("HelloAkkaRemoting2")
  println("Creating actor from HelloAkkaRemoting2")
  val actor = actorSystem.actorOf(Props[ResultPrinterActor], "simpleRemoteActor")
  actor ! showAddress
}
