package com.nossin.ndb

import scala.util.{Failure, Success}
import akka.actor.{ActorRef, ActorSystem, Cancellable, PoisonPill, Props}

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import akka.pattern.ask
import akka.persistence.query.PersistenceQuery
import akka.persistence.query.journal.leveldb.scaladsl.LeveldbReadJournal
import akka.persistence.{Recovery, SnapshotSelectionCriteria}
import akka.routing.ConsistentHashingRouter.{ConsistentHashMapping, ConsistentHashableEnvelope}
import akka.routing._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink

import scala.concurrent.duration._
import akka.util.Timeout
import com.nossin.ndb.custommailbox.{BecomeActor, Logger, PriorityActor, ProxyActor}
import com.nossin.ndb.messages.Hashing.{Entry, Evict, Get}
import com.nossin.ndb.messages.{CustomControlMessage, Stop}
import com.nossin.ndb.messages.Messages.{Cancel, CreateChild, Error, Kill, Send, Service, Start, StopActor}
import com.typesafe.config.{Config, ConfigFactory}

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
  becomeUnBecome ! "still there?"
  //becomeUnBecome ! PoisonPill  //build in kill message
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
  //println("scattergatherfirst")
  //Send message to all actor, after first responds send message to rest to stop it.
  //Hmm for some reason  all actors responded and crashes after timeout
  //val router5 = actorSystem.actorOf(ScatterGatherFirstCompletedPool(5, within = 10.seconds).props(Props[ServiceActor]))
  //println(Await.result((router5 ? "hello").mapTo[String], 10 seconds))
  // Tail chopping also ignores rest after first responds. Actors are randomly picked
  //val router6 = actorSystem.actorOf(TailChoppingPool(5, within = 10.seconds,interval = 20.millis).props(Props[ServiceActor]))
  //println(Await.result((router6 ? "hello").mapTo[String], 10 seconds))

  // random pool, if you ont care which actor gets message
  val router7 = actorSystem.actorOf(RandomPool(5).props(Props[ServiceActor]))
  for (i <- 1 to 10) {
    router7 ! s"Hello $i"
    Thread.sleep(100)
  }

  //Caching and consistent hashing. If we want to cache over multiple nodes and make sure the same data is distributed the same way
  //we will use hasing
  def hashMapping: ConsistentHashMapping = {
    case Evict(key) => key
  }
  val cache = actorSystem.actorOf(ConsistentHashingPool(10, hashMapping = hashMapping).props(Props[CacheActor]), name = "cache")
  cache ! ConsistentHashableEnvelope(message = Entry("hello", "HELLO"), hashKey = "hello")
  cache ! ConsistentHashableEnvelope(message = Entry("hi", "HI"), hashKey = "hi")
  cache ! ConsistentHashableEnvelope(message = Entry("ahaaa", "MICHEL"), hashKey = "ahaaa")
  cache ! Get("hello")
  cache ! Get("hi")
  cache ! Evict("hi")

  //Special message to router
  println("broadcast to router")
  router7 ! Broadcast("Watch out for Davy Jones' locker")  //Send to actors
  router7 ! PoisonPill //Kill router, which will will stop the children but poison is not send to children
  router7 ! Broadcast(PoisonPill) //Router and children poison
  router7 ! Kill //Send ActorKilledException , like poisonpill use Broadcast(Kill) to send kill to children also

  //ResizablePool , will grow and shrink based on needs
  println("load test")
  val resizer = DefaultResizer(lowerBound = 2, upperBound = 15)
  val router8 = actorSystem.actorOf(RoundRobinPool(5, Some(resizer)).props(Props[ServiceActor]))
  router8 ! "some small load"

  //Futures , to handle operations on another thread. Results can be obtained synchronious by blocking the thread,
  //or using futures:
  val future2 = Future(1+2).mapTo[Int]
  val sum = Await.result(future2, 10 seconds)
  println(s"Future Result $sum")
  val future3 = (FibActor ? 3 ).mapTo[Int]
  val sum2 = Await.result(future3, 10 seconds)
  println(s"Future Result Fibonacci $sum2")

  //Use for comprehension to browse futures like lists
  val futureA = Future(20 + 20)
  val futureB = Future(30 + 30)
  val finalFuture: Future[Int] = for {
    a <- futureA
    b <- futureB
  } yield a + b
  println("Future result is " + Await.result(finalFuture, 1 seconds))

  //future callback for async
  val future4 = Future(1 + 2).mapTo[Int]
  future4 onComplete {
    case Success(result) => println(s"result is $result")
    case Failure(fail) => fail.printStackTrace()
  }
  println("Executed before callback")

  //Reduce futures list
  val listOfFutures = (1 to 10).map(Future(_))
  val finalFuture2 = Future.reduce(listOfFutures)(_ + _)
  println(s"sum of numbers from 1 to 10 is ${Await.result(finalFuture2, 10 seconds)}")

  //scheduling
  actorSystem.scheduler.scheduleOnce(10 seconds) { println(s"Sum of (1 + 2) is ${1 + 2}")}
  //actorSystem.scheduler.schedule(11 seconds, 2 seconds) { println(s"Hello, Sorry for disturbing you every 2 seconds") }

  //Do the same with actors
  actorSystem.scheduler.scheduleOnce(10 seconds, sumActor, 4)
  //val cancellable: Cancellable =  actorSystem.scheduler.schedule(11 seconds, 2 seconds, sumActor, 8)

  //To cancel, your schedule should be of type Cancellable
  //Thread.sleep(10000)
  //sumActor ! Cancel
  logActor ! "how are"

  //How to read config file. Put a .conf file in src/main/resources and add some yaml.
  val config: Config = ConfigFactory.load("application.conf")
  val iq = config.getString("michel.iq")
  println(s"michel iq is ${iq}")

  //Using persistence Actor
  println("Using persistence actor")
  val persistentActor1 = actorSystem.actorOf(Props[PersistenceActor])
  persistentActor1 ! UserUpdate("foo", Add)
  persistentActor1 ! UserUpdate("baz", Add)
  persistentActor1 ! "snap"
  persistentActor1 ! "print"
  persistentActor1 ! UserUpdate("baz", Remove)
  persistentActor1 ! "print"
  Thread.sleep(2000)
  actorSystem.stop(persistentActor1)
  val persistentActor2 = actorSystem.actorOf(Props[PersistenceActor])
  persistentActor2 ! "print"
  Thread.sleep(2000)
  //actorSystem.terminate()

  //Recovery of Actors
  val hector = actorSystem.actorOf(FriendActor.props("Hector", Recovery()))
  hector ! AddFriend(Friend("Laura"))
  hector ! AddFriend(Friend("Nancy"))
  hector ! AddFriend(Friend("Oliver"))
  hector ! AddFriend(Friend("Steve"))
  hector ! "snap"
  hector ! RemoveFriend(Friend("Oliver"))
  hector ! "print"
  Thread.sleep(2000)
  //actorSystem.terminate()

  //recovery usi g snapshots and events replay
  println("recovery of friend")
  //val system = ActorSystem("test")
  val hector2 = actorSystem.actorOf(FriendActor.props("Hector", Recovery()))
  hector2 ! "print"
  Thread.sleep(2000)

  //recover using only events
  val recovery3 = Recovery(fromSnapshot = SnapshotSelectionCriteria.None)
  val hector3 = actorSystem.actorOf(FriendActor.props("Hector", recovery3))
  hector3 ! "print"

  //limit recovery
  //val recovery = Recovery(fromSnapshot = SnapshotSelectionCriteria.None, replayMax = 3)
  val recovery4 = Recovery(fromSnapshot = SnapshotSelectionCriteria.None, toSequenceNr = 2)
  val hector4 = actorSystem.actorOf(FriendActor.props("Hector", recovery4))
  hector4 ! "print"
  Thread.sleep(2000)

  //Fsm , finit state actor to persist data
  def createActor(id: String) =  actorSystem.actorOf(FsmActor.props(id))

  val actorx = createActor("uid1")
  actorx ! Initialize(4)
  actorx ! Mark
  actorx ! Mark
  Thread.sleep(2000)
  actorSystem.stop(actorx)
  println("new actor same fsm")
  val actory = createActor("uid1")
  Thread.sleep(2000)
  actory ! Mark
  actory ! Mark

  //import system.dispatcher
  //Lets use persistence query to get all events
  implicit val mat = ActorMaterializer()(actorSystem)
  val queries = PersistenceQuery(actorSystem).readJournalFor[LeveldbReadJournal](LeveldbReadJournal.Identifier)
  println(" adding some friends")
  val laura = actorSystem.actorOf(FriendActor.props("Laura", Recovery()))
  val maria = actorSystem.actorOf(FriendActor.props("Maria", Recovery()))
  laura ! AddFriend(Friend("Hector"))
  laura ! AddFriend(Friend("Nancy"))
  maria ! AddFriend(Friend("Oliver"))
  maria ! AddFriend(Friend("Steve"))
  Thread.sleep(2000)
  actorSystem.scheduler.scheduleOnce(5 second, maria, AddFriend(Friend("Steve")))
  actorSystem.scheduler.scheduleOnce(10 second, maria, RemoveFriend(Friend("Oliver")))
  Thread.sleep(2000)

  print ("Query the event source logs")
  queries.allPersistenceIds().map(id => actorSystem.log.info(s"Id received [$id]")).to(Sink.ignore).run()
  queries.eventsByPersistenceId("Laura").map(e => log(e.persistenceId, e.event)).to(Sink.ignore).run()
  queries.eventsByPersistenceId("Maria").map(e => log(e.persistenceId, e.event)).to(Sink.ignore).run()
  Thread.sleep(2000)

  def log(id: String, evt: Any) = actorSystem.log.info(s"Id [$id] Event [$evt]")

  //Akka remote (akka protocol for peer to peer connection actors on different machines and jvm
  //and akka clustering, gossip protocol, actors are member of a cluster handling HA
  //Use run configurations, or use sbt, start actors in this order:
  //sbt -Dconfig.resource=application-1.conf "runMain com.nossin.ndb.HelloAkkaRemoting1"
  //sbt -Dconfig.resource=application-2.conf "runMain com.nossin.ndb.HelloAkkaRemoting2"

  actorSystem.terminate()
}
