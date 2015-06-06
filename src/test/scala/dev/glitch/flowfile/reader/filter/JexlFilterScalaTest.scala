package dev.glitch.flowfile.reader.filter

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers

/**
 * Unit tests for JexlFilter using ScalaTest
 */
@RunWith(classOf[JUnitRunner])
class JexlFilterScalaTest extends FunSuite with ShouldMatchers {

  test("Filter should match on these attributes"){
    val filter = new JexlFilter("FOO == 'bar' and Color == 'Red'")
    val attributes = Map("Foo"->"bar", "Some"->"thing", "color"->"red")

    filter.matches(attributes) should be (true)
  }

  test("Filter should NOT match on these attributes"){
    val filter = new JexlFilter("FOO == 'bar' and Color == 'blue'")
    val attributes = Map("Foo"->"bar", "Some"->"thing", "color"->"red")

    filter.matches(attributes) should not be (true)
  }
}
