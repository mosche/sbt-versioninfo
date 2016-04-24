package mosche.sbtversioninfo.internal

import com.typesafe.sbt.GitPlugin.autoImport.git.{formattedDateVersion, gitHeadCommit}
import mosche.sbtversioninfo.VersionInfoKeys._
import sbt.Keys._
import sbt._

import scala.util.parsing.json._

object VersionFile {
  import DependencyChecks.Keys._

  def settings = Seq(
    resourceGenerators in Compile <+= Def.task {

      val dependencies = trackedOrganizationDependencies.value.fold(Map.empty[String, JSONType])(deps =>
        Map("dependencies" -> JSONObject(deps.map{ case (name, modules) => name -> JSONArray(modules.map(_.version).distinct.toList)}))
      )

      val remoteDependencies = organizationClients.value.fold(Map.empty[String, JSONType])(deps =>
        Map("remoteDependencies" -> JSONObject(deps.map{ case (name, module) => name -> module.version}))
      )

      val git = gitHeadCommit.value.fold(Map.empty[String, JSONType])(gitHead =>
        Map("git" -> JSONObject(Map(
          "head" -> gitHead
        )))
      )

      val json = JSONObject(Map(
        "name" -> name.value,
        "version" -> version.value,
        "versionDate" -> formattedDateVersion.value
      ) ++ git ++ dependencies ++ remoteDependencies)

      IO.write(versionJson.value, json.toString(JSONFormat.defaultFormatter).getBytes)
      Seq(versionJson.value)
    }
  )
}

