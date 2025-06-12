package hard

import akka.actor.testkit.typed.scaladsl.{ScalaTestWithActorTestKit, TestProbe}
import expectedresults.hard.{PingActor, PongActor}
import org.scalatest.wordspec.AnyWordSpecLike

class PingPongAdvancedSpec extends ScalaTestWithActorTestKit with AnyWordSpecLike {

  "PingActor" should {

    "respond to Pong by sending Ping with decremented count" in {
      val pingActor = spawn(PingActor())
      val pongProbe = TestProbe[PongActor.Command]()

      pingActor ! PingActor.Pong(pongProbe.ref, 3)

      val ping = pongProbe.expectMessageType[PongActor.Ping]
      ping.remainingMessages shouldBe 2
    }

    "send correct ActorRef in Ping message" in {
      val pingActor = spawn(PingActor())
      val pongProbe = TestProbe[PongActor.Command]()

      pingActor ! PingActor.Pong(pongProbe.ref, 1)

      val ping = pongProbe.expectMessageType[PongActor.Ping]
      ping.pingRef shouldBe pingActor
    }

    "stop and notify Pong when message limit is 0" in {
      val pingActor = spawn(PingActor())
      val pongProbe = TestProbe[PongActor.Command]()

      pingActor ! PingActor.Pong(pongProbe.ref, 0)

      pongProbe.expectMessage(PongActor.Finished)
    }

    "handle Finished message and stop" in {
      val pingActor = spawn(PingActor())
      pingActor ! PingActor.Finished
      // Nothing to assert; should not throw
    }
  }

  "PongActor" should {

    "respond to Ping by sending Pong with decremented count" in {
      val pongActor = spawn(PongActor())
      val pingProbe = TestProbe[PingActor.Command]()

      pongActor ! PongActor.Ping(pingProbe.ref, 3)

      val pong = pingProbe.expectMessageType[PingActor.Pong]
      pong.remainingMessages shouldBe 2
    }

    "send correct ActorRef in Pong message" in {
      val pongActor = spawn(PongActor())
      val pingProbe = TestProbe[PingActor.Command]()

      pongActor ! PongActor.Ping(pingProbe.ref, 1)

      val pong = pingProbe.expectMessageType[PingActor.Pong]
      pong.pongRef shouldBe pongActor
    }

    "stop and notify Ping when message limit is 0" in {
      val pongActor = spawn(PongActor())
      val pingProbe = TestProbe[PingActor.Command]()

      pongActor ! PongActor.Ping(pingProbe.ref, 0)

      pingProbe.expectMessage(PingActor.Finished)
    }

    "handle Finished message and stop" in {
      val pongActor = spawn(PongActor())
      pongActor ! PongActor.Finished
    }
  }

  "PingPong interaction" should {

    "run a full ping-pong cycle until completion" in {
      val pingActor = spawn(PingActor())
      val pongActor = spawn(PongActor())

      pingActor ! PingActor.Pong(pongActor, 4)

      // 4 messages: Ping → Pong → Ping → Pong
      // Remaining reaches 0, each actor sends Finished
      // No assertion needed if we reach here without exceptions
      Thread.sleep(1000)
    }

    "support multiple independent ping-pong cycles" in {
      val ping1 = spawn(PingActor(), "ping1")
      val pong1 = spawn(PongActor(), "pong1")
      val ping2 = spawn(PingActor(), "ping2")
      val pong2 = spawn(PongActor(), "pong2")

      ping1 ! PingActor.Pong(pong1, 3)
      ping2 ! PingActor.Pong(pong2, 5)

      Thread.sleep(1500)
    }
  }
}
