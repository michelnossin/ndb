package com.nossin.ndb

import akka.actor.Actor
import com.nossin.ndb.messages.Messages.{DoubleValue, Response}

/**
  * Created by michelnossin on 05-06-17.
  */
class DoubleActor extends Actor {
  def receive = {
    case DoubleValue(number) =>
      println(s"${self.path.name} Got the number $number")
      sender ! Response(number * 2)
  }
}