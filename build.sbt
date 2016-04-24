import com.typesafe.sbt.SbtScalariform

name := "sbt-versioninfo"

description := "Sbt sample plugin generating a json file with details about build version and dependencies."

addSbtPlugin("org.scalariform" % "sbt-scalariform" % "1.4.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.8.5")

addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.8.0")

sbtPlugin := true

scalacOptions := Seq("-unchecked", "-deprecation", "-feature", "-language:implicitConversions")

SbtScalariform.scalariformSettings