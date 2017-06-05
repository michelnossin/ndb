package com.nossin.ndb

import akka.actor.{Actor, Props, Terminated}
import com.nossin.ndb.messages.Messages.{Kill, Service}

/**
  * Created by michelnossin on 05-06-17.
  */
class DeathWatchActor extends Actor {
  val child = context.actorOf(Props[ServiceActor], "serviceChildActor")
  Thread.sleep(1000)

  context.watch(child)
  def receive = {
    case Service => child ! Service
    case Kill => context.stop(child)
    case Terminated(`child`) => println("The service actor has terminated and no longer available")  // monitor status
  }
}
