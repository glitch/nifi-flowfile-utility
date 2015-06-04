package dev.glitch.flowfile.reader.filter

import org.junit.{Assert, Test}

/**
 *
 */
class JexlFilterTest {

  @Test
  def test01(): Unit = {
    val filter = new JexlFilter("color == 'red' && make=='Ford'")
    val attrsTrue = Map("COLOR"->"red", "make"->"Ford", "MoDeL"->"F150")
    Assert.assertTrue(filter.matches(attrsTrue))

    val attrsFalse = Map("COLOR"->"blue", "make"->"Ford", "MoDeL"->"F150")
    Assert.assertFalse(filter.matches(attrsFalse))
  }
}
