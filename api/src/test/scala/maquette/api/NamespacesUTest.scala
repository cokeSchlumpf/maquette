package maquette.api
import org.junit.runner.RunWith
import org.scalatest._
import org.scalatestplus.junit.JUnitRunner

import org.assertj.core.api.Assertions.assertThat

@RunWith(classOf[JUnitRunner])
class NamespacesUTest extends FlatSpec with Matchers {

  import maquette.api.types._
  import maquette.api.Namespaces.messages._
  import maquette.api.Namespaces.formats._
  import spray.json._

  "An unmarshalled request" should "be equal to its original instance" in {
    val a = CreateNamespaceRequest(ResourceName("foo"))
    val b = a.toJson.convertTo[CreateNamespaceRequest]

    assertThat(a).isEqualTo(b)
  }

  "An unmarshalled response" should "be equal to its original instance" in {
    val a = CreatedNamespace(ResourceName("foo")).asInstanceOf[CreateNamespaceResponse]
    val b = a.toJson.convertTo[CreateNamespaceResponse]

    assertThat(a).isEqualTo(b)

    val c = NotAuthorized.asInstanceOf[CreateNamespaceResponse]
    val d = c.toJson.convertTo[CreateNamespaceResponse]

    assertThat(c).isEqualTo(d)

    val e = AlreadyExists.asInstanceOf[CreateNamespaceResponse]
    val f = e.toJson.convertTo[CreateNamespaceResponse]

    assertThat(e).isEqualTo(f)
  }

}
