package dev.glitch.builder

import java.io.File

/**
 *
 */
object FlowFileBuilder {
  
    def main(args : Array[String]) {
        println( "Anoop!" )

//        val parser = new scopt.OptionParser[BuilderCmdLineConfig]("scopt") {
//          head("scopt", "3.x")
//          opt[File]("attr") action { (x,config) => config.copy(attributesInputFile = x)}
//          opt[File]("payload") action { (x,config) => config.copy(attributesInputFile = x)}
//          opt[File]("out") action { (x,config) => config.copy(attributesInputFile = x)}
//        }
//
//        parser.parse(args, BuilderCmdLineConfig()) match {
//          case Some(config) =>
//
//
//          case None =>
//          // arguments are bad, error message will have been displayed
//        }


      val config = BuilderConfiguration("builder")
      config.entries.foreach{ entry =>
        println("-----")
        println("attributes -> " + entry.attr)
        println("payload-file -> " + entry.payloadFile)
      }
      println("Done reading config")
    }

}

case class FlowFileBuilderCmdLineConfig(attributesInputFile: File = new File("."), payloadInputFile: File = new File("."), outputFile: File = new File("."))