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



## Testing

Docs: 

- [How to configure an application for testing (via Guice)?](https://www.playframework.com/documentation/latest/JavaTestingWithGuice)

- [How to do functional testing (fake server, etc)?](https://www.playframework.com/documentation/latest/JavaFunctionalTest)


### Fake Server

In your tests you can create one or multiple fake servers, i.e. a actual servers to which you can send standard HTTP requests. Either by using `TestServer`, e.g.

```java
TestServer testServer = new TestServer(9000, Helpers.fakeApplication());
testServer.start();
// ... or this:
TestServer testServer = Helpers.testServer(9000, Helpers.fakeApplication());
```

Or via  `extends WithServer` which is simply a wrapper around `TestServer`. You might want to  `@Override`  the methods `int providePort()` and `Application provideApplication()`. Note that  `startServer()` uses `@Before`, i.e. it will start the server automatically in tests before running tests.

If you to use multiple servers, create a factory, e.g:

```java
public class FakeServiceOneFactory {
    public static TestServer defaults(final int port) {
        TestServer testServer = Helpers.testServer(port, Helpers.fakeApplication());
        return Helpers.testServer(port, Helpers.fakeApplication());
    }

    public static TestServer configured(final int port, final Application application){
        return Helpers.testServer(port, application);
    }
}
```

**Notes**

- If you use multiple servers then static variables have the same memory reference (i.e. are equal), because Guice wires them together from the same classes (duh...)


### Functional testing with multiple projects at once

With some [sbt magic](https://www.scala-sbt.org/release/docs/Multi-Project.html#Per-configuration+classpath+dependencies) we can create a sub-project solely for testing which depends on multiple microservices and has access to all classes, including classes in `test/`. So you can simulate a whole network of microservices from code. 

```scala
lazy val `functest` = (project in file("functest"))
  .settings(
    name := "functest",
    version := "0.1",
    libraryDependencies ++= Seq(
      junit,
    )
  )
  .dependsOn(
    serviceOne % "compile->compile;test->test",
    serviceTwo % "compile->compile;test->test",
    commons % "compile->compile;test->test"
  )
```

**Notes**

- If you try to create different configurations from that testing project, then you might run into dependency problems due to naming collisions in the classpath, because the classes are somehow all thrown together into their respective packages with no unique identifier. E.g. if your serviceOne and serviceTwo both have class in `src/main/java/utils/Utils.java` then, in the testing project, both are accessible via `import utils.Utils`. Everything compiles just fine but the import points to the last project defined in `build.sbt` which uses this classpath, i.e. in the example above the Utils from serviceTwo would win.

  - Probably best way is to preconfigure everything in the respective project, so that you can simply do this: 

    ```java
    new FakeFullyConnfiguredServiceOne().run()
    ```

  - You could also encapsulate all project classes in a unique package. E.g. 
    `src/main/java/utils/Utils.java` to `src/main/java/serviceone/utils/Utils.java`
    Then it would be imported via `import serviceone.utils.Utils`

- Logback also complains when it multiple projects are thrown together: `Resource [logback.xml] occurs multiple times on the classpath`. You could run with the following (see [docs](http://logback.qos.ch/manual/configuration.html)): 

  ```
  -Dlogback.configurationFile=/path/to/config.xml
  ```

  If all your services use the same logger configuration, then you can simply put it into the commons project and this problem doesn't arise in the first place.



## Encountered Problems

### Routes files get confused

- There was a problem when running tests for multiple services at once. Somehow the routes file of one service was used as routes for another service throwing the "Action not found" error (which shows the "tried routes"). Both fake servers for the services were set up in their retrospective and that technically shouldn't have occurred? Problem was resolved by running each service as sbt project (which applies some black magic no one knows about).
- If that happens again you can try to give unique names to the route files. E.g. `play.http.router=hello.Routes` in `application.conf`. See [here](https://stackoverflow.com/questions/33627062/using-play-2-4-with-a-renamed-conf-routes-file).





## Other Nice2Knows

- When you define a new sub-project in the `build.sbt` and reimport, then it creates the needed folders. You just need to create the `src/` manually. So no need to use IntelliJ for scaffolding. IntelliJ project configuration (i.e. what the sources, resources, tests folder are) is also automatically set by sbt.

- To make a sbt project available in another sbt project you must include it in `.dependsOn(SOME_SBT_PROJECT)` of the project specification where you want to use it. You can even make a Play project depend on another project. But maybe there will be problems with build size or spaghetti code?

- The `PlayJava` plugin comes with a shit load of dependencies. Ctrl + left click on `guice` in `build.sbt` to see them.

- To use a setting for all projects at once you can add `in ThisBuild`, e.g. 

  ```scala
  scalaVersion in ThisBuild := "2.13.1"
  ```

- To use Akka TYPED, inject the ActorSystem, spawn typed guardian actor via Adapter.spawn(), see [here](https://www.playframework.com/documentation/2.8.x/AkkaTyped#Compile-time-dependency-injection).

## Todo

### Run all tests in all modules at once

We can run tests in a single project from the IDE as usual, but running all tests of all projects doesn't work out of the box. 

- Probably solvable via sbt Tasks, see [docs](https://www.scala-sbt.org/1.x/docs/Tasks.html) and [this example](https://stackoverflow.com/a/22321779/4179212).
- You could technically also use Junit suites and call them from the root project tests, see "How to use code in the root project (e.g. for tests)?". But that's probably a shitty solution since you need to manually make sure that every single test is included.

