package expectedresults.hard

import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import akka.actor.typed.scaladsl.Behaviors

object PingActor {
  sealed trait Command
  case class PongMessage(counter: Int, replyTo: ActorRef[PongActor.Command]) extends Command

  def apply(): Behavior[Command] = Behaviors.receive { (context, message) =>
    message match {
      case PongMessage(counter, pongRef) =>
        context.log.info(s"Ping: $counter")
        if (counter <= 0) {
          pongRef ! PongActor.PingMessage(0, context.self)
          Behaviors.stopped
        } else {
          pongRef ! PongActor.PingMessage(counter - 1, context.self)
          Behaviors.same
        }
    }
  }
}

object PongActor {
  sealed trait Command
  case class PingMessage(counter: Int, replyTo: ActorRef[PingActor.Command]) extends Command

  def apply(): Behavior[Command] = Behaviors.receive { (context, message) =>
    message match {
      case PingMessage(counter, pingRef) =>
        context.log.info(s"Pong: $counter")
        if (counter <= 0) {
          pingRef ! PingActor.PongMessage(0, context.self)
          Behaviors.stopped
        } else {
          pingRef ! PingActor.PongMessage(counter - 1, context.self)
          Behaviors.same
        }
    }
  }
}

object PingPongMain extends App {
  val system: ActorSystem[PingActor.Command] = ActorSystem(PingActor(), "PingPongSystem")
  val pongActor: ActorRef[PongActor.Command] = system.systemActorOf(PongActor(), "pong")

  // Start the interaction with an initial count (e.g., 10)
  system ! PingActor.PongMessage(counter = 10, pongActor)
}
