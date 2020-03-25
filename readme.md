# Multi Play

## Todo

- dependencies
- tests
- depend on other Play project possible? Can we somehow run both at once?



## Notes

- For official documentation concerning sbt multi-project see [here](https://www.scala-sbt.org/1.x/docs/Multi-Project.html).
- For another multi project example (2 projects which also have sub-projects) see [here](https://github.com/mariussoutier/play-multi-multi-project).  
- All project specifications are packed into a single `build.sbt` file. If you want to split that see [here](https://www.scala-sbt.org/1.x/docs/Scala-Files-Example.html).
- We use the `PlayJava` as Play plugin, i.e. with templating and other stuff microservices don't need. There's also `PlayService` and `PlayMinimalJava`, but no documentation how to actually use them...



## How to set up a multi sbt project?

1. Create new `sbt` project in IntelliJ.

   - Delete `src/` folder.

   - If you want to use the same Scala version throughout all projects define `scalaVersion` in `build.sbt` as:

     ```
     scalaVersion in ThisBuild := "2.13.1"
     ```

2. Create new Play project: `sbt new playframework/play-java-seed.g8`

   - Delete folder `.g8` & `build.sbt`.
   - The root project needs a specification of the `PlayJava` plugin. Simply copy `plugins.sbt` from the Play project's `project/` folder into the `project/` folder of the root project.

3. Repeat step 2 for the second project.

4. Create a new `sbt` project in IntelliJ in the root project, e.g. "commons".

   - Delete `.idea` folder and `build.sbt`.
   - Rename scala folders in `src/`  to java.

5. Define the 3 projects in the `build.sbt` of the root project. See definition in this Git repo. The only difference between a Play project and a common sbt project is that you must use `.enablePlugins(PlayJava)` additionally. E.g.

   ```scala
   lazy val `serviceOne` = (project in file("serviceone"))
     .enablePlugins(PlayJava)
     .settings(
       name := "serviceOne",
       version := "0.1",
       libraryDependencies ++= Seq(
         guice
       )
     )
     .dependsOn()
   ```

6. Reimport root project: Right pane > sbt > right click > reimport. IntelliJ configures Project Settings automatically (sources, tests, resources, etc). 

7. Done. You might also want to delete the `views/` and `public/` folders in the Play projects, assuming your microservices aren't websites.



## How to run a single Play instance in IntelliJ

To run one of the Play projects it must be defined as standard sbt task:

- "Run/Debug Configurations"
- Add new configuation (+)
- sbt Task
- In Tasks input field insert: `"project YOUR_PROJECT" run`
- Make sure the working directory is the root project.



## Nice2Knows

- To make a sbt project available in another sbt project you must include it in `.dependsOn(SOME_SBT_PROJECT)` of the project specification where you want to use it.
- The PlayJava plugin comes with a shit load of dependencies. Ctrl + left click on `guice` in `build.sbt` to see them.
- 