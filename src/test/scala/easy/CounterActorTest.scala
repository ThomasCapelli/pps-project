package easy

import akka.actor.testkit.typed.scaladsl.{ActorTestKit, LoggingTestKit}
import akka.actor.typed.ActorSystem
import expectedresults.easy.CounterActor
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class CounterActorSpec extends AnyWordSpec with BeforeAndAfterAll with Matchers {

  val testKit = ActorTestKit()
  implicit val system: ActorSystem[Nothing] = testKit.system

  override def afterAll(): Unit = testKit.shutdownTestKit()

  import CounterActor._

  "CounterActor" should {

    "increment counter by 1" in {
      val counterActor = testKit.spawn(CounterActor(), "counter-increment")

      LoggingTestKit.info("Contatore incrementato: 1").expect {
        counterActor ! Increment
      }
    }

    "decrement counter by 1" in {
      val counterActor = testKit.spawn(CounterActor(), "counter-decrement")

      // Prima incrementiamo per avere un valore positivo
      counterActor ! Increment
      counterActor ! Increment

      LoggingTestKit.info("Contatore decrementato: 1").expect {
        counterActor ! Decrement
      }
    }

    "increment counter by specific value" in {
      val counterActor = testKit.spawn(CounterActor(), "counter-increment-by")

      LoggingTestKit.info("Contatore incrementato di 5: 5").expect {
        counterActor ! IncrementBy(5)
      }
    }

    "reset counter to 0" in {
      val counterActor = testKit.spawn(CounterActor(), "counter-reset")

      // Prima incrementiamo
      counterActor ! IncrementBy(10)

      LoggingTestKit.info("Contatore resettato: 0").expect {
        counterActor ! Reset
      }
    }

    "show current count with GetCount" in {
      val counterActor = testKit.spawn(CounterActor(), "counter-get-count")

      LoggingTestKit.info("Contatore: 0").expect {
        counterActor ! GetCount()
      }
    }

    "handle negative values correctly" in {
      val counterActor = testKit.spawn(CounterActor(), "counter-negative")

      LoggingTestKit.info("Contatore decrementato: -1").expect {
        counterActor ! Decrement
      }
    }

    "handle sequence of operations correctly" in {
      val counterActor = testKit.spawn(CounterActor(), "counter-sequence")

      counterActor ! Increment // 1
      counterActor ! IncrementBy(3) // 4
      counterActor ! Decrement // 3
      counterActor ! IncrementBy(2) // 5

      LoggingTestKit.info("Contatore: 5").expect {
        counterActor ! GetCount()
      }
    }
  }
}