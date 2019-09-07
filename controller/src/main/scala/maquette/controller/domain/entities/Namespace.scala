package maquette.controller.domain.entities

import java.time.Instant

import maquette.controller.domain.values.exceptions.DomainException
import maquette.controller.values._

import scala.util.{Success, Try}

trait NamespaceRepository {

  def getAllNamespaces: List[Namespace]

  def getNamespace(name: ResourceName): Option[Namespace]

  def saveNamespace(namespace: Namespace): Unit

}

private[domain] sealed trait Namespace {

  val id: UID

  var grants: Set[GrantedAuthorization]

  def name: ResourceName

  def changeOwner(executor: User, to: Authorization): Try[Unit]

  def rename(name: ResourceName): Try[Unit]

  def isAllowed(executor: User): Boolean = {
    // TODO: Implement actual logic
    true
  }

  def grant(executor: User, to: Authorization): Try[Unit] = {
    this.grants = this.grants + GrantedAuthorization(to, executor, Instant.now())
    Success()
  }

  def revoke(executor: User, from: Authorization): Try[Unit] = {
    this.grants = grants.filter(_.authorization.equals(from))
    Success()
  }

}

private[domain] case class UserNamespace(
                          owner: User)(
                          var grants: Set[GrantedAuthorization] = Set()) extends Namespace {

  override val id: UID = UID(owner.id.name)

  override def name: ResourceName = owner.id

  override def changeOwner(executor: User, to: Authorization): Try[Unit] = {
    throw DomainException("A user namespace cannot change its owner")
  }

  override def rename(name: ResourceName): Try[Unit] = {
    throw DomainException("A user namespace cannot be renamed")
  }

}

private[domain] case class OrganizationNamespace(
                             id: UID)(
                             var name: ResourceName,
                             var owner: Authorization,
                             var grants: Set[GrantedAuthorization] = Set()) extends Namespace {

  override def changeOwner(executor: User, to: Authorization): Try[Unit] = {
    this.owner = to
    Success()
  }

  override def rename(name: ResourceName): Try[Unit] = {
    this.name = name
    Success()
  }

}
