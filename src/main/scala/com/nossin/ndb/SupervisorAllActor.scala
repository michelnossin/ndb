package com.nossin.ndb

import akka.actor.{Actor, AllForOneStrategy, Props}
import akka.actor.SupervisorStrategy.{Escalate, Restart, Resume, Stop}
import com.nossin.ndb.messages.Calculator.{Div, Sub}
//import com.nossin.ndb.messages.Calculator.{Add, Div, Sub}
import scala.concurrent.duration._

/**
  * Created by michelnossin on 05-06-17.
  */
class SupervisorAllActor extends Actor{
    override val supervisorStrategy =
      AllForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 seconds)
      {
        case _: ArithmeticException => Restart
        case _: NullPointerException => Resume
        case _: IllegalArgumentException => Stop
        case _: Exception => Escalate

      }
    val printer = context.actorOf(Props[ResultPrinterActor])
    val calculator =  context.actorOf(Props(classOf[CalculatorActor], printer)) //The all supervisor strategy will also restart printer!
    def receive = {
      case "Start" => calculator ! com.nossin.ndb.messages.Calculator.Add(10, 12)
        calculator ! Sub(12, 10)
        calculator ! Div(5, 2)
        calculator ! Div(5, 0)
    }
}
