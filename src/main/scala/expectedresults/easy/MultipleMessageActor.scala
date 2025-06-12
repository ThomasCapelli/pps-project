package expectedresults.easy

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorSystem, Behavior}

object MultiActor {
  sealed trait Command
  case class StringMessage(text: String) extends Command
  case class IntMessage(number: Int) extends Command
  case class DoubleMessage(value: Double) extends Command

  def apply(): Behavior[Command] = Behaviors.receive { (ctx, msg) =>
    msg match {
      case StringMessage(text) =>
        ctx.log.info(s"Processing string: '${text.toUpperCase}'")
        Behaviors.same

      case IntMessage(number) =>
        ctx.log.info(s"Processing int: ${number * 2} (doubled)")
        Behaviors.same

      case DoubleMessage(value) =>
        ctx.log.info(s"Processing double: ${math.round(value * 100.0) / 100.0} (rounded to 2 decimals)")
        Behaviors.same
    }
  }
}

object MultipleMessageMain extends App {
  val system = ActorSystem(MultiActor(), "multi-actor")

  // Send different message types
  system ! MultiActor.StringMessage("hello world")
  system ! MultiActor.IntMessage(42)
  system ! MultiActor.DoubleMessage(3.14159)
  system ! MultiActor.StringMessage("akka rocks")
  system ! MultiActor.IntMessage(21)
  system ! MultiActor.DoubleMessage(2.71828)

  Thread.sleep(1000)
  system.terminate()
}