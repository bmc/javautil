*org.clapper.util* Java Utility Library
=======================================

[![Build Status](https://travis-ci.org/bmc/javautil.svg?branch=master)](https://travis-ci.org/bmc/javautil)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.clapper/javautil/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.clapper/javautil)

The *org.clapper.util* Java Utility Library is a library of miscellaneous
utility classes and methods. As a general-purpose set of tools, this API is
a conceptual extension of the JDK.

I don't use this library much any more. I do most of my JVM-based programming
in [Scala](http://www.scala-lang.org), rather than Java, and my
[Grizzled Scala](http://software.clapper.org/grizzled-scala) is the
Scala-equivalent of this library.

_However_..., I _do_ ensure that this library is kept up-to-date, and if there
are bugs in it, I fix them. If you run into a problem using this library,
open an [issue](https://github.com/bmc/javautil/issues), and I will try
to address it.

The library is Copyright &copy; 2004-2017, Brian M. Clapper, is released
under a [New BSD License](LICENSE.md).

Please see the [home page][] for more information, downloads, and the like.

## Building from Source

This software builds with [SBT][http://scala-sbt.org/] (mostly because I
can't stand Maven, and I'm tired of fighting with Gradle).

If you're on a *nix box (including MacOS), simply run:

```
bin/activator compile
```

If you're on Windows, you'll have to download SBT and run

```
sbt build
```

[home page]: http://software.clapper.org/javautil/

