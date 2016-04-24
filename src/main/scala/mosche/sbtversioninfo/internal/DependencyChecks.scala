package mosche.sbtversioninfo.internal

import mosche.sbtversioninfo.VersionInfoKeys._
import net.virtualvoid.sbt.graph.DependencyGraphPlugin.autoImport._
import net.virtualvoid.sbt.graph.{ModuleGraph, ModuleId}
import sbt.Keys._
import sbt.impl.GroupArtifactID
import sbt.{CrossVersion, Def, _}

object DependencyChecks {
  import Keys._

  object Keys {
    private[internal] val organizationDependencies = TaskKey[Seq[ModuleId]]("organizationDependencies", "All organization dependencies collected from the transitive graph")

    private[internal] val organizationClients = TaskKey[Option[Map[String, ModuleId]]]("organizationClients", "All organization client artifacts matching the versionClientNamePattern.")

    private[internal] val trackedOrganizationDependencies = TaskKey[Option[Map[String, Seq[ModuleId]]]]("trackedOrganizationDependencies", "All organization dependencies that are tracked based on the versionDependencyPattern")
  }

  def settings = Seq(
    organizationDependencies <<= Def.task {
      // collect all modules of this organization
      (moduleGraph in Compile).value.nodes.collect {
        case node if node.id.organisation == organization.value => node.id
      }
    },
    trackedOrganizationDependencies <<= Def.task {
      versionDependencyPattern.value.map { Pattern =>
        implicit val graph = (moduleGraph in Compile).value

        // match all organization modules against the pattern setting
        val deps = organizationDependencies.value.map(m => m.name -> m).collect{
          case (Pattern(aggregationName), m) if name != null && !m.isEvicted => aggregationName -> m
        }

        // aggregate by resolved name
        deps.groupBy(_._1).map(t => t._1 -> t._2.map(_._2))
      }
    },
    organizationClients <<= Def.task {
      versionClientNamePattern.value.map { ClientName =>
        implicit val graph = (moduleGraph in Compile).value
        implicit val is = ivyScala.value

        /**
          * Optional client library filter.
          * Clients are expected to depend on at least one of the configured artifacts.
          */
        def usesClientLibrary(m: ModuleId) = versionClientLibraries.value.map(_.exists(lib => m.dependsOn(lib)))

        // collect all modules matching the client name pattern and check the clientLibrary filter
        organizationDependencies.value.map(m => m.name -> m).collect {
          case (ClientName(name), m) if name != null && !m.isEvicted && usesClientLibrary(m).getOrElse(true) => name -> m
        }.toMap
      }
    }
  )

  implicit private[this] class ToModuleIdSyntax(val moduleId: ModuleId) extends AnyVal {

    /**
      * Returns true if the module got evicted during transitive dependency resolution.
      */
    def isEvicted(implicit graph: ModuleGraph): Boolean = {
      graph.module(moduleId).isEvicted
    }

    /**
      * Returns true if the module matches organization and name (ignoring any specific version).
      */
    def matches(artifactId: GroupArtifactID)(implicit is: Option[IvyScala]): Boolean = {
      val rev = artifactId % "0"
      moduleId.organisation == rev.organization && moduleId.name == CrossVersion(rev, is).fold(rev.name)(_(rev.name))
    }

    /**
      * Returns true if the module has a direct dependency on a artifact (ignoring any specific version).
      * This does not consider transitive dependencies.
      */
    def dependsOn(artifact: GroupArtifactID)(implicit graph: ModuleGraph, is: Option[IvyScala]): Boolean = graph.edges.exists {
      case (from, to) => from == moduleId && to.matches(artifact)
    }
  }
}
