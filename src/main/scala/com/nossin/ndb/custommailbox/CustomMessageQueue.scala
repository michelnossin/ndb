package com.nossin.ndb.custommailbox
import java.util.concurrent.ConcurrentLinkedQueue

import akka.actor.ActorRef
import akka.dispatch.{Envelope, MessageQueue}

/**
  * Created by michelnossin on 03-06-17.
  */

class CustomMessageQueue extends MessageQueue {
  private final val queue = new
      ConcurrentLinkedQueue[Envelope]()
  // these should be implemented; queue used as example
  def enqueue(receiver: ActorRef, handle: Envelope): Unit =
  {

    if(handle.sender.path.name == "MyActor") {
      println("in enqueue, actor identified ")
      handle.sender !  "Hey dude, How are you?, I Know your name,processing your request"
        queue.offer(handle)
    }
    else {
      println("in enqueue, unknown actor")
      handle.sender ! "I don't talk to strangers, Ibcan't process your request"
    }
  }

  def dequeue(): Envelope = queue.poll
  def numberOfMessages: Int = queue.size
  def hasMessages: Boolean = !queue.isEmpty
  def cleanUp(owner: ActorRef, deadLetters: MessageQueue) {
    while (hasMessages) {
        deadLetters.enqueue(owner, dequeue())
    }
  }
}