package dev.glitch.flowfile.reader

import java.io.{FileOutputStream, FileInputStream, File}

import dev.glitch.flowfile.reader.filter.{JexlFilter, SimpleFilter, Filter}

/**
 * Utility to extract attributes & payloads from a flow file
 */
class ExtractFlowFile {

  def main(args:Array[String]) {

    // Cmd line options parsing
    val parser = new scopt.OptionParser[ExtractCmdLineConfig]("scopt") {
      head("scopt", "3.x")
      opt[String]("in") action { (x,config) => config.copy(inputFile = x)} text "flow file bundle to read"
      opt[String]("out") action { (x,config) => config.copy(outputDir = x)} text "Output directory to write .attr & .bytes files to"
      opt[Boolean]("all") action { (x,config) => config.copy(all = true)} text "Flag to dump all records in flow file bundle"
      opt[String]("jexl") action { (x,config) => config.copy(jexlMatchCmd = x)} text "Advanced Match: JEXL query to match against"
      opt[String]("attrMatch") action { (x,config) => config.copy(simpleMatchAttr = x)} text "Simple Match: Attribute to match against"
      opt[String]("attrValue") action { (x,config) => config.copy(simpleMatchValues = x)} text "Simple Match: CSV list of values to hit on"
    }

    parser.parse(args, ExtractCmdLineConfig()) match {
      case Some(cmdLineConfig) =>

        val inputFile = new File(cmdLineConfig.inputFile)
        val inputStream = new FileInputStream(inputFile)
        val filter = buildFilter(cmdLineConfig)

        var counter = 0 // Used for generating file output names
        while (UnpackFlowFile.nextHeader(inputStream)) {

          val attrs = UnpackFlowFile.getAttributes(inputStream)

          // Based on cmd line args, test the attrs & take appropriate action with the payload
          val matches = cmdLineConfig.all || filter.matches(attrs)
          if (matches) println("Matched record: " + counter + "  Attributes: " + attrs)

          // TODO more options for outputting payloads
          if (matches && cmdLineConfig.outputDir.nonEmpty) {
            val baseName = inputFile.getName + "_" + counter
            printToFile(new File(baseName+".attrs")) { p => for ( (k,v) <- attrs ) p.println(k+":"+v) }
            UnpackFlowFile.writePayload(inputStream, new FileOutputStream(baseName+".payload"))

          } else {
            UnpackFlowFile.skipPayload(inputStream)
          }

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
      JexlFilter(config.jexlMatchCmd)
    } else {
      new {} with Filter {}
    }
  }

  def csvToSet(input:String): Set[String] = {
    input.split(",").toSet
  }

  /**
   * Found on StackOverflow somewhere by Rex Kerr
   */
  def printToFile(f: java.io.File)(op: java.io.PrintWriter => Unit) {
    val p = new java.io.PrintWriter(f)
    try { op(p) } finally { p.close() }
  }

//  and it's used like this:
//  val data = Array("Five","strings","in","a","file!")
//  printToFile(new File("example.txt")) { p =>
//    data.foreach(p.println)
//  }

}

case class ExtractCmdLineConfig(
                               inputFile:String = "test.flowfile.pkg",
                               outputDir:String = "",
                               all:Boolean = false,
                               jexlMatchCmd:String = "",
                               simpleMatchAttr:String = "",
                               simpleMatchValues:String = ""
                               )