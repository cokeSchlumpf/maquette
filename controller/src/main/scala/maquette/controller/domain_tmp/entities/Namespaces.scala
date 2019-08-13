package maquette.controller.domain_tmp.entities

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.AbstractBehavior
import akka.persistence.typed.scaladsl.EventSourcedBehavior
import maquette.controller.domain.entities.NamespaceRepository.Protocol.{Command, Event}
import maquette.controller.domain.entities.Namespace.Protocol.{Command => NamespaceCommand}
import maquette.controller.domain.entities.NamespaceRepository.State
import maquette.controller.domain_tmp.entities.Namespace.Protocol
import maquette.controller.values.{ResourceName, User}

object Namespaces {

  object Protocol {

    sealed trait Command

    case class CreateNamespace(executor: User, name: ResourceName, replyTo: ActorRef[CreatedNamespace]) extends Command

    case class DeleteNamespace(executor: User, name: ResourceName, replyTo: ActorRef[DeletedNamespace]) extends Command

    case class RenameNamespace(executor: User, name: ResourceName, newName: ResourceName, replyTo: ActorRef[RenamedNamespace]) extends Command

    sealed trait Event

    case class CreatedNamespace(executor: User, name: ResourceName) extends Event

    case class DeletedNamespace(executor: User, name: ResourceName) extends Event

    case class RenamedNamespace(executor: User, name: ResourceName, newName: ResourceName) extends Event

    sealed trait Query extends Command

    private[domain] case class Lookup(name: ResourceName, replyTo: ActorRef[ActorRef[Protocol.Command]]) extends Query

    sealed trait Result

    private[domain] case class LookupResult(name: ResourceName, namespace: ActorRef[Protocol.Command]) extends Result

  }

  sealed trait State

  def apply()

}

class Namespaces extends EventSourcedBehavior[Command, Event, State] {

  override def onMessage(msg: Command): Behavior[Command] = msg match {
    case _ =>
  }

}
