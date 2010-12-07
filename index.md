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

There are two ways to install the *org.clapper.util* library:

* Via an [IzPack][] installer jar, available on the [Downloads][] page
* Manually

Each method is described below.

Unfortunately, this library is not yet available via any [Maven][]
repository.

## Installing with the Installer Jar

[IzPack][] installers support both a graphical installation mode (the
default) and a command-line installation mode (by specifying a `-console`
parameter to the invocation).

This installation method is preferred, since it's simple, it automatically
bundles and installs all dependent third-party libraries, and it provides a
convenient way to upgrade or uninstall the package.

To install via the graphical installer:

* Download the installer jar from the [Downloads][] page.
* Run the installer jar: `java -jar install-ocutil-xxxx.jar`
* Follow the instructions in the graphical installation screens.

Once you've installed the library via the installer, be sure to add the
path to the library's jar file to your `CLASSPATH`.

## Installing Manually

Instead of using the installer, you can install the binary jar file
manually. Doing so means you're also responsible for ensuring that the
dependent third-party jar files are available on your system.

To install the binary jar file (see the [Downloads][] page), simply place
it somewhere in your CLASSPATH. This software assumes a 1.5 JDK or better,
and it depends on the following third-party libraries:

* The [JavaMail][] jar.
* The [JavaBeans Activation Framework][jaf] (JAF), if you're using a 1.5 JDK.
  (JAF is bundled with Java 1.6.)
* The jars for version 2 of the [ASM][] bytecode manipulation library.

*However...* you're much better off just using the graphical installer.

# Documentation

* You can find javadocs for the *org.clapper.util* library [here][javadocs].
* Also see the [CHANGELOG][] for a list of changes associated with each 
  release.

# Building from Source

The library does not currently build with [Maven][], so building it from
source is a bit of a pain.

## Third-party Software

Before building *org.clapper.util*, you'll need

* [Jakarta Ant][Ant], version 1.6.5 or better.
* The [JavaMail][] jar.
* The [JavaBeans Activation Framework][jaf] (JAF), if you're using a 1.5 JDK.
  (JAF is bundled with Java 1.6.)
* The jars for version 2 of the [ASM][] bytecode manipulation library.
* The `izpack-compiler.jar` file from the [IzPack][] distribution. This is
  only necessary if you're going to build the installer.

[FreeBSD][] users will find ports for many of the third-party libraries.
Linux users may find packages (RPMs, DEBs, etc.) for those libraries.

## Prepare the Build Environment

1. Once you've downloaded the various third-party jar files, place them in
   a directory somewhere.
2. Download the source from the [downloads area][] and unzip it, or
   get the code from the [GitHub repository][].
3. Change your working directory to the top-level `javautil` source directory.
4. In the topmost source directory (i.e., the directory containing the
   `build.xml` file), create a file called `build.properties` containing the
   following line:
   
>    third.party.jar.dir: /path/to/directory/containing/jars

## Building

* Type `ant build` to compile the code and create the jar file. The jar file
  ends up in the `build/lib` subdirectory.
* To create the Javadocs, type `ant javadocs`. (This step is optional.)
* To create version-stamped release files, type `ant release`. The
  resulting files end up in the `build/release` directory.
* To create the installer, type `ant release installer`. The installer jar
  file will end up in the `build/release` directory.

# Copyright and License

This library is copyright &copy; 2004-2010 Brian M. Clapper and is released
under a [BSD License][].

# Patches

I gladly accept patches from their original authors. Feel free to email
patches to me or to fork the [GitHub repository][] and send me a pull
request. Along with any patch you send:

* Please state that the patch is your original work.
* Please indicate that you license the work to the *org.clapper.util* project
  under a [BSD License][].

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
