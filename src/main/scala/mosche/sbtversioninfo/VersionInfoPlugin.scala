package mosche.sbtversioninfo

import com.typesafe.sbt.GitVersioning
import mosche.sbtversioninfo.VersionInfoKeys._
import mosche.sbtversioninfo.internal.{DependencyChecks, VersionFile}
import net.virtualvoid.sbt.graph.DependencyGraphPlugin
import sbt.Keys._
import sbt._

object VersionInfoPlugin  extends AutoPlugin {

  object autoImport extends VersionInfoKeys

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    versionJson := (resourceManaged in Compile).value / "public" / "version.json",
    versionDependencyPattern := Some("(commons)-\\w+_.*".r),
    versionClientNamePattern := Some("^(\\w+)-(?:client)_.*".r),
    versionClientLibraries := None
  ) ++ VersionFile.settings ++ DependencyChecks.settings

  override def requires: Plugins = GitVersioning && DependencyGraphPlugin

  override def trigger: PluginTrigger = noTrigger
}
