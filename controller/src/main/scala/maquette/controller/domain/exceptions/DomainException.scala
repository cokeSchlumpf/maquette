package maquette.controller.domain.exceptions

case class DomainException(message: String) extends Exception(message)
