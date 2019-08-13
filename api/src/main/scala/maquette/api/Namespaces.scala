package maquette.api

import maquette.api.types.ResourceName

object Namespaces {

  private object types {

    val kind = "kind"

    val created = "created"

    val alreadyExists = "already-exists"

    val notAuthorized = "not-authorized"

  }

  object messages {

    case class CreateNamespaceRequest(name: ResourceName)


    sealed trait CreateNamespaceResponse

    case class CreatedNamespace(name: ResourceName) extends CreateNamespaceResponse

    case object NotAuthorized extends CreateNamespaceResponse

    case object AlreadyExists extends CreateNamespaceResponse

  }

  object formats {

    import spray.json._
    import spray.json.DefaultJsonProtocol._
    import maquette.api.formats._
    import maquette.api.Namespaces.messages._

    implicit val createNamespaceRequestFormat: RootJsonFormat[CreateNamespaceRequest] = jsonFormat1(CreateNamespaceRequest)

    private implicit val createdNamespace: RootJsonFormat[CreatedNamespace] = jsonFormat1(CreatedNamespace)

    implicit val createNamespaceResponseFormat: RootJsonFormat[CreateNamespaceResponse] = new RootJsonFormat[CreateNamespaceResponse] {

      override def read(json: JsValue): CreateNamespaceResponse = {
        json.asJsObject.fields(types.kind) match {
          case JsString(types.created) => json.convertTo[CreatedNamespace]
          case JsString(types.notAuthorized) => NotAuthorized
          case JsString(types.alreadyExists) => AlreadyExists
          case _ => throw new RuntimeException("Unknown type")
        }
      }

      override def write(obj: CreateNamespaceResponse): JsValue = {
        obj match {
          case created: CreatedNamespace =>
            val json = created.toJson.asJsObject
            json.copy(fields = json.fields + (types.kind -> JsString(types.created))).asJsObject

          case NotAuthorized => JsObject((types.kind, JsString(types.notAuthorized)))
          case AlreadyExists => JsObject((types.kind, JsString(types.alreadyExists)))
        }
      }

    }

  }

}

trait Namespaces {

  import  maquette.api.Namespaces.messages._

  def create(in: CreateNamespaceRequest): CreateNamespaceResponse

}
