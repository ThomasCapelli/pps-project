package expectedresults.medium

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}

object PingActor {
  sealed trait Command
  case class Pong(pongRef: ActorRef[PongActor.Command]) extends Command

  def apply(): Behavior[Command] = Behaviors.receive { (ctx, msg) =>
    msg match {
      case Pong(pongRef) =>
        ctx.log.info("Ping!")
        Thread.sleep(500)
        pongRef ! PongActor.Ping(ctx.self)
        Behaviors.same
    }
  }
}

object PongActor {
  sealed trait Command
  case class Ping(pingRef: ActorRef[PingActor.Command]) extends Command

  def apply(): Behavior[Command] = Behaviors.receive { (ctx, msg) =>
    msg match {
      case Ping(pingRef) =>
        ctx.log.info("Pong!")
        Thread.sleep(500)
        pingRef ! PingActor.Pong(ctx.self)
        Behaviors.same
    }
  }
}
object PingPongMain extends App {
  val system = ActorSystem(PingActor(), "ping")
  val pongActor = system.systemActorOf(PongActor(), "pong")
  system ! PingActor.Pong(pongActor)
  system.terminate()
}