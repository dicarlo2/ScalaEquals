/*
 * Copyright (c) 2013 Alex DiCarlo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import sbt._
import Keys._

object BuildSettings {
  val buildVersion = "1.2.0"
  val buildScalaVersion = "2.10.0"

  val buildSettings = Defaults.defaultSettings ++ Seq(
    organization := "org.scalaequals",
    version := buildVersion,
    scalaVersion := buildScalaVersion,
    scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature"),

    publishTo <<= version { (v: String) =>
      val nexus = "https://oss.sonatype.org/"
      if (v.trim.endsWith("SNAPSHOT"))
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases"  at nexus + "service/local/staging/deploy/maven2")
    },
    pomIncludeRepository := { _ => false },
    publishArtifact in Test := false,
    pomExtra := (
      <url>http://scalaequals.org/</url>
        <licenses>
          <license>
            <name>MIT-style</name>
            <url>http://opensource.org/licenses/MIT</url>
            <distribution>repo</distribution>
          </license>
        </licenses>
        <scm>
          <url>git@github.com:dicarlo2/ScalaEquals.git</url>
          <connection>scm:git:git@github.com:dicarlo2/ScalaEquals.git</connection>
        </scm>
        <developers>
          <developer>
            <id>dicarlo2</id>
            <name>Alex DiCarlo</name>
            <email>alexdicarlo@gmail.com/</email>
          </developer>
        </developers>)
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
    settings = buildSettings ++ Seq(
      publishArtifact in (Compile, packageBin) := false,
      publishArtifact in (Compile, packageSrc) := false,
      publishArtifact in (Compile, packageDoc) := false
    )) aggregate(core, core_test)

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
      libraryDependencies ++= Seq(scalatest, scalacheck),
      publish := (),
      publishLocal := ()
    )) dependsOn(core)
}