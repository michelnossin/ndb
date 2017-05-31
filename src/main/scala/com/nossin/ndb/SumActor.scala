package com.nossin.ndb
import akka.actor.Actor

class SumActor extends Actor {
  // state inside the actor
  var sum = 0
  // behaviour which is applied on the state
  override def receive: Receive = {
    // receives message an integer
    case x: Int => sum = sum + x
      println(s"my state as sum is $sum")
    // receives default message
    case _ => println("I don't know what")
  }
}