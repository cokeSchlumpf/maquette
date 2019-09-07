package maquette.controller.values

import maquette.controller.domain.values.exceptions.DomainException

import scala.util.matching.Regex

case class ResourceName(name: String)

object ResourceName {

  val regex: Regex = "^[a-z][a-z\\-0-9]*$".r

  def apply(name: String): ResourceName = {
    if (regex.pattern.matcher(name).matches()) {
      new ResourceName(name)
    } else {
      throw DomainException(s"'$name' is not a valid resource name")
    }
  }

}
