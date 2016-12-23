# VUGShell
API for accessing precompiled UGShell distribution for VRL plugins and Java projects

[ ![Download](https://api.bintray.com/packages/miho/UG/VUGShell/images/download.svg) ](https://bintray.com/miho/UG/VUGShell/_latestVersion)

## Usage:

To execute a specified ug *.lua script, the following method can be used:
```java
// execute laplace.lua with working directory "exampleDir"
Shell.execute(exampleDir, new File(exampleDir,"laplace.lua"));
```
To print the output to given print streams, use the `print(...)` method:
```java
// execute laplace.lua with working directory "exampleDir"
Shell.execute(exampleDir, new File(exampleDir,"laplace.lua")).print(System.out,System.err)
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
