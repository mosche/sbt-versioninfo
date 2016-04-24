package mosche.sbtversioninfo.internal

import com.typesafe.sbt.SbtGit.GitKeys.{formattedDateVersion, gitHeadCommit}
import mosche.sbtversioninfo.VersionInfoKeys._
import mosche.sbtversioninfo.internal.GitExtensions.Commit
import sbt.Keys._
import sbt._

import scala.util.parsing.json._

object VersionFile {
  import DependencyChecks.Keys._
  import GitExtensions.Keys._

  def settings = Seq(
    resourceGenerators in Compile <+= Def.task {

      val dependencies = trackedOrganizationDependencies.value.fold(Map.empty[String, JSONType])(deps =>
        Map("dependencies" -> JSONObject(deps.map{ case (name, modules) => name -> JSONArray(modules.map(_.version).distinct.toList)}))
      )

      val remoteDependencies = organizationClients.value.fold(Map.empty[String, JSONType])(deps =>
        Map("remoteDependencies" -> JSONObject(deps.map{ case (name, module) => name -> module.version}))
      )

      val git = gitHeadCommit.value.fold(Map.empty[String, JSONType]) { gitHead =>
        def toJson(c: Commit) = JSONObject(Map(
          "author" -> c.author,
          "date" -> c.date,
          "message" -> c.message
        ))

        Map("git" -> JSONObject(Map[String, Any](
          "head" -> gitHead,
          "lastCommits" -> JSONArray(gitLastCommits.value.map(toJson).toList)
        )))
      }

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

