import akka.actor.typed.{ActorRef, ActorSystem, Behavior, Terminated}
import akka.actor.typed.scaladsl.Behaviors

object PingPongApp {

  // Messages
  sealed trait PingPongMessage
  final case class PingMessage(counter: Int, replyTo: ActorRef[PingPongMessage]) extends PingPongMessage
  final case class PongMessage(counter: Int, replyTo: ActorRef[PingPongMessage]) extends PingPongMessage

  // Ping Actor
  def pingBehavior: Behavior[PingPongMessage] = Behaviors.receive { (context, message) =>
    message match {
      case PongMessage(counter, pongRef) =>
        println(s"Ping: $counter")
        if (counter <= 0) {
          pongRef ! PingMessage(0, context.self)
          Behaviors.stopped
        } else {
          pongRef ! PingMessage(counter - 1, context.self)
          Behaviors.same
        }
    }
  }

  // Pong Actor
  def pongBehavior: Behavior[PingPongMessage] = Behaviors.receive { (context, message) =>
    message match {
      case PingMessage(counter, pingRef) =>
        println(s"Pong: $counter")
        if (counter <= 0) {
          pingRef ! PongMessage(0, context.self)
          Behaviors.stopped
        } else {
          pingRef ! PongMessage(counter - 1, context.self)
          Behaviors.same
        }
    }
  }

  // Guardian (Main)
  def mainBehavior(n: Int): Behavior[Nothing] = Behaviors.setup[Nothing] { context =>
    val ping = context.spawn(pingBehavior, "Ping")
    val pong = context.spawn(pongBehavior, "Pong")

    // Start the exchange
    ping ! PongMessage(n, pong)

    Behaviors.empty
  }

  def main(args: Array[String]): Unit = {
    val system = ActorSystem[Nothing](mainBehavior(n = 10), "PingPongSystem")
  }
}
