name := "multiplay"

version := "0.1"

scalaVersion in ThisBuild := "2.13.1"



lazy val `serviceOne` = (project in file("serviceone"))
  .enablePlugins(PlayJava)
  .settings(
    name := "serviceOne",
    version := "0.1",
    libraryDependencies ++= Seq(
      guice
    )
  )
  .dependsOn(commons, serviceTwo)

lazy val `serviceTwo` = (project in file("servicetwo"))
  .enablePlugins(PlayJava)
  .settings(
    name := "serviceTwo",
    version := "0.1",
    libraryDependencies ++= Seq(
      guice
    )
  )
  .dependsOn(commons)

lazy val `commons` = (project in file("commons"))
  .settings(
    name := "commons",
    version := "0.1",
    libraryDependencies ++= Seq(

    )
  )
  .dependsOn()
