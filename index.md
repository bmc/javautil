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

The easiest way to install the jar is via [Maven][]. The Java Utility Library
is published to my personal Maven repository at *maven.clapper.org*. To access
it, add the following repository to your `pom.xml`:

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
      <version>3.0</version>
    </dependency>

## Installing Manually

If you're not using Maven (e.g., if you're still using [Ant][]), you'll
have to download the jar and its dependent jars, and install them manually.

The compiled jar is located at
<http://maven.clapper.org/org/clapper/javautil/3.0/javautil-3.0.jar>.

This software assumes a 1.5 JDK or better, and it depends on the following
third-party libraries:

* The [JavaMail][] jar.
* The [JavaBeans Activation Framework][jaf] (JAF), if you're using a 1.5 JDK.
  (JAF is bundled with Java 1.6.)
* The jars for version 3.3.1 of the [ASM][] bytecode manipulation library.

*However...* you're much better off using Maven, if you can.

# Documentation

* You can find javadocs for the *org.clapper.util* library [here][javadocs].
* Also see the [CHANGELOG][] for a list of changes associated with each 
  release.

# Building from Source

The library builds with [Maven][], so building from source is generally
straightforward.

First, clone a copy of the Git repository:

    git clone git://github.com/bmc/javautil.git

Then, change your working directory to the newly-created `javautil` directory,
and type:

    mvn package

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
[downloads area]: http://github.com/bmc/javautil/downloads
[bmc@clapper.org]: mailto:bmc@clapper.org
[Maven]: http://maven.apache.org/
[IzPack]: http://www.izforge.com/izpack/
[JavaMail]: http://www.oracle.com/technetwork/java/index-jsp-139225.html
[jaf]: http://java.sun.com/products/archive/javabeans/jaf102.html
[ASM]: http://asm.ow2.org/
[javadocs]: api/index.html
[CHANGELOG]: CHANGELOG.txt
