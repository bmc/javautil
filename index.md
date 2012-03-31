---
title: org.clapper.util Java Utility Library
layout: withTOC
---

# Introduction

The *org.clapper.util* Java Utility Library is a library of miscellaneous
utility classes and methods. As a general-purpose set of tools, this API is
a conceptual extension of the JDK. I use this library myself, so I
frequently update and revise its contents.

# Installation

The Java Utility Library is published to my personal [Maven][] repository
at *maven.clapper.org*.

## Using from Maven

If you're building your project with [Maven][], add the following
repository to your `pom.xml`:

    <repositories>
      <repository>
        <releases>
          <enabled>true</enabled>
          <updatePolicy>always</updatePolicy>
          <checksumPolicy>warn</checksumPolicy>
        </releases>
        <id>clapper-org-maven-repo</id>
        <name>org.clapper Maven Repo</name>
        <url>http://maven.clapper.org/</url>
        <layout>default</layout>
      </repository>
      ...
    </repositories>

Then, add the following dependency to your `<dependencies>` section:

    <dependency>
      <groupId>org.clapper</groupId>
      <artifactId>javautil</artifactId>
      <version>3.1.0</version>
    </dependency>
    
## Building with Apache Buildr

If you're using Apache [Buildr][], the following lines should do the trick:

    repositories.remote << 'http://maven.clapper.org/'
    compile.with 'org.clapper:javautil:jar:3.1.0'

## Installing Manually

If you're not using [Maven][], or [Buildr][], or [SBT][] or somthing else
reasonably sane (e.g., if you're still using [Ant][]), you'll have to
download the jar and its dependent jars, and install them manually.

The compiled jar is located at
<http://maven.clapper.org/org/clapper/javautil/3.1.0/javautil-3.1.0.jar>.

This software assumes a 1.5 JDK or better, and it depends on the following
third-party libraries:

* The [JavaMail][] jar.
* The [JavaBeans Activation Framework][jaf] (JAF), if you're using a 1.5 JDK.
  (JAF is bundled with Java 1.6.)
* The jars for version 3.3.1 of the [ASM][] bytecode manipulation library.

*However...* you're much better off using a [Maven][]-aware build tool,
such as [Maven][], [SBT][] or [Buildr][], if you can.

# Documentation

* You can find javadocs for the *org.clapper.util* library [here][javadocs].
* Also see the [CHANGELOG][] for a list of changes associated with each 
  release.

# Building from Source

## Prerequisites

### Git

The source for the Java Utility Library is in a [GitHub repository][]. The
easiest way to obtain it is via [Git][], which runs on Unix-like operating
systems (such as Linux and FreeBSD), Windows, and Mac OS X.

### Buildr

The library builds with Apache [Buildr][], because Buildr's Ruby-based
build files are more powerful and flexible, and much easier to read and
maintain, than [Maven][]'s POM files. If you're building this library from
source, you must first [download and install Buildr][].

## Building

First, clone a copy of the Git repository:

    git clone git://github.com/bmc/javautil.git

Then, change your working directory to the newly-created `javautil` directory,
and type:

    buildr compile

to compile the code. To install it in your local Maven repository, type

    buildr install

# Copyright and License

This library is copyright &copy; 2004-2011 Brian M. Clapper and is released
under a [BSD License][].

# Patches

I gladly accept patches from their original authors. Feel free to email
patches to me or to fork the [GitHub repository][] and send me a pull
request. Along with any patch you send:

* Please state that the patch is your original work.
* Please indicate that you license the work to the *org.clapper.util* project
  under a [BSD License][].

[Ant]: http://ant.apache.org/
[BSD License]: license.html
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
[CHANGELOG]: CHANGELOG.html
[Buildr]: http://buildr.apache.org/
[SBT]: https://github.com/harrah/xsbt/
[download and install Buildr]: http://buildr.apache.org/installing.html
