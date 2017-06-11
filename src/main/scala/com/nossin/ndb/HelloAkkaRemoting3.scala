package com.nossin.ndb

import akka.actor.{ActorRef, ActorSystem, Address, Deploy, Props}
import akka.remote.RemoteScope
import com.nossin.ndb.messages.Messages.showAddress

import scala.concurrent.duration._

/**
  * Created by michelnossin on 11-06-17.
  */

object HelloAkkaRemoting3  extends App {
  val actorSystem = ActorSystem("LookingUpActors")
  implicit val dispatcher = actorSystem.dispatcher
  val selection = actorSystem.actorSelection("akka.tcp://LookingUpRemoteActors@127.0.0.1:2553/user/remoteActor")
  selection ! "test"
  selection.resolveOne(3 seconds).onSuccess {
    case actorRef : ActorRef =>
    println("We got an ActorRef")
    actorRef ! "test"
  }
}

object LookingUpRemoteActors extends App {
  val actorSystem = ActorSystem("LookingUpRemoteActors")
  actorSystem.actorOf(Props[ResultPrinterActor], "remoteActor")
}

object myActorSystem extends App {
  val actorSystem = ActorSystem("MyActorSystem")
}

object RemoteActors extends App {
  val actorSystem = ActorSystem("RemoteActorsProgramatically2")
  println("Creating actor from RemoteActorsProgramatically2")
  val address = Address("akka.tcp", "MyActorSystem", "127.0.0.1", 2552)
  val actor = actorSystem.actorOf(Props[ResultPrinterActor].withDeploy(Deploy(scope = RemoteScope(address))), "remoteActor")
  actor ! showAddress
}