package com.nossin.ndb
import akka.actor.{ActorSystem, Props}

import scala.concurrent.Await
import akka.pattern.ask

import scala.concurrent.duration._
import akka.util.Timeout
import com.nossin.ndb.custommailbox.ProxyActor
import com.nossin.ndb.messages.Messages.Start

object NdbControl extends App {
	val actorSystem = ActorSystem("NdbControl")
	println(actorSystem)

  //Call Actor which prints stuff:w
	val sumActor = actorSystem.actorOf(Props(classOf[SumActor]),"SumActor")
	sumActor ! 3
  sumActor ! 6
  print ("This actor lives here: " + sumActor.path)

  //Call Actor which returns to sender
  implicit val timeout = Timeout(5 seconds)
  val FibActor = actorSystem.actorOf(Props[FibonacciActor])
  // asking for result from actor via Ask , which uses Futures , eventhandlers
  val future = (FibActor? 10).mapTo[Int]
  val fiboacciNumber = Await.result(future, 10 seconds)
  println(fiboacciNumber)

  //Communcate actor to actor
  val randomNumberGenerator = actorSystem.actorOf(Props[RandomNumberGeneratorActor], "randomNumberGeneratorActor")
  val queryActor = actorSystem.actorOf(Props[QueryActor], "queryActor")
  queryActor ! Start(randomNumberGenerator)

  //Custom mailbox, preventing actor with wrong id.
  val proxyActor = actorSystem.actorOf(Props[ProxyActor].withDispatcher("custom-dispatcher"))
  val actor1 = actorSystem.actorOf(Props[SumActor],"xyz")
  val actor2 = actorSystem.actorOf(Props[SumActor],"MyActor")
  print("Start custom mailbox")
  actor1 !  (3,proxyActor)
  actor2 !  (5,proxyActor)

} 
