package com.nossin.ndb

import akka.actor.{Actor, ActorRef, Props}
import com.nossin.ndb.messages.Messages.{CreateChild, DoubleValue, Response, Send}
/**
  * Created by michelnossin on 05-06-17.
  */
class BigParentActor extends Actor {
  val random = new scala.util.Random
  var childs = scala.collection.mutable.ListBuffer[ActorRef]()
  def receive = {
    case CreateChild =>
      childs ++= List(context.actorOf(Props[DoubleActor]))
    case Send =>
      println(s"Sending messages to child")
      childs.zipWithIndex map { case (child, value) => child ! DoubleValue(random.nextInt(10)) }
    case Response(x) => println(s"Parent: Response from child ${sender.path.name} is $x")
  }
}
