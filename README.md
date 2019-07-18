# SonarQube Scanner for MSBuild Gradle Plugin

## Summary

This plugin allows you to launch a SonarQube scan on a .Net solution. It will download the scanner release in a cache folder, restore the solution packages, launch the scan building the solution with msbuild command line and upload the report to the wanted server.

## Requirements

As this plugin has been written in Kotlin, it requires Gradle 4.9+ to work.

To execute the scan, you will also need to install msbuild on your computer.

## Tasks

This plugins creates several tasks to execute his scan :

* __nugetRestore__ : Restore NuGet packages for the solution. If a task with the same name already exists, it will use it instead of register its own one.
* __sonarScan__ : Execute the SonarQube scan building the solution with the defined configuration and platform.

## Quick Start

TODO

## Complete DSL

```groovy
sonarqube {
    // Project configuration
    projectKey = ""                 // Required, key for project on SonarQube
    projectName = ""                // Optional, default: null

    // Server configuration
    url = "http://localhost:9000"   // Optional, default: use SonarScanner default server URL
    login = ""                      // Optional, default: null
    password = ""                   // Optional, default: null
    authentificationToken = ""      // Optional, default: null

    nunitReport = ""                // Optional, default: null
}
```

## Note

If the SonarQube server you use needs authentication, the plugin provides you the two methods defined in the scanner :

* `login` / `password`
* `authenticationToken`

If the three properties are defined in your `build.gradle`, the authentication token will be used instead of the login and password.
