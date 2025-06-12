package easy

import akka.actor.testkit.typed.scaladsl.{LoggingTestKit, ScalaTestWithActorTestKit}
import expectedresults.easy.MultiActor
import org.scalatest.wordspec.AnyWordSpecLike

class MultipleMessageActorTest extends ScalaTestWithActorTestKit with AnyWordSpecLike {

  "MultiActor" should {

    "process string messages" in {
      val actor = spawn(MultiActor())

      LoggingTestKit.info("HELLO").expect {
        actor ! MultiActor.StringMessage("hello")
      }
      LoggingTestKit.info("WORLD").expect {
        actor ! MultiActor.StringMessage("world")
      }
    }

    "process int messages" in {
      val actor = spawn(MultiActor())

      LoggingTestKit.info("20").expect {
        actor ! MultiActor.IntMessage(10)
      }

      LoggingTestKit.info("30").expect {
        actor ! MultiActor.IntMessage(15)
      }
      LoggingTestKit.info("0").expect {
        actor ! MultiActor.IntMessage(0)
      }
    }

    "process double messages" in {
      val actor = spawn(MultiActor())

      LoggingTestKit.info("3.14").expect {
        actor ! MultiActor.DoubleMessage(3.14159)
      }
      LoggingTestKit.info("2.72").expect {
        actor ! MultiActor.DoubleMessage(2.71828)
      }
    }

    "handle mixed message types" in {
      val actor = spawn(MultiActor())

      actor ! MultiActor.StringMessage("test")
      actor ! MultiActor.IntMessage(42)
      actor ! MultiActor.DoubleMessage(3.14)
      actor ! MultiActor.StringMessage("another")
      actor ! MultiActor.IntMessage(100)

      Thread.sleep(100)
    }

    "handle edge cases" in {
      val actor = spawn(MultiActor())

      LoggingTestKit.info("20").expect {
        actor ! MultiActor.IntMessage(10)
      }
      LoggingTestKit.info("HELLO").expect {
        actor ! MultiActor.StringMessage("hello")
      }
      LoggingTestKit.info("0.0").expect {
        actor ! MultiActor.DoubleMessage(0.0)
      }
    }
  }
}
