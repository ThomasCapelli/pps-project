package hard

import akka.actor.testkit.typed.scaladsl.{ScalaTestWithActorTestKit, TestProbe}
import expectedresults.hard.{PingActor, PongActor}
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.matchers.should.Matchers

class PingPongLimitActorSpec extends ScalaTestWithActorTestKit with AnyWordSpecLike with Matchers {

  "PingActor" should {

    "respond to PongMessage by sending PingMessage to pong actor" in {
      val pingActor = spawn(PingActor())
      val pongProbe = TestProbe[PongActor.Command]()

      pingActor ! PingActor.PongMessage(counter = 1, pongProbe.ref)

      pongProbe.expectMessageType[PongActor.PingMessage]
    }

    "send correct replyTo reference in PingMessage" in {
      val pingActor = spawn(PingActor())
      val pongProbe = TestProbe[PongActor.Command]()

      pingActor ! PingActor.PongMessage(counter = 1, pongProbe.ref)

      val pingMsg = pongProbe.expectMessageType[PongActor.PingMessage]
      pingMsg.replyTo shouldBe pingActor
    }
  }

  "PongActor" should {

    "respond to PingMessage by sending PongMessage to ping actor" in {
      val pongActor = spawn(PongActor())
      val pingProbe = TestProbe[PingActor.Command]()

      pongActor ! PongActor.PingMessage(counter = 1, pingProbe.ref)

      pingProbe.expectMessageType[PingActor.PongMessage]
    }

    "send correct replyTo reference in PongMessage" in {
      val pongActor = spawn(PongActor())
      val pingProbe = TestProbe[PingActor.Command]()

      pongActor ! PongActor.PingMessage(counter = 1, pingProbe.ref)

      val pongMsg = pingProbe.expectMessageType[PingActor.PongMessage]
      pongMsg.replyTo shouldBe pongActor
    }
  }

  "PingPong interaction" should {

    "perform a complete cycle of ping-pong messages down to zero" in {
      val pingActor = spawn(PingActor())
      val pongProbe = TestProbe[PongActor.Command]()

      pingActor ! PingActor.PongMessage(counter = 3, pongProbe.ref)

      for (i <- (1 to 3).reverse) {
        val pingMsg = pongProbe.expectMessageType[PongActor.PingMessage]
        pingMsg.counter shouldBe i - 1
        pingMsg.replyTo ! PingActor.PongMessage(pingMsg.counter, pongProbe.ref)
      }
    }

    "support multiple independent ping-pong actor pairs" in {
      val ping1 = spawn(PingActor(), "ping1")
      val pong1 = spawn(PongActor(), "pong1")
      val ping2 = spawn(PingActor(), "ping2")
      val pong2 = spawn(PongActor(), "pong2")

      ping1 ! PingActor.PongMessage(counter = 2, pong1)
      ping2 ! PingActor.PongMessage(counter = 2, pong2)

      Thread.sleep(1000)
    }
  }
}
