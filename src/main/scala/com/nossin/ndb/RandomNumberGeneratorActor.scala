package com.nossin.ndb
import akka.actor.Actor
import com.nossin.ndb.messages.Messages.{Done, GiveMeRandomNumber}

import scala.util.Random._
/**
  * Created by michelnossin on 02-06-17.
  */
class RandomNumberGeneratorActor extends Actor {

  override def receive: Receive = {
    case GiveMeRandomNumber =>
      println("received a message to generate a random integer")
        val randomNumber = nextInt
        sender ! Done(randomNumber)
  }
}