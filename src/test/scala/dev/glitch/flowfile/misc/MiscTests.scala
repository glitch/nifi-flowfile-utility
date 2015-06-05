package dev.glitch.flowfile.misc

import java.io.FileInputStream

import dev.glitch.flowfile.builder.FlowFileBuilder
import dev.glitch.flowfile.reader.ExtractFlowFile
import org.apache.commons.jexl2.{MapContext, Expression, JexlEngine}
import org.junit._
import Assert._

/**
 * Miscellaneous tests
 */
class MiscTests {

  @Test
  def testInputStreamReading(): Unit = {
    val path = "/home/kyle/giraffe.jpg"

    val iStream = new FileInputStream(path)

    var i = 0
    while (iStream.read() != -1) {
      i+=1
    }
    iStream.close()

    var j = 0
    val jStream = new FileInputStream(path)
  }

  @Test
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
}
