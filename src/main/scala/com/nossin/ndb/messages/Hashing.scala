package com.nossin.ndb.messages

import akka.routing.ConsistentHashingRouter.ConsistentHashable

/**
  * Created by michelnossin on 05-06-17.
  */
object Hashing {
  case class Evict(key: String)
  case class Get(key: String) extends ConsistentHashable {
    override def consistentHashKey: Any = key
  }
  case class Entry(key: String, value: String)
}
