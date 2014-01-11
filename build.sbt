name := "ReactiveCouchbase_SecureSocial_Sample"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  cache,
  "securesocial" %% "securesocial" % "2.1.2",
  "org.reactivecouchbase" %% "reactivecouchbase-play" % "0.1-SNAPSHOT"
)

resolvers ++= Seq(
  Resolver.url("sbt-plugin-releases", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns),
  "ReactiveCouchbase repository" at "https://raw.github.com/ReactiveCouchbase/repository/master/snapshots"
)

play.Project.playScalaSettings
