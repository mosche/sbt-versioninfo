package mosche.sbtversioninfo.internal

import java.util.Date

import com.typesafe.sbt.SbtGit.GitKeys._
import com.typesafe.sbt.SbtGit.git.defaultFormatDateVersion
import com.typesafe.sbt.git.JGit
import mosche.sbtversioninfo.internal.GitExtensions.Keys._
import sbt.{Def, TaskKey}

import scala.collection.JavaConversions._

object GitExtensions {

  case class Commit(author: String, date: String, message: String)

  object Keys {
    private[internal] val gitLastCommits = TaskKey[Seq[Commit]]("gitLastCommits", "The last commits")
  }

  def settings = Seq(
    gitLastCommits <<= Def.task{
      gitReader.value.withGit{
        case git: JGit =>
          git.porcelain.log()
            .setMaxCount(5)
            .call().toSeq
            .map(rev =>
              Commit(rev.getAuthorIdent.getName, defaultFormatDateVersion(None, new Date(rev.getCommitTime.toLong)), rev.getShortMessage)
            )
        case _ =>
          Seq.empty
      }
    }
  )

}
