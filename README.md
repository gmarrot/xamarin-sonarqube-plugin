# SonarQube Scanner for MSBuild Gradle Plugin

## Summary

This plugin allows you to launch a SonarQube scan on a .Net solution. It will download the scanner release in a cache folder, restore the solution packages, launch the scan building the solution with msbuild command line and upload the report to the wanted server.

## Requirements

As this plugin has been written in Kotlin, it requires Gradle 4.9+ to work.

To execute the scan, you will also need to install msbuild on your computer.

## Quick Start

First, you need to apply the plugin in your `build.gradle`.
As it uses a jcenter library, you will also need to update your buildscript.

```groovy
plugins {
    id "com.betomorrow.xamarin.sonarqube" version "1.0.0"
}
```

Or you can use the legacy plugin declaration.
As it uses third-party libraries from jcenter, you will also need to update your buildscript.

```groovy
buildscript {
    repositories {
        jcenter()
        maven {
            url "https://plugins.gradle.org/m2/"
        }
    }
    dependencies {
        classpath "com.betomorrow.gradle:xamarin-sonarqube-plugin:1.0.0"
    }
}

apply plugin: "com.betomorrow.xamarin.sonarqube"
```

We will also have to declare your SonarQube project key :

```groovy
sonarqube {
    projectKey = "my-project-jey"
}
```

## Tasks

This plugins creates several tasks to execute his scan :

* __nugetRestore__ : Restore NuGet packages for the solution. If a task with the same name already exists, it will use it instead of register its own one.
* __sonarScan__ : Execute the SonarQube scan building the solution with the defined configuration and platform.

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
