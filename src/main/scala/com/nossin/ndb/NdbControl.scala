package com.nossin.ndb
//import com.nossin.ndb.SupervisorActor
import akka.actor.{ActorRef, ActorSystem, PoisonPill, Props}

import scala.concurrent.Await
import akka.pattern.ask
import akka.routing._

import scala.concurrent.duration._
import akka.util.Timeout
import com.nossin.ndb.custommailbox.{BecomeActor, Logger, PriorityActor, ProxyActor}
import com.nossin.ndb.messages.{CustomControlMessage, Stop}
import com.nossin.ndb.messages.Messages.{CreateChild, Error, Kill, Send, Service, Start, StopActor}

object NdbControl extends App {
	val actorSystem = ActorSystem("NdbControl")
	println(actorSystem)

  //Call Actor which prints stuff:w
	val sumActor = actorSystem.actorOf(Props(classOf[SumActor]),"SumActor")
	sumActor ! 3
  sumActor ! 6
  print ("This actor lives here: " + sumActor.path)

  //Call Actor which returns to sender
  implicit val timeout = Timeout(10 seconds)
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

  //Priomailbox events being processed are run in order determined by our prio mailbox
  val myPriorityActor = actorSystem.actorOf(Props[PriorityActor].withDispatcher("prio-dispatcher"))
  myPriorityActor ! 6.0
  myPriorityActor ! 1
  myPriorityActor ! 5.0
  myPriorityActor ! 3
  myPriorityActor ! "Hello"
  myPriorityActor ! 5
  myPriorityActor ! "I am priority actor"
  myPriorityActor ! "I process string messages first,then integer, long and others"

  //Control aware mailbox, we can prio a particular event
  val logActor = actorSystem.actorOf(Props[Logger].withDispatcher("control-aware-dispatcher"))
  logActor ! "hello"
  logActor ! "how are"
  logActor ! "you?"
  logActor ! CustomControlMessage

  //Become actor, based on state change behavioud, for example let this actor handle string OR numbers
  val becomeUnBecome = actorSystem.actorOf(Props[BecomeActor])
  becomeUnBecome ! true
  becomeUnBecome ! "Hello how are you?"
  becomeUnBecome ! false
  becomeUnBecome ! 1100
  becomeUnBecome ! true
  becomeUnBecome ! "What do u do?"

  //Stop , we build this into the become actor
  becomeUnBecome ! Stop
  becomeUnBecome ! "stil there?"
  //becomeUnBecome ! PoisonPill  //buils in kill message
  //becomeUnBecome ! "not any more"

  //PArent Child actors, parent create child, we call parent
  val parent = actorSystem.actorOf(Props[ParentActor], "parent")
  parent ! CreateChild

  //Supervisor and lifecycle hooks for actors (post/pre sytart or stop)
  //This will show error as expected. It also shows the child gets restarted also after error
  val supervisor = actorSystem.actorOf(Props[SupervisorActor], "supervisor")
  val childFuture = supervisor ? (Props(new LifeCycleActor), "LifeCycleActor")
  val child = Await.result(childFuture.mapTo[ActorRef], 2 seconds)
  child ! Error
  Thread.sleep(1000)
  supervisor ! StopActor(child)

  //Bigparent with multiple childs
  val bigParent = actorSystem.actorOf(Props[BigParentActor], "bigparent")
  bigParent ! CreateChild
  bigParent ! CreateChild
  bigParent ! CreateChild
  bigParent ! Send

  //Test all supervisor, it has another superviso policy which restarts depended childs
  val supervisorAll = actorSystem.actorOf(Props[SupervisorAllActor], "supervisorall")
  supervisorAll ! "Start"

  Thread.sleep(1000)

  //DeathWatchActor ,  check status child and monitor if its terminated
  val deathWatchActor = actorSystem.actorOf(Props[DeathWatchActor],"parentService")
  deathWatchActor ! Service
  //deathWatchActor ! Kill  //it works, but we want the next excersises to also use this actors

  Thread.sleep(1000)
  println("Showing off routers")

  println("smallest pool")
  //Router = group of actor , based on least amount of message in mailbox
  val router1 = actorSystem.actorOf(SmallestMailboxPool(5).props(Props[ServiceActor]))
  for (i <- 1 to 10) {
    router1 ! s"Hello $i"  //Load balances to ServiceActor with least messages
  }
  println("load balance")
  //Same, but also redistributes load if needed.
  val router2 = actorSystem.actorOf(BalancingPool(5).props(Props[ServiceActor]))
  for (i <- 1 to 10) {
    router2 ! s"Hello $i"  //Load balances to ServiceActor with least messages
  }
  println("roundrobin")
  //Round robin (keeps on switching no matter current loa of nr of message
  val router3 = actorSystem.actorOf(RoundRobinPool(5).props(Props[ServiceActor]))
  for (i <- 1 to 10) {
    router3 ! s"Hello $i"  //Load balances to ServiceActor with least messages
  }
  println("Broadcast")
  Thread.sleep(100)
  //Send commands to all actors in the routing group
  val router4 = actorSystem.actorOf(BroadcastPool(5).props(Props[ServiceActor]))
  for (i <- 1 to 10) {
    router4 ! s"Hello $i"  //Load balances to ServiceActor with least messages
    Thread.sleep(100)
  }
  println("scattergatherfirst")
  //Send message to all actor, after first responds send message to rest to stop it.
  //Hmm for some reason  all actors responded and crashes after timeout
  //val router5 = actorSystem.actorOf(ScatterGatherFirstCompletedPool(5, within = 10.seconds).props(Props[ServiceActor]))
  //println(Await.result((router5 ? "hello").mapTo[String], 10 seconds))
  //val router6 = actorSystem.actorOf(TailChoppingPool(5, within = 10.seconds,interval = 20.millis).props(Props[ServiceActor]))
  //println(Await.result((router6 ? "hello").mapTo[String], 10 seconds))
}
