package com.nossin.ndb

import akka.actor.SupervisorStrategy.{Escalate, Restart}
import akka.actor.{Actor, OneForOneStrategy, Props}
import com.nossin.ndb.messages.Messages.StopActor
import scala.concurrent.duration._
/**
  * Created by michelnossin on 05-06-17.
  */
class SupervisorActor extends Actor {
  override val supervisorStrategy = OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute)
    {
      case _: ArithmeticException => Restart
      case t =>
        super.supervisorStrategy.decider.applyOrElse(t, (_: Any) => Escalate)
    }
  def receive = {
    case (props: Props, name: String) => sender ! context.actorOf(props, name)
    case StopActor(actorRef) => context.stop(actorRef)
  }
}
