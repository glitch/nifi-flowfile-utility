package dev.glitch.flowfile.reader

import java.io.{InputStream, FileInputStream, File}

/**
 * Utility to extract attributes & payloads from a flow file
 */
class ExtractFlowFile {

  def main(args:Array[String]) {

    // Cmd line options parsing
    val parser = new scopt.OptionParser[ExtractCmdLineConfig]("scopt") {
      head("scopt", "3.x")
      opt[File]("in") action { (x,config) => config.copy(inputFile = x)} text "flow file bundle to read"
      opt[File]("out") action { (x,config) => config.copy(outputDir = x)} text "Output directory to write .attr & .bytes files to"
      opt[Boolean]("all") action { (x,config) => config.copy(all = x)} text "Flag to dump all records in flow file bundle"
      opt[String]("jexl") action { (x,config) => config.copy(jexlMatchCmd = x)} text "Advanced Match: JEXL query to match against"
      opt[String]("attrMatch") action { (x,config) => config.copy(simpleMatchAttr = x)} text "Simple Match: Attribute to match against"
      opt[String]("attrValue") action { (x,config) => config.copy(simpleMatchValues = x)} text "Simple Match: CSV list of values to hit on"
    }

    parser.parse(args, ExtractCmdLineConfig()) match {
      case Some(cmdLineConfig) =>

        // various cases here based on args
        val inputStream = new FileInputStream(cmdLineConfig.inputFile)
        while (UnpackFlowFile.nextHeader(inputStream)) {
          val attrs = UnpackFlowFile.getAttributes(inputStream)
          val payload = UnpackFlowFile.getPayload(inputStream)

          println("Attributes: " + attrs)
          println("Payload length: " + payload.length)
        }

        inputStream.close()

      case None =>
    }

  }

  def simpleMatch(inStream:InputStream, attrToMatch:String, valuesToMatch:Set[String] ): Unit = {

  }

  def csvToSet(input:String): Set[String] ={
    input.split(",").toSet
  }

  def getNextRecordFull(inputStream: InputStream) = {
    if (UnpackFlowFile.nextHeader(inputStream)){
      val attrs = UnpackFlowFile.getAttributes(inputStream)
      val payload = UnpackFlowFile.getPayload(inputStream)
      Option( (attrs, payload) )
    } else {
      None
    }
  }


  def dumpAllAttrs(is:InputStream): Unit = {
//    Iterator.continually( UnpackFlowFile.getAttributes(is) ).foreach( x => println(x))   This doesn't work

  }


}

case class ExtractCmdLineConfig(
                               inputFile:File=new File("test.flowfile.out"),
                               outputDir:File=new File("."),
                               all:Boolean=false,
                               jexlMatchCmd:String="",
                               simpleMatchAttr:String="",
                               simpleMatchValues:String=""
                               )