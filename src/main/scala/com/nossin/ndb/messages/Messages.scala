package com.nossin.ndb.messages

import akka.actor.{ActorRef, Status}
/**
  * Created by michelnossin on 02-06-17.
  */
object Messages {
  case class Done(randomNumber: Int)
  case object GiveMeRandomNumber
  case class Start(actorRef: ActorRef)
  case object CreateChild
  case class Greet(msg: String)
  case object Error
  case class DoubleValue(x: Int)
  case class StopActor(actorRef: ActorRef)
  case object Send
  case class Response(x: Int)
}
