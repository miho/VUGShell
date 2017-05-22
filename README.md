# VUGShell

[ ![Download](https://api.bintray.com/packages/miho/UG/VUGShell/images/download.svg) ](https://bintray.com/miho/UG/VUGShell/_latestVersion)

API for accessing precompiled UGShell/[ug4](https://github.com/UG4/ugcore) distribution via [VRL](http://vrl-studio.mihosoft.eu/) plugins and Java projects

## Usage:

To execute lua code, the following method can be used:
```java
// execute code with working directory "exampleDir"
Shell.execute(exampleDir, "print(\"Hello from Java!\")");
```
To execute a specified ug *.lua script, use:
```java
// execute laplace.lua with working directory "exampleDir"
Shell.execute(exampleDir, new File(exampleDir,"laplace.lua"));
```
To print the output to given print streams, use the `print(...)` method:
```java
// execute laplace.lua with working directory "exampleDir"
Shell.execute(exampleDir, new File(exampleDir,"laplace.lua")).print(System.out,System.err);
```
To wait until the execution has finished, use the `waitFor()` method:
```java
// execute laplace.lua with working directory "exampleDir"
Shell.execute(exampleDir, new File(exampleDir,"laplace.lua")).waitFor();
```
The exit value can be accessed via:
```java
// execute laplace.lua with working directory "exampleDir"
Shell.execute(exampleDir, new File(exampleDir,"laplace.lua")).getProcess().exitValue();
```
To destroy the current process, the `destroy()`method can be used:
```java
// execute laplace.lua with working directory "exampleDir"
Shell.execute(exampleDir, new File(exampleDir,"laplace.lua")).destroy();
```

## How to Build VUGShell

### Requirements

- Java >= 1.8
- Internet connection (dependencies are downloaded automatically)
- IDE: [Gradle](http://www.gradle.org/) Plugin (not necessary for command line usage)

### IDE

Open the `VUGShell` [Gradle](http://www.gradle.org/) project in your favourite IDE (tested with NetBeans 8.2) and build it
by calling the `assemble` task.

### Command Line

Navigate to the [Gradle](http://www.gradle.org/) project (e.g., `path/to/VUGShell`) and enter the following command

#### Bash (Linux/OS X/Cygwin/other Unix-like shell)

    sh gradlew assemble
    
#### Windows (CMD)

    gradlew assemble
