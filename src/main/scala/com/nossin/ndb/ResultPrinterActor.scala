package com.nossin.ndb

import akka.actor.Actor

/**
  * Created by michelnossin on 05-06-17.
  */
class ResultPrinterActor extends Actor {
  override def preRestart(reason: Throwable, message:
  Option[Any]): Unit = {
    println("Printer : I am restarting as well")
  }
  def receive = {
    case msg:String => println(s"message ${msg}")
    case showAddress => println(s"I have been created at ${self.path.address.hostPort}")
  }
}