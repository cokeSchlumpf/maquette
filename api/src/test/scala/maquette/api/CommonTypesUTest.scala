package maquette.api

import org.junit.runner.RunWith
import org.scalatest.{FlatSpec, Matchers}
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class CommonTypesUTest extends FlatSpec with Matchers {

  import maquette.api.types._

  "An resource name" should "only contain valid name strings" in {
    println(ResourceName("foo"))
    println(ResourceName("foo-bar"))
  }

  an[IllegalArgumentException] should be thrownBy ResourceName("12_abc")
  an[IllegalArgumentException] should be thrownBy ResourceName("")

}
