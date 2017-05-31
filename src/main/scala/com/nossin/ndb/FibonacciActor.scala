package com.nossin.ndb
import akka.actor.Actor

class FibonacciActor extends Actor {
  override def receive: Receive = {
    case num : Int =>
      val fibonacciNumber = fib(num)
      sender ! fibonacciNumber
  }
  def fib( n : Int) : Int = n match {
    case 0 | 1 => n
    case _ => fib( n-1 ) + fib( n-2 )
  }
}