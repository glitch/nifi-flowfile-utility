package dev.glitch.flowfile.reader.filter

/**
 *
 */
trait Filter {

  def matches(attrs:Map[String,String]): Boolean = { false }
}
