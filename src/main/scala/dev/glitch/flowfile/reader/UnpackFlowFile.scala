package dev.glitch.flowfile.reader

import java.io.{IOException, ByteArrayOutputStream, OutputStream, InputStream}
import java.util

import org.apache.nifi.util.{FlowFilePackagerV3, FlowFileUnpackagerV3}
import scala.collection.JavaConversions._

/**
 * Simple utility object for manipulating in/out streams of flow file.
 * It is *VERY* fragile since it is not maintaining the state of the input stream.
 * It's up to the invoker to manage that appropriately.
 *
 * Order of operations are
 * 1. nextHeader -> advances stream, returns true if there is a valid header
 * 2. getAttributes -> reads & returns attributes from stream
 * 3. get/write/skip Payload ->
 *        getPayload -> returns Array[Byte] of the payload
 *        writePayload -> write payload into provided outputstream
 * 4. repeat step 1->3
 */
object UnpackFlowFile extends FlowFileUnpackagerV3 {

  def nextHeader(inputStream: InputStream): Boolean = {
    val header:Option[Array[Byte]] = readHeader(inputStream)
    // Check that it is FFv3
    if (header.nonEmpty && !util.Arrays.equals(header.get, FlowFilePackagerV3.MAGIC_HEADER)) throw new IOException("Not in FlowFile-v3 format")
    header.nonEmpty
  }

  def getAttributes(inputStream:InputStream): Map[String,String] ={
    super.readAttributes(inputStream).toMap
  }

  def getPayload(inputStream:InputStream): Array[Byte] ={
    val payloadLength = super.readLong(inputStream)
    val payload = new ByteArrayOutputStream(payloadLength.toInt)
    super.copy(inputStream, payload, payloadLength)
    payload.toByteArray
  }

  def writePayload(inputStream: InputStream, outputStream: OutputStream): Unit ={
    super.copy(inputStream, outputStream, super.readLong(inputStream))
    outputStream.close()
  }

  def skipPayload(inputStream: InputStream): Unit ={
    inputStream.skip(super.readLong(inputStream))
  }

  @throws(classOf[IOException])
  private def readHeader(in: InputStream): Option[Array[Byte]] = {
    val header = new Array[Byte](FlowFilePackagerV3.MAGIC_HEADER.length)
    var next:Int = 0
    for(i <- 0 to header.length-1) {
      next = in.read
      if (next < 0) {
        if (i==0) {
          return None
        }
        throw new IOException("Not in FlowFile-v3 format")
      }
      header(i) = (next & 0xFF).toByte
    }
    Some(header)
  }
}
