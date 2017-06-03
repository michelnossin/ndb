package com.nossin.ndb
import akka.actor.{Actor, Status}
import com.nossin.ndb.messages.Messages.{Done, GiveMeRandomNumber, Start}
/**
  * Created by michelnossin on 02-06-17.
  */
class QueryActor extends Actor {

  override def receive: Receive = {
    case Start(actorRef) => println(s"send me the next random number")
      actorRef ! GiveMeRandomNumber
    case Done(randomNumber) =>
      println(s"received a random number $randomNumber")
  }
}
