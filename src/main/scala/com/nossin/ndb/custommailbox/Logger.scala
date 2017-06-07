package com.nossin.ndb.custommailbox

import akka.actor.{Actor, ActorLogging}
import com.nossin.ndb.messages.CustomControlMessage

/**
  * Created by michelnossin on 03-06-17.
  */
class Logger extends Actor with ActorLogging {
  def receive = {
    case CustomControlMessage => {
      println("Oh, I have to process Control message first")

    }
    case x => {
      println(x.toString)
      log.warning(s"i don't know what are you talking about : ${x}")
    }
  }
}
