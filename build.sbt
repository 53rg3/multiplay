name := "multiplay"

version := "0.1"

scalaVersion in ThisBuild := "2.13.1"


// ------------------------------------------------------------------------------------------ //
// SERVICE 1
// ------------------------------------------------------------------------------------------ //

lazy val `serviceOne` = (project in file("serviceone"))
  .enablePlugins(PlayJava)
  .settings(
    name := "serviceOne",
    version := "0.1",
    libraryDependencies ++= Seq(
      guice, // contained in PlayJava
      junit
    )
  )
  .dependsOn(commons)


// ------------------------------------------------------------------------------------------ //
// SERVICE 2
// ------------------------------------------------------------------------------------------ //

lazy val `serviceTwo` = (project in file("servicetwo"))
  .enablePlugins(PlayJava)
  .settings(
    name := "serviceTwo",
    version := "0.1",
    libraryDependencies ++= Seq(
      guice, // contained in PlayJava
      junit
    )
  )
  .dependsOn(commons)


// ------------------------------------------------------------------------------------------ //
// COMMONS PROJECT
// ------------------------------------------------------------------------------------------ //

lazy val `commons` = (project in file("commons"))
  .settings(
    name := "commons",
    version := "0.1",
    libraryDependencies ++= Seq(
      "junit" % "junit" % "4.12" % Test
    )
  )
  .dependsOn()


// ------------------------------------------------------------------------------------------ //
// DEPENDENCIES
// ------------------------------------------------------------------------------------------ //

val junit = "junit" % "junit" % "4.12" % Test
