package maquette.api

import maquette.api.types.ResourceName

import scala.util.matching.Regex

object types {

  case class ResourceName(name: String)

  object ResourceName {

    val regex: Regex = "^[a-z][a-z\\-0-9]*$".r

    def apply(name: String): ResourceName = {
      if (regex.pattern.matcher(name).matches()) {
        new ResourceName(name)
      } else {
        throw new IllegalArgumentException(s"'$name' is not a valid resource name")
      }
    }

  }

}

object formats {

  import spray.json._
  import spray.json.RootJsonFormat

  implicit val resourceNameFormat: RootJsonFormat[ResourceName] = new RootJsonFormat[ResourceName] {

    override def read(json: JsValue): ResourceName = json match {
      case JsString(value) => ResourceName(value)
      case _ => throw new RuntimeException("Invalid format")
    }

    override def write(obj: ResourceName): JsValue = JsString(obj.name)

  }

}
