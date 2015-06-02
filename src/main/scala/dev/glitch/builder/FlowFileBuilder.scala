package dev.glitch.builder

import java.io._
import scala.collection.JavaConversions._
import org.apache.nifi.util.{FlowFilePackager, FlowFilePackagerV3}

/**
 *
 */
object FlowFileBuilder {
  
    def main(args : Array[String]) {

        val parser = new scopt.OptionParser[FlowFileBuilderCmdLineConfig]("scopt") {
          head("scopt", "3.x")
          opt[String]("config") action { (x,config) => config.copy(configFile = x)} text "Config file containg FlowFile entry descriptions"
          opt[File]("out") action { (x,config) => config.copy(outputFile = x)} text "Output file to write to"
        }

        parser.parse(args, FlowFileBuilderCmdLineConfig()) match {
          case Some(cmdLineConfig) =>

            // Validate cmd line args
            val foo = cmdLineConfig.outputFile
            val cf  = cmdLineConfig.configFile

            val out = new FileOutputStream(cmdLineConfig.outputFile)
            val configName = if (cmdLineConfig.configFile.isEmpty) "builder" else cmdLineConfig.configFile
            BuilderConfiguration( configName ).entries.foreach{ entry =>
              println("-----")
              println("attributes -> " + entry.attr)
              println("payload-file -> " + entry.payloadFile)
              val payload = inputStreamToByteArray(new FileInputStream(entry.payloadFile))
              writeToFlowFile(entry.attr, new ByteArrayInputStream(payload), out, payload.length)
            }

          case None => println("No cmdline options were given")
          // arguments are bad, error message will have been displayed
        }



      println("Done")
    }

  def writeToFlowFile(attrs:Map[String,String], iStream: InputStream, oStream:OutputStream, payloadSize:Long): Unit = {
    val packager:FlowFilePackager = new FlowFilePackagerV3()
    packager.packageFlowFile(iStream, oStream, attrs, payloadSize)
  }

  def inputStreamToByteArray(is: InputStream): Array[Byte] =
    Iterator continually is.read takeWhile (-1 !=) map (_.toByte) toArray
}

case class FlowFileBuilderCmdLineConfig(configFile:String="", outputFile: File = new File("test.flowfile.out"))