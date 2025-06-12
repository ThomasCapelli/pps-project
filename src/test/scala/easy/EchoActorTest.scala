package easy

import akka.actor.testkit.typed.scaladsl.{LoggingTestKit, ScalaTestWithActorTestKit}
import expectedresults.easy.EchoActor
import org.scalatest.wordspec.AnyWordSpecLike

class EchoActorTest extends ScalaTestWithActorTestKit with AnyWordSpecLike {

  "EchoActor" should {

    "log received messages" in {
      val echoActor = testKit.spawn(EchoActor(), "echo-actor-test")

      val testMessages = List("test1", "test2")

      testMessages.foreach { msg =>
        LoggingTestKit.info(s"Echo: $msg").expect {
          echoActor ! EchoActor.Echo(msg)
        }
      }
    }

    "handle empty message" in {
      val echoActor = spawn(EchoActor())
      LoggingTestKit.info(s"Echo: ").expect {
        echoActor ! EchoActor.Echo("")
      }
    }
  }
}