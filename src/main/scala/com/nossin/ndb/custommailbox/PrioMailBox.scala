package com.nossin.ndb.custommailbox

import akka.actor.ActorSystem
import akka.dispatch.{PriorityGenerator, UnboundedPriorityMailbox}
import com.typesafe.config.Config

/**
  * Created by michelnossin on 03-06-17.
  */
class PrioMailbox(settings: ActorSystem.Settings, config: Config) extends UnboundedPriorityMailbox (
    // Create a new PriorityGenerator, lower prio means more important
      PriorityGenerator {
      // Int Messages
      case x: Int => 1
      // String Messages
      case x: String => 0
      // Long messages
      case x: Long => 2
      // other messages
      case _ => 3
    }
  )