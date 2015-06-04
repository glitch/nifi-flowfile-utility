package dev.glitch.flowfile.misc

import java.io.FileInputStream

import dev.glitch.flowfile.reader.ExtractFlowFile
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
  def testExtractFlowFile(): Unit = {
    val foo = new ExtractFlowFile()
    foo.main(new Array[String](0))

    val bar = new ExtractFlowFile()
    bar.main(Array("--all","true"))
  }
}
