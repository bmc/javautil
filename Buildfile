#                                                                   -*- ruby -*-
# buildr buildfile for javautil
# ---------------------------------------------------------------------------

# ---------------------------------------------------------------------------
# Imports
# ---------------------------------------------------------------------------

require 'buildr/resolver'

# ---------------------------------------------------------------------------
# Dependencies.
# ---------------------------------------------------------------------------

repositories.remote << 'http://repo.maven.apache.org/maven2/'

JAVAX            = 'javax.activation:activation:jar:1.1-rev-1'
JAVAMAIL         = 'javax.mail:mail:jar:1.4.4'
ASM              = 'asm:asm:jar:3.3.1'
ASM_COMMONS      = 'asm:asm-commons:jar:3.3.1'
COMMONS_LOGGING  = 'commons-logging:commons-logging:jar:1.1.1'
SLF4J            = 'org.slf4j:slf4j-jdk14:jar:1.6.4'

DEPS = [JAVAX, JAVAMAIL, ASM, ASM_COMMONS, COMMONS_LOGGING]

# ---------------------------------------------------------------------------
# Definitions
# ---------------------------------------------------------------------------

# Project
PROJECT          = 'javautil'

# Where we publish
UPLOAD_REPO      = 'sftp://maven.clapper.org/var/www/maven.clapper.org/html'

# Where we copy the API docs and change log
API_DOC_TARGET   = '../gh-pages/api'
CHANGELOG_TARGET = '../gh-pages/CHANGELOG.md'

MAIN_BUNDLE      = 'src/main/resources/org/clapper/util/misc/Bundle.properties'

# Buildr needs THIS_VERSION to be a string.
VERSION          = File.open(MAIN_BUNDLE) do |f|
  f.readlines.select {|s| s =~ /^api\.version/}.map {|s| s.chomp.sub(/^.*=/, '')}
end[0]
THIS_VERSION     = "#{VERSION}"
THIS_POM         = "target/#{PROJECT}-#{VERSION}.pom"

# This artifact
ARTIFACT         = "org.clapper:#{PROJECT}:jar:#{VERSION}"

# ---------------------------------------------------------------------------
# Aliases
# ---------------------------------------------------------------------------

# Some local tasks and task aliases
Project.local_task :publish
Project.local_task :copydoc
Project.local_task :version
Project.local_task :pom

# ---------------------------------------------------------------------------
# Project definition
# ---------------------------------------------------------------------------

# The project definition itself.
define PROJECT do
  Java.load
  
  project.version = VERSION
  project.group   = 'org.clapper'

  $pkg = package :jar

  repositories.release_to[:url] = UPLOAD_REPO
  repositories.release_to[:username] = 'bmc'

  compile.using :target => '1.6', :lint => 'all', :deprecation => true
  compile.with DEPS

  test.using :environment => {}, :fork => true
  test.with SLF4J

  task :version do
    msg THIS_VERSION
  end

  task :pom do
    deps = Buildr::Resolver.resolve(DEPS)
    Buildr::Resolver.write_pom(ARTIFACT, THIS_POM)
  end

  task :publish => [:copydoc, :upload]

  task :install => [:package]

  task :copydoc => :doc do
    rm_r API_DOC_TARGET
    cp_r 'target/doc', API_DOC_TARGET
    cp 'CHANGELOG.md', CHANGELOG_TARGET
  end

end

# ---------------------------------------------------------------------------
# Utility Functions
# ---------------------------------------------------------------------------

def msg(s)
  $stderr.puts "*** #{s}"
end

# ---------------------------------------------------------------------------
# Gross and ugly hacks
# ---------------------------------------------------------------------------

class Local
  # Create a POM that has dependencies in it. Uses the buildr/resolver gem.
  def self.make_pom
    mkdir_p File.dirname(THIS_POM)
    deps = Buildr::Resolver.resolve(DEPS)
    Buildr::Resolver.write_pom(ARTIFACT, THIS_POM)
  end
end

module Buildr

  # Local hack job to override Buildr's default POM generation, to include
  # dependencies in the POM.

  module Package
    alias :old_package :package
    def package(*args)
      old_package *args
      Local.make_pom
    end

  end

  module ActsAsArtifact

    def pom_xml
      Local.make_pom
      File.open(THIS_POM).readlines.join('')
    end
  end
end


