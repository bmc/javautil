---
title: org.clapper.util Java Utility Library
layout: withTOC
---

[![Build Status](https://travis-ci.org/bmc/javautil.svg?branch=master)](https://travis-ci.org/bmc/javautil)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.clapper/javautil/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.clapper/javautil)

# Introduction

The *org.clapper.util* Java Utility Library is a library of miscellaneous
utility classes and methods. As a general-purpose set of tools, this API is
a conceptual extension of the JDK.

**Note**: I don't use this library much any more. I do most of my JVM-based programming
in [Scala](http://www.scala-lang.org), rather than Java, and my
[Grizzled Scala](http://software.clapper.org/grizzled-scala) is the
Scala-equivalent of this library.

_However_, I _do_ ensure that this library is kept up-to-date, and if there
are bugs in it, I try to fix them. If you run into a problem using this library,
open an [issue](https://github.com/bmc/javautil/issues), and I will try
to address it.

# Installation

The Java Utility Library is published to the
[Bintray Maven repository](https://bintray.com/bmc/maven), which is
automatically linked to Bintray's [JCenter](https://bintray.com/bintray/jcenter)
repository. (From JCenter, it's eventually pushed to the
[Maven Central Repository](http://search.maven.org/).)

## Using from Maven

If you're building your project with [Maven][], just specify the artifact
as a dependency:

```
<dependency>
  <groupId>org.clapper</groupId>
  <artifactId>javautil</artifactId>
  <version>3.2.0</version>
</dependency>
```

If you cannot resolve the artifact, then add the JCenter repository:

```
<repositories>
  <repository>
    <snapshots>
      <enabled>false</enabled>
    </snapshots>
    <id>central</id>
    <name>bintray</name>
    <url>http://jcenter.bintray.com</url>
  </repository>
  ...
</repositories>
```

## Building with Gradle

If you're using [Gradle][], the following lines should do the
trick:

```
buildscript {
  repositories {
    jcenter()
    mavenLocal()
  }
}

dependencies {
  compile group: 'org.clapper', name: 'javautil', version: '3.2.0'
  ...
}
```

## Installing Manually

If you're not using [Maven][] or [Gradle][] or [SBT][] or something else
reasonably sane (e.g., if you're still using Ant), you'll have to
download the jar and its dependent jars, and install them manually.

The compiled jar is located at
<https://bintray.com/bmc/maven/download_file?file_path=org%2Fclapper%2Fjavautil%2F3.2.0%2Fjavautil-3.2.0.jar>

This software assumes a 1.7 JDK or better, and it depends on the following
third-party libraries:

* The [JavaMail][] jar.
* The jars for version 3.3.1 of the [ASM][] bytecode manipulation library.

*However...* you're much better off using a [Maven][]-aware build tool,
such as [Maven][], [SBT][] or [Gradle][], if you can.

# Documentation

* You can find javadocs for the *org.clapper.util* library [here][javadocs].
* Also see the [CHANGELOG][] for a list of changes associated with each
  release.

# Building from Source

This software builds with [SBT](http://scala-sbt.org/), because I really
dislike Maven, and I'm tired of fighting with Gradle.

If you're building this library from source on a Unix-like system (including
MacOS), you do _not_ need to install SBT, however. The `bin/activator`
script bundled with the repository will handle that for you. (If you
insist on using Windows, you _will_ have to download SBT first.)

## Building

First, clone a copy of the Git repository:

```
git clone git://github.com/bmc/javautil.git
```

Then, change your working directory to the newly-created `javautil` directory,
and type:

```
bin/activator package
```

to compile the code. The resulting jar file will be in `./target`.

If you're using Windows, `bin/activator` may not work, as it is a Bash
script. In that case, download and install [SBT](http://www.scala-sbt.org).
Then, run:

```
sbt package
```

To install it in your local Maven repository, type

```
bin/activator publish-local
```

(or `sbt publish-local` on Windows).

# Copyright and License

This library is copyright &copy; 2004-2017 Brian M. Clapper and is released
under a [New BSD License][].

# Patches

I gladly accept patches from their original authors. Feel free to email
patches to me or to fork the [GitHub repository][] and send me a pull
request. Along with any patch you send:

* Please state that the patch is your original work.
* Please indicate that you license the work to the *org.clapper.util* project
  under a [New BSD License][].

[New BSD License]: http://opensource.org/licenses/BSD-3-Clause
[GitHub repository]: http://github.com/bmc/javautil
[GitHub]: http://github.com/bmc/
[Git]: http://git-scm.com/
[downloads area]: http://github.com/bmc/javautil/downloads
[bmc@clapper.org]: mailto:bmc@clapper.org
[Maven]: http://maven.apache.org/
[IzPack]: http://www.izforge.com/izpack/
[JavaMail]: http://www.oracle.com/technetwork/java/index-jsp-139225.html
[jaf]: http://java.sun.com/products/archive/javabeans/jaf102.html
[ASM]: http://asm.ow2.org/
[javadocs]: api/index.html
[CHANGELOG]: https://github.com/bmc/javautil/blob/master/CHANGELOG.md
[Gradle]: (http://gradle.org)
[Buildr]: http://buildr.apache.org/
[SBT]: https://github.com/harrah/xsbt/
[download and install Buildr]: http://buildr.apache.org/installing.html
