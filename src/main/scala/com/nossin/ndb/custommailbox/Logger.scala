package com.nossin.ndb.custommailbox

import akka.actor.Actor
import com.nossin.ndb.messages.CustomControlMessage

/**
  * Created by michelnossin on 03-06-17.
  */
class Logger extends Actor {
  def receive = {
    case CustomControlMessage => println("Oh, I have to process Control message first")
      case x => println(x.toString)
      }
}
