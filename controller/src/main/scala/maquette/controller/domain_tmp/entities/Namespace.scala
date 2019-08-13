package maquette.controller.domain_tmp.entities

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.AbstractBehavior
import akka.actor.typed.scaladsl.ActorContext
import Namespace.Protocol.Command
import maquette.controller.values.ResourceName

object Namespace {

  object Protocol {

    sealed trait Command

  }

}

class Namespace(ctx: ActorContext[Command], name: ResourceName) extends AbstractBehavior[Command] {

  override def onMessage(msg: Command): Behavior[Command] = msg match {
    case command =>
      ctx.log.info(s"Received unknown command $command ...")
      this
  }

}
