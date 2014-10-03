import AssemblyKeys._

assemblySettings

jarName in assembly := "AuroraFinagleServer.jar"

mergeStrategy in assembly := {
  case PathList(ps @ _*) if ps.last.startsWith("cmdline.arg.info.txt") => MergeStrategy.concat
  case PathList(ps @ _*) if Assembly.isReadme(ps.last) || Assembly.isLicenseFile(ps.last) =>
    MergeStrategy.rename
  case PathList("META-INF", xs @ _*) =>
    (xs map {_.toLowerCase}) match {
      case ("manifest.mf" :: Nil) | ("index.list" :: Nil) | ("dependencies" :: Nil) =>
        MergeStrategy.discard
      case _ => MergeStrategy.deduplicate
    }
  case _ => MergeStrategy.deduplicate
}

name := "aurora-finagle-server"

version := "1.0"

resolvers ++= Seq(
  "Twitter Maven" at "http://maven.twttr.com/",
  "Maven Central" at "http://repo1.maven.org"
)

libraryDependencies ++= Seq(
  "com.twitter" %% "finagle-http" % "6.20.0",
  "com.twitter" %% "finagle-serversets" % "6.20.0"
)
