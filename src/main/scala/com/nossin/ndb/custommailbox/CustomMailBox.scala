package com.nossin.ndb.custommailbox

import akka.actor.{ActorRef, ActorSystem}

import akka.dispatch.{MailboxType, ProducesMessageQueue}
import com.typesafe.config.Config
import akka.dispatch.MessageQueue
//import sun.jvm.hotspot.utilities.MessageQueue

/**
  * Created by michelnossin on 03-06-17.
  */
class CustomMailBox extends MailboxType with ProducesMessageQueue[CustomMessageQueue] {
  def this(settings: ActorSystem.Settings,config: Config) = {
    this()
    print("in custom mailbox")
  }
  // The create method is called to create the MessageQueue
  final override def create(owner: Option[ActorRef], system: Option[ActorSystem]): MessageQueue = new CustomMessageQueue()
}