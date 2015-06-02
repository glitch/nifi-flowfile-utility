package dev.glitch.flowfile.reader

/**
 *
 */
trait Filter {

  def matches(attrs:Map[String,String]): Boolean
}
