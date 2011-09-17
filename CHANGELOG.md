---
title: Change log for org.clapper.util Java Utility Library
layout: default
---

Version 3.0.1 (17 September, 2011)

* Switched to Apache [Buildr][], because Buildr's Ruby-based build files
  are more powerful and flexible, and much easier to read and maintain,
  than [Maven][]'s POM files.

[Buildr]: http://buildr.apache.org/
[Maven]: http://maven.apache.org/

----

Version 3.0 (16 September, 2011)

* Now builds with Maven.
* Now published to org.clapper Maven repository. See the
  [web page](http://software.clapper.org/javautil/) for details.
* `org.clapper.util.ant` has been removed.
* `org.clapper.util.scripting` has been removed.

----


Version 2.5.2 (23 April, 2010)

* Fixed problem with parsing of boolean flags in configuration files,
  introduced in previous release.
* Changed license to a true, new-style BSD license.

----


Version 2.5.1 (25 October, 2009)

* `org.clapper.util.config.Configuration` now permits specification of encoding
  on the file or URL being loaded.

----


Version 2.5 (25 October, 2009)

* Updated to ASM version 3.2. Thanks to Charles Johnson 
  (cj *at* technojeeves.com) for suggesting this upgrade and for providing
  required changes to the `org.clapper.util.classutil` classes.
* The `build.xml` file now honors the value of the `ASM_HOME` environment
  variable, to find the location of the ASM bytecode library. If the bytecode
  library isn't present, the `org.clapper.util.classutil` package isn't built.
* `org.clapper.util.classutil.ClassInfo` now contains information about a
  class's methods and fields.

----


Version 2.4.3 (25 July, 2009)

* `org.clapper.util.mail.SMTPEmailTransport` now allows you to set the
  name of the localhost.

* Fixed an open file leak in `org.clapper.util.classutil.ClassFinder`. When
  called with a classpath consisting of a lot of directories (not jar files),
  it was possible to end up with too many open files, because the files
  weren't explicitly closed. While they'd eventually be collected and closed
  by the garbage collector, the files could be opened in rapid succession,
  leading to many open files before the garbage collector ever ran.

  Thanks to Robert von Burg (eitch *at* eitchnet.ch) for reporting and
  diagnosing the bug.

----


Version 2.4.2 (12 January, 2008)

* Updated `org.clapper.util.ant.JUnitSummaryFormatter` to work with
  Ant 1.7.

* Fixed a bug in the `org.clapper.util.io.RollingFileWriter` class: If the
  file pattern is incorrect, the class throws an exception. However, the
  exception message used a `java.text.MessageFormat` format that caused
  `MessageFormat` to gag. Thanks to Daniel Carr 
  (daniel.carr *at* clipsal.com.au) for finding and reporting the
  bug.


Version 2.4.1 (13 August, 2007)

* Added the `org.clapper.util.misc.SparseArrayList` class.
* Added new `HTMLUtil.escapeHTML()` method.
* Added new `org.clapper.util.text.Duration` class, which parses and formats
  strings like "1 day, 5 minutes" and "300 days, 23 hours, 59 minutes".
* Factored parameter parsing out of the
  `org.clapper.util.cmdline.CommandLineUtility` class and into a separate
  `org.clapper.util.cmdline.ParameterParser` class, allowing it to be used in
  other contexts (e.g., within a property value that happens to have a
  command line option syntax).
* In the `org.clapper.util.config.Configuration` class, the
  `getConfigurationTokens()` method has been simplified and made more
  intuitive. Now, it returns the value, split on white space, with
  quotes honored. (Previously, it only split the value into tokens based
  on quotes.)
* In the `org.clapper.util.misc.NestedException` method (the base class for
  all exceptions in the library), the `getMessages()` method is used to
  get one consolidated string that concatenates the messages of all the
  nested exceptions. It now suppresses any duplicate messages.
* Added a new `org.clapper.util.text.MultipleMapVariableDereferencer` class,
  to permit `VariableSubstituter` objects to use multiple maps to resolve
  variable values.
* `org.clapper.util.misc.BuildInfo` now contains some useful print methods.
* Fixed a bundle string retrieval bug in `org.clapper.util.misc.BundleUtil`.


----


Version 2.4 (17 April, 2007)

* The new `org.clapper.util.text.TextUtil` method `isPrintable()` determines
  whether a character is printable.
* The new `org.clapper.util.text.TextUtil` method `charToUnicodeEscape()`
  converts a character to a string representing its Java unicode escape
  sequence.
* Converted `org.clapper.util.text.XStringBufBase` (the base class for
  `XStringBuffer` and `XStringBuilder`) to use `TextUtil.isPrintable()` and
  `TextUtil.charToUnicodeEscape()`, instead of doing those things inline.
* Added more JUnit tests for `org.clapper.util.html.HTMLUtil` and
  `org.clapper.util.text.TextUtil`.
* The `convertCharacterEntities()` method in `org.clapper.util.html.HTMLUtil`
  handled decimal character entities (e.g., "&amp;#8482;") but not hexadecimal
  ones (e.g., "&amp;#x2122;"). It does now. Thanks to Arjumand Bonhomme 
  (jumand *at* gmail.com) for finding the problem and suggesting a
  fix. (I've adapted his patch.)
* The `makeCharacterEntities()` method in `org.clapper.util.html.HTMLUtil`
  didn't properly handle some symbolic entities, owing to some mistakes
  in the properties file it uses for conversion. That's been fixed.

----

Version 2.3 (29 November, 2006)

* The graphical installer now renders the license in a more readable,
  proportional font.
* Propagated modified license (from 2.2) into source files.
* Added JUnit infrastructure and various JUnit tests to build.
* In `org.clapper.util.misc.LRUMap`, the `containsValue()` method was broken; a
  new JUnit test found the bug. Also implemented a proper `values()` method
  for `LRUMap`.
* In `org.clapper.util.misc.MultiIterator`, the `remove()` method was broken;
  it would throw an `IllegalStateException` when removing the last item in a
  contained iterator. A new JUnit test found the bug.
* `Configuration` class (in `org.clapper.util.config`) constructors that
  actually parse the configuration have been deprecated. They are unsafe,
  since they invoke methods that could be overridden by a subclass (and
  thus could be called on an incompletely constructed object). Use the
  `load()` methods, instead.
* `TextUtil.stringIsEmpty()` is now more efficient and is a good (and faster)
  alternative to `String.trim().isEmpty()` for determining whether a string
  has any non-blank characters. (`String.trim()` creates a new `String`
  instance, just to test its length; `TextUtil.stringIsEmpty()` does not.)
* In `org.clapper.util.classutil`, `AndClassFilter` and `OrClassFilter` are
  now final. This change avoids problems with the (useful) constructors for
  those classes potentially calling methods that could be overridden.
* In `org.clapper.util.io`, `AndFileFilter`, `AndFilenameFilter`,
  `OrFileFilter` and `OrFilenameFilter` are now final. This change avoids
  problems with the (useful) constructors for those classes potentially
  calling methods that could be overridden.

* Changes and fixes to the `org.clapper.util.config.Configuration` class:
  - It how properly handles variable references to the "system", "program"
    and "env" pseudo-sections when an include file was being processed.
  - It will now abort, by default, if it encounters a reference to a
    non-existent section or variable in a `${...}` variable substitution.
    Prior to this change, it would simply substitute an empty string;
    substituting an empty string can lead to runtime errors that are
    difficult to diagnose. The old behavior can be restored by calling
    Configuration.setAbortOnUndefinedVariable(true).
  - It now supports `${var?default value}` syntax. In that example, if
    `${var}` does not have a value, or has an empty value, the string
    "default value" will be substituted.

* In `org.clapper.util.misc.FileHashMap`:
  - Class was using an exclusive file lock to prevent concurrent
    modification, but this strategy turns out to cause more problems than
    it solves (owing to the way Java file locking is implemented); it has
    been removed.
  - Added `delete()` method, to force deletion of files.

* Miscellaneous code cleanup based on output from PMD (pmd.sourceforge.net).

* The `org.clapper.util.misc.ArrayIterator` class's `next()` method would
  throw a `NullPointerException` if the wrapped array was `null`. It now
  throws a `NoSuchElementException`.

* The `org.clapper.util.text.UnixShellVariableSubstituter` class has been
  extended:
  - It now supports a `${var?default value}` syntax. In that example,
    if `${var}` does not have a value, or has an empty value, the string
    "default value" will be substituted.
  - It can be configured to abort when a variable is undefined, rather
    than substituting an empty string.
  - It can be configured to abort on syntax error (e.g., an unclosed
    `}`), rather than simply passing the bad variable reference through.
  - The version of `substitute()` that takes an `allowEscapes` parameter
    has been deprecated. The same capability is now available by setting
    the "honor escapes" flag via the new `setHonorEscapes()` method.

* The `org.clapper.util.text.WindowsShellVariableSubstituter` class has been
  extended:
  - It can be configured to abort when a variable is undefined, rather
    than substituting an empty string.
  - It can be configured to abort on syntax error (e.g., an unclosed
    "%"), rather than simply passing the bad variable reference through.

* Added a new `org.clapper.util.scripting package`, containing a simplified
  front-end to both the Apache Jakarta Bean Scripting Framework and the
  Java 6 javax.script (a.k.a., JSR 223) scripting framework. This package
  allows code to use one API to interact with either (or both) framework.

* Added a new generics-aware `org.clapper.util.misc.MultiValueMap` class.
  This class is very similar to the `MultiValueMap` in the Jakarta Commons
  Collections API, except that it uses Java generics.

* `org.clapper.util.classutil.ClassUtil` now has an `instantiateClass()`
  convenience method.

* The `HTMLUtil` class has been moved from the `org.clapper.util.text` package
  to new `org.clapper.util.html` package. A deprecated version of `HTMLUtil`
  remains in the `org.clapper.util.text package` (for now), but its methods
  merely invoke the corresponding methods in `org.clapper.util.html.HTMLUtil`.

* In `org.clapper.util.text.TextUtil`:

  - Added various hexadecimal conversion routines.
  - Added `romanNumeralsForNumber()` method.

----

Version 2.2 (4 July, 2006)

* `org.clapper.util.logging` API is now implemented in terms of Jakarta
  Commons Logging, rather than `java.util.logging`.
* Added new `org.clapper.util.io.FileOnlyFilter`, a complement to the
  `org.clapper.util.io.DirectoryFilter` class. Like `DirectoryFilter`,
  `FileOnlyFilter` implements `java.io.FileFilter` and can be used to filter
  files.
* Deprecated `MultipleRegexFilenameFilter`, `CombinationFilenameFilter` and
  `CombinationFileFilter` classes in `org.clapper.util.io` package in favor of
  new, simpler filters that can be combined more easily.
* Created new `org.clapper.io.classutil` package with some class-related
  classes.
* Added new `org.clapper.util.misc.XDate` class, a subclass of
  `java.util.Date`. `XDate` contains some utility methods to aid in time zone
  conversion.
* `org.clapper.util.misc.BuildInfo` now produces build info properties files
  with a build ID (consisting of the date and time, with milliseconds, in
  UTC).
* The `CommandLineUtility` and `UsageInfo` classes (in
  `org.clapper.util.cmdline`) now permit an option to be specified with a
  null explanation, denoting a "hidden" option that isn't shown in the
  usage display. This is useful, for instance, when you've deprecated an
  option but are retaining it for backward compatibility.
* The `UsageInfo` class now permits the caller to set the command name, which
  is then displayed in the usage message instead of "java full-class-name".
  If not set, then "java full-class-name" is used, as before.
* Added new `org.clapper.util.io.Zipper` class, a front-end to the
  `java.util.zip` library that makes creating zip files easier.

----

Version 2.1.3 (5 February, 2006)

* Fixed a bug in the `org.clapper.util.io.WordWrapWriter` class: When
  writing a new line (i.e., when the last thing written was a newline
  character), if the first token to be written exceeded the line length,
  the `WordWrapWriter.flushBuffer()` method would put out a spurious newline
  before writing the token. That is, it did not properly recognize that the
  line was *already* wrapped, since the *last* thing it had written was a
  newline.
* The `org.clapper.util.config.Configuration` class now honors double quoted
  fields in a configuration file. Double quotes can be used to escape the
  special meaning of white space, while still permitting metacharacters and
  variable references to be expanded. (Metacharacter and variable
  references are not expanded between single quotes.) When retrieving a
  variable's value via `Configuration.getConfigurationValue()`, a program
  will not be able to tell whether double quotes were used or not, since
  `getConfigurationValue()` returns the "cooked" value as a single string.
  There's a new `Configuration.getConfigurationTokens()` method that programs
  can call to retrieve the parsed tokens that comprise a configuration value.
  Double- and single-quoted strings are returned as individual tokens.
  See the javadocs for the Configuration class for additional details.
* Added two new versions of the `TextUtil.join()` method that allow the
  caller to specify a starting array index and length.
* `RollingFileWriter` was mistakenly treating embedded backslashes in its
  file patterns as Unix-style escapes. This led to problems on Windows
  systems. Specifically, embedded file separators (backslash characters)
  were being swallowed when they should not have been.

----

Version 2.1.2 (2 January, 2006)

* The library can now be installed (along with all dependent jars,
  documentation, sources, and a wrapper shell or BAT script) via a
  graphical installer. The graphical installer is based on the IzPack
  installer framework (http://www.izforge.com/izpack/).
* The Ant `build.xml` file wasn't properly constructing the source zip file.
  In particular, it was omitting properties files.
* Fixed bug in `TextUtil.join()` method taking a `java.util.Collection`: No
  longer craps out with a `NullPointerException` if it encounters a null
  entry in the Collection.

----

Version 2.1.1 (13 December, 2005)

* Added `makeCharacterEntities()` method to `org.clapper.util.text.HTMLUtil`
  class.

* Fixed a `org.clapper.util.io.RollingFileWriter` constructor that was
  ignoring the specified character set name (i.e., the encoding) and using
  the default encoding of the VM.

----

Version 2.1 (25 November, 2005)

* Added new `RollingFileWriter` class. Works like a `FileWriter`, but
  rolls files when they get too big.
* Some minor changes to some exception error messages.
* Added `org.clapper.util.io.FileUtil.getDefaultEncoding()` method.
* Fixed formatting bug in usage output in `CommandLineUtility` class.
* Fixed `getFileNameNoExtension()` and `getFileNameExtension()` methods
  in `org.clapper.util.io.FileUtil` to look for the last "." in the
  file name, not the first.


----

Version 2.0.3 (19 August, 2005)

* Some additional Java 5.0 (a.k.a., JDK 1.5) changes, specifically:
  - `org.clapper.util.logging.LogLevel` is now an enum
  - `org.clapper.util.io.CombinationFilterMode` is now an enum
  - `org.clapper.util.misc.Semaphore` and
    `org.clapper.util.misc.ObjectLockSemaphore` are now deprecated, in
    favor of the `java.util.concurrent.Semaphore` class provided with
    J2SE 5.0 (a.k.a., Java 1.5)

----

Version 2.0.2 (10 August, 2005)

* Add versions of `TextUtil.join()` that take variable arguments.


----

Version 2.0.1 (29 July, 2005)

* Some minor changes to the `org.clapper.util.mail.EmailMessage` class:
  - The `clear()` method now explicitly deletes any temporary files that
    were created to handle `java.io.InputStream` attachments. As before,
    the the finalizer also performs that explicit deletion, and the files
    are marked for deletion when the VM exits. But the application can now
    force that cleanup itself.
  - Added some new versions of the `setText()` and `addAttachment()` methods,
    permitting use of a `java.io.File` object while still setting an
    explicit attachment name that isn't necessarily derived from the
    actual file name.


----

Version 2.0 (22 April, 2005)

* Now requires Java 1.5.0 JDK/JRE.
* Converted various classes to use JDK 1.5 generics.
* The `org.clapper.util.text.XStringBuffer` class now implements
  `java.lang.Appendable` and `java.lang.CharSequence`.
* New class `org.clapper.util.text.XStringBuilder` provides similar
  functionality to `XStringBuffer`, except that it wraps a `StringBuilder`.
  Functionality common to `XStringBuffer` and `XStringBuilder` is in a new
  `XStringBufBase` class.
* `org.clapper.util.config.Configuration` class now supports an "env"
  pseudosection, to interpolate the values of environment variables into
  a configuration. (The 1.5 JDK has re-established support for environment
  variables.) See the javadocs for the `Configuration` class for details.
* `org.clapper.util.io.JustifyTextWriter` now uses (new) enum
  `org.clapper.util.io.JustifyStyle` to specify the justification style
  (`CENTER`, `LEFT_JUSTIFY`, `RIGHT_JUSTIFY`), instead of an "int".
  Unfortunately, this change is not backward-compatible with previous
  versions of this library.

* `org.clapper.util.MultipleRegexFilenameFilter` now uses enums for the
  match type values:
  - `MultipleRegexFilenameFilter.MATCH_FILENAME` becomes
    `MultipleRegexFilenameFilter.MatchType.FILENAME`
  - `MultipleRegexFilenameFilter.MATCH_PATH` becomes
    `MultipleRegexFilenameFilter.MatchType.PATH`

* `org.clapper.util.text.MapVariableDereferencer` now expects a
  `java.util.Map<String,String>` object, not just a `Map` object.
* To make it easier to use a `java.util.Properties` object with the
  `org.clapper.util.text.VariableSubstituter` class, there's now a type-safe
  `org.clapper.util.misc.PropertiesMap` wrapper for `java.util.Properties`.
* Add new (simple) `org.clapper.util.io.XMLWriter` class, used to write
  XML output.


----

Version 1.1.9 (09 August, 2005) **MAINTENANCE RELEASE OF DEPRECATED CODE
BRANCH**

* Some minor changes to the `org.clapper.util.mail.EmailMessage` class:
  - The `clear()` method now explicitly deletes any temporary files that
    were created to handle java.io.InputStream attachments. As before, the
    the finalizer also performs that explicit deletion, and the files are
    marked for deletion when the VM exits. But the application can now
    force that cleanup itself.
  - Added some new versions of the `setText()` and `addAttachment()` methods,
    permitting use of a `java.io.File` object while still setting an
    explicit attachment name that isn't necessarily derived from the
    actual file name.


----

Version 1.1.8 (21 April, 2005)

* Fixed bug in `org.clapper.util.cmdline.CommandLineUtility` class: Default
  version `processPostOptionCommandLine()` threw an unconditional
  exception. It should've thrown an exception only if the iterator still
  had something in it. This bug led to an erroneous usage message with
  utilities that expected no parameters and thus did not override the
  `processPostOptionCommandLine()` method. (Branch version fix.)


----

Version 1.1.7 (08 April, 2005)

* Miscellaneous Javadoc clarifications


----

Version 1.1.6 (09 February, 2005)

* Added `TextUtil.split()` methods that permit preserving empty strings. By
  default, the `split()` methods parse through adjacent empty tokens. e.g.,
  Given the string "a:b::c" and delimiter ":", the `split()` methods used to
  return the array \["a", "b", "c"\], since the adjacent "::" delimiters were
  treated as one delimiter. Now, that behavior can be controlled by passing
  in a boolean, so that `split()` can return the array \["a", "b", "", "c"\].

* Modified RegexUtil.substitute() to properly handle substitutions of the
  empty string. Strings like this were previously rejected as syntactically
  incorrect: `s/foo//`  They are now permitted and processed properly.

* `build.xml` no longer hard-codes `jikes` compiler.

* `build.xml` corrected so it no longer unconditionally recompiles everything.

* Serializable classes now provide their own `serialVersionUID` variable,
  per recommendations in the JDK 1.5 `java.io.Serializable` docs and in
  `jikes` 1.22 warnings.


----

Version 1.1.5 (02 December, 2004)

* Added `getOptionalCardinalValue()` and `getRequiredCardinalValue()` utility
  methods to `org.clapper.util.config.Configuration` class.


----

Version 1.1.4 (10 November, 2004)

* Added new methods in the `org.clapper.util.mail.EmailAddress`, allowing
  specification of a file name to `setText()` and `addAttachment()` methods.
  The file name is used to specify the suggested file name in the MIME
  headers. Also fixed some bugs relating to the assignment of file names
  and determination of MIME types for attachments.

* Added `MIMETypeUtil` class, with enhanced methods to map from a file name
  or extension to a MIME type (similar to what the JDK provides), and to
  map from a MIME type to a preferred extension (which the JDK does not
  provide).


----

Version 1.1.3 (30 October, 2004)

* Added `org.clapper.util.misc.FileHashMap` class, a Map that keeps the keys
  in memory, but stores the values as serialized objects in a random access
  disk file.

* Added `message()` method to `org.clapper.util.logging.Logger` class.


----

Version 1.1.2 (20 October, 2004)

* Moved `org.clapper.util.misc.JDK14TextLogFormatter` and
  `org.clapper.util.misc.Logger` classes to the `org.clapper.util.logging`
  package. Renamed `JDK14TextLogFormatter` to `JavaUtilLoggingTextFormatter`.

* `org.clapper.util.logging.Logger` is now implemented directly in terms of
  `java.util.logging`, eliminating another third-party dependency. Consult
  the javadocs for `org.clapper.logging.Logger` for complete rationale.


----

Version 1.1.1 (12 October, 2004)

* Added `org.clapper.util.misc.JDK14TextLogFormatter` class, a simple
  text log formatter for the 1.4 JDK `java.util.logging` infrastructure.

* Simplified loading of API version, in `build.xml`. Instead of loading
  version from `Version.class`, it now loads the version from a properties
  file. The `Version` class uses the same properties file, as a resource
  bundle.


----

Version 1.1 (07 October, 2004):

* Now requires JDK 1.4 or better. JDK 1.3 is no longer supported.

* Converted code that used Jakarta ORO to use the JDK 1.4
  `java.util.regexp` classes. Includes introduction of new
  org.clapper.util.regex.RegexUtil class, which provides a `substitute()`
  method that takes the place of the `org.apache.oro.text.perl.Perl5Util`
  `substitute()` method.

* Added new `org.clapper.util.misc.LRUMap` class, which implements a simple
  least recently used map.

* `org.clapper.util.misc.NestedException` is now implemented in terms of
  the built-in exception chaining in the 1.4 JDK. `NestedException` is
  retained for backward compatibility and because it provides support for
  localizing exception messages.

* Added `parseIntParameter()`, `parseIntOptionArgument()`,
  `parseFloatParameter()`, `parseFloatOptionArgument()`, etc., parameter
  parsing helper methods to `CommandLineUtility` class.

* Overhauled `build.xml` to make compilations more efficient. Requires a
  newer version of Jikes (if compiling with Jikes).

* Cleaned up a lot of unnecessary imports. Addressed minor issues that
  Jikes warned about.

* Minor javadoc changes and adjustments.


----

Version 1.0.2 (24 September, 2004):

* Drastically simplified word-wrapping logic in `org.clapper.io.WordWrapWriter`.
* Added `rightJustifyString()`, `leftJustifyString()` and `centerString()`
  methods to `org.clapper.util.text.TextUtil`.
* Added `org.clapper.util.io.JustifyTextWriter` class.


----

Version 1.0.1 (16 September, 2004):

* New methods (`copyTextFile()`, `copyReader()`, etc.) in
  `org.clapper.util.io.FileUtil`. `copyTextFile()` explicitly handles
  character set conversion.
* `FileUtil` copy methods no longer do their own buffering. They use the
  appropriate `java.io.Buffered*` classes, instead.
* Miscellaneous javadocs enhancements.


----

Version 1.0 (29 August, 2004):

* First version posted to the web.
