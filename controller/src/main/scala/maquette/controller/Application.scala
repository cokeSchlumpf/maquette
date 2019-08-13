package maquette.controller

import maquette.api.Namespaces.formats._
import maquette.api.Namespaces.messages._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.{HttpApp, Route}
import maquette.api.types.ResourceName

object Controller extends HttpApp with SprayJsonSupport {

  override protected def routes: Route = {
    pathPrefix("api" / "v1" / "resources" / "namespaces") {
      post {
        entity(as[CreateNamespaceRequest]) { request =>
          complete(CreatedNamespace(request.name).asInstanceOf[CreateNamespaceResponse])
        }
      } ~
      get {
        complete(CreatedNamespace(ResourceName("foo")).asInstanceOf[CreateNamespaceResponse])
      }
    }
  }

}

object Application extends App {

  Controller.startServer("0.0.0.0", 8080)

}


