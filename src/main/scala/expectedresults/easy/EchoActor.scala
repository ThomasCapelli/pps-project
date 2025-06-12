package expectedresults.easy

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorSystem, Behavior}

object EchoActor {
  sealed trait Command
  case class Echo(msg: String) extends Command
  def apply(): Behavior[Command] = Behaviors.receive { (ctx, msg) =>
    msg match {
      case Echo(text) =>
        ctx.log.info(s"Echo: $text")
        Behaviors.same
    }
  }
}

object EchoMain extends App {
  val system = ActorSystem(EchoActor(), "echo")
  val messages = List("hello", "world", "akka", "scala")
  messages.foreach(msg =>
    system ! EchoActor.Echo(msg)
  )
  system.terminate()
}