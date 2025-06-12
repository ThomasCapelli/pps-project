package medium
import akka.actor.testkit.typed.scaladsl.{ScalaTestWithActorTestKit, TestProbe}
import expectedresults.medium.{PingActor, PongActor}
import org.scalatest.wordspec.AnyWordSpecLike

class PingPongSpec extends ScalaTestWithActorTestKit with AnyWordSpecLike {

  "PingActor" should {

    "respond to Pong message by sending Ping" in {
      val pingActor = spawn(PingActor())
      val pongProbe = TestProbe[PongActor.Command]()

      pingActor ! PingActor.Pong(pongProbe.ref)

      pongProbe.expectMessageType[PongActor.Ping]
    }

    "send correct actor reference in Ping message" in {
      val pingActor = spawn(PingActor())
      val pongProbe = TestProbe[PongActor.Command]()

      pingActor ! PingActor.Pong(pongProbe.ref)

      val ping = pongProbe.expectMessageType[PongActor.Ping]
      ping.pingRef shouldBe pingActor
    }
  }

  "PongActor" should {

    "respond to Ping message by sending Pong" in {
      val pongActor = spawn(PongActor())
      val pingProbe = TestProbe[PingActor.Command]()

      pongActor ! PongActor.Ping(pingProbe.ref)

      pingProbe.expectMessageType[PingActor.Pong]
    }

    "send correct actor reference in Pong message" in {
      val pongActor = spawn(PongActor())
      val pingProbe = TestProbe[PingActor.Command]()

      pongActor ! PongActor.Ping(pingProbe.ref)

      val pong = pingProbe.expectMessageType[PingActor.Pong]
      pong.pongRef shouldBe pongActor
    }
  }

  "PingPong interaction" should {

    "create a ping-pong cycle" in {
      val pingActor = spawn(PingActor())
      val pongActor = spawn(PongActor())

      // Start the cycle
      pingActor ! PingActor.Pong(pongActor.ref)

      // Give it time to exchange a few messages
      Thread.sleep(2000)

      // Both actors should still be alive
      pingActor ! PingActor.Pong(pongActor.ref)
      pongActor ! PongActor.Ping(pingActor.ref)

      Thread.sleep(1000)
    }

    "handle multiple ping-pong cycles" in {
      val pingActor1 = spawn(PingActor(), "ping1")
      val pongActor1 = spawn(PongActor(), "pong1")
      val pingActor2 = spawn(PingActor(), "ping2")
      val pongActor2 = spawn(PongActor(), "pong2")

      // Start multiple cycles
      pingActor1 ! PingActor.Pong(pongActor1.ref)
      pingActor2 ! PingActor.Pong(pongActor2.ref)

      Thread.sleep(1500)
    }
  }
}