package com.nossin.ndb

import akka.actor.Actor
import com.nossin.ndb.messages.Messages.Service

/**
  * Created by michelnossin on 05-06-17.
  */
  class ServiceActor extends Actor {
    def receive = {
      case Service => println("I provide a special service")
    }
}
