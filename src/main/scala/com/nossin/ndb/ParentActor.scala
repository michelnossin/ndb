package com.nossin.ndb

import akka.actor.{Actor, Props}
import com.nossin.ndb.messages.Messages.{CreateChild, Greet}

class ParentActor extends Actor {
  def receive = {
    case CreateChild  =>
      val child = context.actorOf(Props[ChildActor], "child")
      child ! Greet("Hello Child")
  }
}