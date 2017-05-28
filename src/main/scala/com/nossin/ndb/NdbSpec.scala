package com.nossin.ndb

import org.scalatest.{BeforeAndAfterEach, FunSpecLike, Matchers}
import akka.actor.ActorSystem
import com.nossin.ndb.messages.SetRequest
import akka.testkit.TestActorRef
//import scala.concurrent.duration

class NdbSpec extends FunSpecLike with Matchers with BeforeAndAfterEach {
  implicit val system = ActorSystem()
  describe("ndb") {
    describe("given SetRequest"){
      it("should place key/value into map"){
        val actorRef = TestActorRef(new Ndb)
        actorRef ! SetRequest("key", "value")
        val ndb = actorRef.underlyingActor
        ndb.map.get("key") should equal(Some("value"))
      }
    }
  }
}
