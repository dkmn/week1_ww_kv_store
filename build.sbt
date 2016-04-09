// Enable the sbt-native-packager plugin (see project/plugins.sbt)
val stifleSillyIDEerror = enablePlugins(JavaServerAppPackaging)

name := """week_1_ww_kv_store"""

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= {
  val akkaVersion = "2.4.3"     // DRY out the Akka version
  Seq(
    // Akka goodies we'll use...
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,

    // Logging stuff...
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "ch.qos.logback"    %  "logback-classic" % "1.1.3",

    // Test stuff...
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
    "org.scalatest" %% "scalatest" % "2.2.6" % "test"
  )
}

fork in run := true