package com.nossin.ndb

import akka.actor.{Actor, ActorRef}
import com.nossin.ndb.messages.Calculator.{Add, Div, Sub}

/**
  * Created by michelnossin on 05-06-17.
  */
class CalculatorActor(printer: ActorRef) extends Actor {
  override def preRestart(reason: Throwable,
                          message: Option[Any]): Unit = {
      println("Calculator : I am restarting because of ArithmeticException")
        }
  def receive = {
    case com.nossin.ndb.messages.Calculator.Add(a, b) => printer ! s"sum is ${a + b}"
    case com.nossin.ndb.messages.Calculator.Sub(a, b) => printer ! s"diff is ${a - b}"
    case com.nossin.ndb.messages.Calculator.Div(a, b) => printer ! s"div is ${a / b}" }
}

