package dev.glitch.flowfile.builder

import scala.collection.JavaConversions._
import com.typesafe.config.{ConfigFactory, Config}

/**
 * Parse config & return BuilderConfiguration
 *   Uses TypeSafe's Config & ConfigFactory for this -> https://github.com/typesafehub/config
 */

case class BuilderConfiguration (entries:Seq[BuilderEntry])
case class BuilderEntry(attr:Map[String,String], payloadFile:String)

object BuilderConfiguration {

  def apply(config: Config): BuilderConfiguration = {
    val entries = config.getConfigList("flowfile-entries")
    val builderEntries = entries.map { c: (Config) =>
      val attrs = c.getConfig("attributes").entrySet().map(f => (f.getKey, f.getValue.render())).toMap
      val payload = c.getString("payload-file")
      BuilderEntry(attrs, payload)
    }
    BuilderConfiguration(builderEntries)
  }

  def apply(configFile: String): BuilderConfiguration = {
    apply(ConfigFactory.load(configFile))
  }

  def apply(): BuilderConfiguration = {
    apply("builder")
  }
}
