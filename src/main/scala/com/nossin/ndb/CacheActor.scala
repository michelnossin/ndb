package com.nossin.ndb

import akka.actor.Actor
import com.nossin.ndb.messages.Hashing.{Entry, Evict, Get}

/**
  * Created by michelnossin on 05-06-17.
  */

class CacheActor extends Actor {
  var cache = Map.empty[String, String]
  def receive = {
    case Entry(key, value) =>
      println(s" ${self.path.name} adding key $key")
      cache += (key -> value)
    case Get(key) =>
      println(s" ${self.path.name} fetching key $key")
      sender() ! cache.get(key)
    case Evict(key)  =>
      println(s" ${self.path.name} removing key $key")
      cache -= key
  }
}

