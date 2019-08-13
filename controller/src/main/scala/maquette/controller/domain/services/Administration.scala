package maquette.controller.domain.services

import maquette.controller.domain.entities.{NamespaceRepository, UserNamespace}
import maquette.controller.values.User

case class RegisterUserRequest(user: User)

sealed trait RegisterUserResult
case class RegisteredUser(user: User) extends RegisterUserResult
case class UserAlreadyExists(user: User) extends RegisterUserResult

class Administration(namespaceRepository: NamespaceRepository) {

  def registerUser(executor: User, request: RegisterUserRequest): RegisterUserResult = {
    namespaceRepository.getAllNamespaces.find(_.name.eq(request.user.id)) match {
      case Some(_) =>
        UserAlreadyExists(request.user)
      case None =>
        val namespace = UserNamespace(request.user)()
        namespaceRepository.saveNamespace(namespace)
        RegisteredUser(request.user)
    }
  }

}