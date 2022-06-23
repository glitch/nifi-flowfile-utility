package dev.glitch.flowfile.builder

import java.io._
import scala.collection.JavaConversions._
import org.apache.nifi.util.{FlowFilePackager, FlowFilePackagerV3}

/**
 * Simple utility for building an Apache-NiFi FlowFile bundle based on a configuration file visible on classpath
 */
object FlowFileBuilder {
  
    def main(args : Array[String]) {

        val parser = new scopt.OptionParser[FlowFileBuilderCmdLineConfig]("scopt") {
          head("scopt", "4.x")
          help("help").text("See the options below & reference.json file in jar for example")
          opt[String]("config") action { (x,config) => config.copy(configFile = x)} text "Required: Config file containing FlowFile entry descriptions"
          opt[File]("out") action { (x,config) => config.copy(outputFile = x)} text "Output file to write to"
        }

        parser.parse(args, FlowFileBuilderCmdLineConfig()) match {
          case Some(cmdLineConfig) =>

            // TODO: Extract into methods for better testing & modularity

            // Validate cmd line args
            // validateCmdLineConfig(cmdLineConfig) // TODO flow control / reporting on bad config

            val out = new FileOutputStream(cmdLineConfig.outputFile)
            val configName = if (cmdLineConfig.configFile.isEmpty) "reference" else cmdLineConfig.configFile
            BuilderConfiguration( configName ).entries.foreach{ entry =>

              // TODO Change these to loggers
              println("-----")
              println("attributes -> " + entry.attr)
              println("payload-file -> " + entry.payloadFile)

              val payload = inputStreamToByteArrayCommons(new FileInputStream(entry.payloadFile))
              writeToFlowFile(entry.attr, new ByteArrayInputStream(payload), out, payload.length)
            }
            out.close()

          case None =>
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

  def inputStreamToByteArrayCommons(is:InputStream): Array[Byte] =
    org.apache.commons.io.IOUtils.toByteArray(is)

  def validateCmdLineConfig(config: FlowFileBuilderCmdLineConfig): Boolean = {
    // TODO validate cmd line args
    false
  }

}

case class FlowFileBuilderCmdLineConfig(configFile:String="", outputFile: File = new File("test.flowfile.pkg"))