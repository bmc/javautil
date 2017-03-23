// SBT build file for JavaUtil

name := """javautil"""

val projectProps = {
  val p = new java.util.Properties
  p.load(new java.io.FileReader("src/main/resources/org/clapper/util/misc/Bundle.properties"))
  p
}

version := projectProps.getProperty("api.version")
organization := "org.clapper"
licenses := Seq("BSD" -> url("https://github.com/bmc/javautil/blob/master/LICENSE.md"))
homepage := Some(url("http://software.clapper.org/javautil/"))
description := "A general-purpose Java utility library"

crossPaths := false
autoScalaLibrary := false

javacOptions in (Compile, compile) ++= Seq("-source", "1.7", "-target", "1.7")
javacOptions in doc ++= Seq("-source", "1.7")

libraryDependencies ++= Seq(
  "javax.activation" % "activation"      % "1.1",
  "javax.mail"       % "mail"            % "1.4.3",
  "asm"              % "asm"             % "3.3.1",
  "asm"              % "asm-commons"     % "3.3.1",
  "commons-logging"  % "commons-logging" % "1.1.1",
  "org.slf4j"        % "slf4j-jdk14"     % "1.6.4",
  "junit"            % "junit"           % "4.12"  % Test,
  "com.novocode"     % "junit-interface" % "0.11" % Test
)

publishMavenStyle := true
publishArtifact in Test := false
pomIncludeRepository := { _ => false }
pomExtra :=
  <scm>
    <url>git@github.com:bmc/javautil.git/</url>
    <connection>scm:git:git@github.com:bmc/javautil.git</connection>
  </scm>
  <developers>
    <developer>
      <id>bmc</id>
      <name>Brian Clapper</name>
      <url>http://www.clapper.org/bmc</url>
    </developer>
  </developers>
