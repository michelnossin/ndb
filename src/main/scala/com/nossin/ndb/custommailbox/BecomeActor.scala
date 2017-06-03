package com.nossin.ndb.custommailbox

import akka.actor.Actor
import com.nossin.ndb.messages.Stop


/**
  * Created by michelnossin on 03-06-17.
  */
class BecomeActor extends Actor {
  def receive: Receive = {
    case true => context.become(isStateTrue)
    case false => context.become(isStateFalse)
    case _ => println("don't know what you want to say !! ")
    case Stop => context.stop(self)
  }
  def isStateTrue: Receive  = {
    case msg : String => println(s"$msg")
    case false => context.become(isStateFalse)
  }
  def isStateFalse: Receive  = {
    case msg : Int => println(s"$msg")
    case true =>  context.become(isStateTrue)
  }
}