package dev.glitch.flowfile.reader.filter

import org.apache.commons.lang3.StringUtils

/**
 * Implements simple filter
 *   Attributes contains a specified value
 */

class SimpleFilter(attrToMatch:String="uuid", valuesToMatch:Set[String]) extends Filter {
  override def matches(attrs: Map[String, String]): Boolean = {
    attrs.contains(attrToMatch) && valuesToMatch.contains(attrs.get(attrToMatch).get)
  }
}

object SimpleFilter extends Filter {
  def apply(attrToMatch:String="uuid", valuesToMatch:Set[String]):SimpleFilter = {
    val a = if(StringUtils.isEmpty(attrToMatch)) "uuid" else attrToMatch
    new SimpleFilter(a, valuesToMatch)
  }
}
