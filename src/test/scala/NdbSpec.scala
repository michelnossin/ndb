import akka.actor.{ActorRef, ActorSystem, Props}
import akka.testkit.TestActorRef
import akka.util.Timeout
import com.nossin.ndb.Ndb
import com.nossin.ndb.ScalaPongActor
import com.nossin.ndb.messages.SetRequest
import org.scalatest.{BeforeAndAfterEach, FunSpecLike, Matchers}

import scala.concurrent.duration
import scala.concurrent.Await
import scala.concurrent.duration._
import akka.pattern.ask

//Scala
//object ScalaPongActor { def props(response: String): Props = { Props(classOf[ScalaPongActor], response) }
//}

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
  describe("Pong actor") {
    val pongActor = system.actorOf(Props(classOf[ScalaPongActor]))
    implicit val timeout = Timeout(5 seconds)

    it("should respond with Pong") {
      val future = pongActor ? "Ping"
      val result = Await.result(future.mapTo[String], 1 second)
      assert(result == "Pong")
    }
    it("should fail on unknown message") {
      val future = pongActor ? "unknown"
      intercept[Exception]{
        Await.result(future.mapTo[String], 1 second)
      }
    }
  }
}