package dev.glitch.flowfile.misc


import dev.glitch.flowfile.builder.FlowFileBuilder
import dev.glitch.flowfile.reader.ExtractFlowFile
import org.apache.commons.jexl2.{MapContext, Expression, JexlEngine}
import org.junit._

/**
 * Miscellaneous tests
 */
class MiscTests {

  @Ignore
  def testFlowFileCreation(): Unit = {
    FlowFileBuilder.main(Array[String]("--config", "reference.json"))
  }

  @Test
  def testExtractFlowFile(): Unit = {
    val url = getClass.getResource("/test.flowfile.pkg")
    val bar = new ExtractFlowFile()
    bar.main(Array("--all","true", "--in", url.getPath))
  }

  @Test
  def testJexlMatch(): Unit = {
    val url = getClass.getResource("/test.flowfile.pkg")
    val bar = new ExtractFlowFile()
    val expression = "foo==\"baz\" && myId == '1'"
    bar.main(Array("--in", url.getPath, "--jexl", expression))
  }

  @Test
  def testJexl(): Unit ={
    val query = "foo==\"bar\""
    val engine:JexlEngine = new JexlEngine() // Could
    val expression:Expression = engine.createExpression(query.toLowerCase)
    val context = new MapContext()
//    context.set("foo","bar")
//    context.set("some","thing")

    val attributes:Map[String,String] = Map("foo"->"bar", "some"->"thing" )
    for ( (k,v) <- attributes ) context.set(k.toLowerCase,v.toLowerCase)

    val result = expression.evaluate(context)
    Assert.assertTrue(Boolean.unbox(result))

  }

  /**
   * Use this to test writing out the .attrs & .payload files based on matches.
   */
  @Ignore
//  @Test
  def testJexlMatchAndWrite(): Unit = {
    val url = getClass.getResource("/test.flowfile.pkg")
    val bar = new ExtractFlowFile()
    val expression = "foo==\"baz\" && myId == '1'"
    bar.main(Array("--in", url.getPath, "--jexl", expression, "--out", "."))
  }
}
