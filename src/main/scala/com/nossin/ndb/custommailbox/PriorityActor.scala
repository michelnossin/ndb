package com.nossin.ndb.custommailbox

import akka.actor.Actor

/**
  * Created by michelnossin on 03-06-17.
  */
class PriorityActor extends Actor{
    def receive: PartialFunction[Any, Unit] = {
      // Int Messages
      case x: Int => println(x)
      // String Messages
      case x: String => println(x)
      // Long messages
      case x: Long => println(x)
      // other messages
      case x => println(x)
    }
  }

