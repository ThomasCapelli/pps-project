package expectedresults.easy

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}

object CounterActor {
  sealed trait Command
  case object Increment extends Command
  case object Decrement extends Command
  case class IncrementBy(value: Int) extends Command
  case object Reset extends Command
  case class GetCount() extends Command

  sealed trait CountResponse
  case class CountValue(count: Int) extends CountResponse

  def apply(): Behavior[Command] = counting(0)

  private def counting(currentCount: Int): Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case Increment =>
          val newCount = currentCount + 1
          ctx.log.info(s"Contatore incrementato: $newCount")
          counting(newCount)

        case Decrement =>
          val newCount = currentCount - 1
          ctx.log.info(s"Contatore decrementato: $newCount")
          counting(newCount)

        case IncrementBy(value) =>
          val newCount = currentCount + value
          ctx.log.info(s"Contatore incrementato di $value: $newCount")
          counting(newCount)

        case Reset =>
          ctx.log.info("Contatore resettato: 0")
          counting(0)

        case GetCount() =>
          ctx.log.info(s"Contatore: $currentCount")
          Behaviors.same
      }
    }
}

object CounterApp extends App {
  import CounterActor._

  val system: ActorSystem[Command] = ActorSystem(CounterActor(), "CounterSystem")

  // Invio dei messaggi
  system ! Increment
  system ! IncrementBy(5)
  system ! Decrement
  system ! GetCount()
  system ! Reset
  system ! GetCount()

  Thread.sleep(2000)
  system.terminate()
}

