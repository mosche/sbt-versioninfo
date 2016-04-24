package mosche.sbtversioninfo

import sbt._
import sbt.impl.GroupArtifactID

import scala.util.matching.Regex

trait VersionInfoKeys {

  val versionJson = SettingKey[File]("versionFile", "The version file (defaults to public/version.json in the managed resource directory)")

  val versionDependencyPattern = SettingKey[Option[Regex]]("versionDependencyPattern", "A Regex (1 capturing group) to track matching dependencies in your version file.")

  val versionClientNamePattern = SettingKey[Option[Regex]]("versionClientNamePattern", "A Regex (1 capturing group) to identify your client artifacts in case you are building these for your services.")

  val versionClientLibraries = SettingKey[Option[Seq[GroupArtifactID]]]("versionClientLibraries", "Optional additional setting to filter clients based on the usage of by particular dependencies.")
}

object VersionInfoKeys extends VersionInfoKeys
