package com.nossin.ndb.custommailbox

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorRef}

/**
  * Created by michelnossin on 03-06-17.
  */
class ProxyActor extends Actor {
  override def receive: Receive = {
    case msg => println("A correct msg in proxy " + msg)
    case _ => println ("invalid msg in proxy")
  }
}