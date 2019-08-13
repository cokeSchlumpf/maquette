package maquette.controller.values

sealed trait User {

  val id: ResourceName

  val roles: List[String]

}

case class AnonymousUser(roles: List[String]) extends User {

  val id: ResourceName = ResourceName("anonymous")

}

case class AuthenticatedUser(name: ResourceName, roles: List[String]) extends User {

  val id: ResourceName = name

}
