import sbt._
import Keys._

object BuildSettings {
  val buildVersion = "0.2.0"
  val buildScalaVersion = "2.10.0"

  val buildSettings = Defaults.defaultSettings ++ Seq(
    organization := "org.scalaequals",
    version := buildVersion,
    scalaVersion := buildScalaVersion,
    scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature")
  )
}

object Dependencies {
  val scalatest =  "org.scalatest" %% "scalatest" % "2.0.M5b"
  val scalacheck = "org.scalacheck" %% "scalacheck" % "1.10.0"
  val reflect = "org.scala-lang" % "scala-reflect" % BuildSettings.buildScalaVersion
}

object ScalaEqualsBuild extends Build {
  import BuildSettings._
  import Dependencies._

  lazy val root = Project(
    id = "ScalaEquals",
    base = file("."),
    settings = buildSettings) aggregate(core, core_test)

  lazy val core = Project(
    id = "core",
    base = file("core"),
    settings = buildSettings ++ Seq(
      name := "ScalaEquals Core",
      libraryDependencies ++= Seq(reflect)
    ))

  lazy val core_test = Project(
    id = "core-test",
    base = file("core-test"),
    settings = buildSettings ++ Seq(
      name := "ScalaEquals Core Tests",
      libraryDependencies ++= Seq(scalatest, scalacheck)
    )) dependsOn(core)
}