package com.nossin.ndb.messages

import akka.actor.{ActorRef, Status}
/**
  * Created by michelnossin on 02-06-17.
  */
object Messages {
  case class Done(randomNumber: Int)
  case object GiveMeRandomNumber
  case class Start(actorRef: ActorRef)
}
