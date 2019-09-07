package maquette.controller.domain.values.exceptions

case class DomainException(message: String) extends Exception(message)
