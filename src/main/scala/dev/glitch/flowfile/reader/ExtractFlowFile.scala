package dev.glitch.flowfile.reader

import java.io.{FileOutputStream, InputStream, FileInputStream, File}

import dev.glitch.flowfile.reader.filter.{JexlFilter, SimpleFilter, Filter}

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
      opt[Boolean]("all") action { (x,config) => config.copy(all = true)} text "Flag to dump all records in flow file bundle"
      opt[String]("jexl") action { (x,config) => config.copy(jexlMatchCmd = x)} text "Advanced Match: JEXL query to match against"
      opt[String]("attrMatch") action { (x,config) => config.copy(simpleMatchAttr = x)} text "Simple Match: Attribute to match against"
      opt[String]("attrValue") action { (x,config) => config.copy(simpleMatchValues = x)} text "Simple Match: CSV list of values to hit on"
    }

    parser.parse(args, ExtractCmdLineConfig()) match {
      case Some(cmdLineConfig) =>

        val inputStream = new FileInputStream(cmdLineConfig.inputFile)
        val filter = buildFilter(cmdLineConfig)
        var counter = 0

        while (UnpackFlowFile.nextHeader(inputStream)) {
          val attrs = UnpackFlowFile.getAttributes(inputStream)

          // Based on cmd line args, test the attrs & take appropriate action with the payload
          if (cmdLineConfig.all || filter.matches(attrs)) {
            println("Matched record: " + counter + "  Attributes: " + attrs)

            // Output payload based on switch

          }

//          val payload = UnpackFlowFile.getPayload(inputStream)
          val payload = UnpackFlowFile.skipPayload(inputStream)

          counter += 1
        }

        inputStream.close()

      case None =>
    }

  }

  def buildFilter(config:ExtractCmdLineConfig):Filter = {
    if (config.simpleMatchValues.nonEmpty) {
      SimpleFilter(config.simpleMatchAttr, csvToSet(config.simpleMatchValues))
    } else if (config.jexlMatchCmd.nonEmpty) {
      new JexlFilter(config.jexlMatchCmd)
    } else {
      new {} with Filter {}
    }
  }

  def csvToSet(input:String): Set[String] ={
    input.split(",").toSet
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