package com.nossin.ndb

import akka.actor.Actor
import com.nossin.ndb.messages.Messages.Greet

/**
  * Created by michelnossin on 04-06-17.
  */
class ChildActor extends Actor {
  def receive = {
    case Greet(msg) => println(s"My parent[${self.path.parent}] greeted to me [${self.path}] $msg")
  }
}