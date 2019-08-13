package maquette.controller.values

import scala.util.matching.Regex

case class UID(name: String)

object UID {

  val regex: Regex = "^[a-z][a-z\\-0-9]*$".r

  def apply(name: String): UID = {
    if (regex.pattern.matcher(name).matches()) {
      new UID(name)
    } else {
      throw new IllegalArgumentException(s"'$name' is not a valid uid")
    }
  }

}

