package com.nossin.ndb
import akka.actor.{Actor, ActorRef}
import com.nossin.ndb.messages.Messages.Cancel
import akka.actor.{Cancellable, Props, ActorSystem, Actor}
import akka.actor.ActorSystem

class SumActor extends Actor {
  // state inside the actor
  var sum = 0
  // behaviour which is applied on the state
  override def receive: Receive = {
    // receives message an integer
    case x: Int => sum = sum + x
      println(s"my state as sum is $sum")
    case (msg: Int, actorRef: ActorRef) => {
      println("sending " + msg )
      actorRef ! msg
    }
    case msg: String => println("received message as string: " + msg)
    // receives default message
    //case Cancel => schedule.cancellable.cancel()
    case _ => println("I don't know what to do with this")
  }
}