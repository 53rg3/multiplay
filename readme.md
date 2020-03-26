# Multi Play

Multiple Play microservices in one project so internal dependencies can be shared, without the need of having code duplication and other inconvenient stuff.



## Notes

- For official documentation concerning sbt multi-projects see [here](https://www.scala-sbt.org/1.x/docs/Multi-Project.html).
- For another multi project example (2 projects which also have sub-projects) see [here](https://github.com/mariussoutier/play-multi-multi-project).  
- All project specifications are packed into a single `build.sbt` file. If you want to split that see [here](https://www.scala-sbt.org/1.x/docs/Scala-Files-Example.html).
- We use the `PlayJava` as Play plugin, i.e. with templating and other stuff microservices don't need. There's also `PlayService` and `PlayMinimalJava`, but no documentation how to actually use them...



## How to set up a multi sbt project?

1. Create new `sbt` project in IntelliJ.

   - Delete `src/` folder, if you don't want to use code in the root project (e.g. for tests).

   - If you want to use the same Scala version throughout all projects define `scalaVersion` in `build.sbt` as:

     ```scala
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

1. "Run/Debug Configurations"

2. Add new configuration (+)

3. Choose "sbt Task"

4. In Tasks input field insert: `"project YOUR_PROJECT" run`

5. Make sure the working directory is the root project.

Hot-loading works as usual.



## How to add library dependencies?

Either define a dependency directly in the `libraryDependencies` of a project:

```scala
libraryDependencies ++= Seq(
	"junit" % "junit" % "4.12" % Test
)
```

Or create a variable out of it and add it the same way:

```scala
val junit = "junit" % "junit" % "4.12" % Test
```



## How to use code in the root project (e.g. for tests)?

You need to define the root project as its own project in its  `build.sbt`:

```scala
lazy val root = project.in(file("."))
  .settings(
    libraryDependencies ++= Seq(
      junit
    )
  )
  .dependsOn(serviceOne, serviceTwo, commons)
  .aggregate(serviceOne, serviceTwo, commons)
```

All classes from the projects in `.dependsOn()` are then available in the root project. 

Not sure if useful... or maybe not. We could create our own [ApplicationLoaders](https://www.playframework.com/documentation/2.8.x/JavaDependencyInjection#Advanced:-Extending-the-GuiceApplicationLoader) and run actual unit tests with multiple services running at once. Maybe.



## How to use Guice?

Works as usual. You can put interfaces into the commons project, implement them in a service project and autowire it via Guice. See `ServiceOneController.java`



## How to build a single subproject?

Either

```
> sbt
> project yourService
> dist
```

Or as single line:

```
> sbt yourService/dist
```



## Other Nice2Knows

- To make a sbt project available in another sbt project you must include it in `.dependsOn(SOME_SBT_PROJECT)` of the project specification where you want to use it. You can even make a Play project depend on another project. But maybe there will be problems with build size or spaghetti code?

- The `PlayJava` plugin comes with a shit load of dependencies. Ctrl + left click on `guice` in `build.sbt` to see them.

- To use a setting for all projects at once you can add `in ThisBuild`, e.g. 

  ```scala
  scalaVersion in ThisBuild := "2.13.1"
  ```



## Todo

### Run all tests in all modules at once

We can run tests in a single project from the IDE as usual, but running all tests of all projects doesn't work out of the box. 

- Probably solvable via sbt Tasks, see [docs](https://www.scala-sbt.org/1.x/docs/Tasks.html) and [this example](https://stackoverflow.com/a/22321779/4179212).
- You could technically also use Junit suites and call them from the root project tests, see "How to use code in the root project (e.g. for tests)?". But that's probably a shitty solution since you need to manually make sure that every single test is included.