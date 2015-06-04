package dev.glitch.flowfile.reader.filter

import org.apache.commons.jexl2.{Expression, MapContext, JexlEngine}

/**
 * Use commons-jexl to do boolean logic evaluation on attributes
 */
class JexlFilter(val expressionString:String) extends Filter {

  val engine:JexlEngine = new JexlEngine() // Could make companion object and use the Jexl Caching if desired, but not necessary
  val expression:Expression = engine.createExpression(expressionString.toLowerCase)

  override def matches(attributes:Map[String,String]): Boolean = {
    val context = new MapContext()
    for ( (k,v) <- attributes ) context.set(k.toLowerCase,v.toLowerCase)
    Boolean.unbox(expression.evaluate(context))
  }

}
