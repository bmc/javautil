#                                                                   -*- ruby -*-
# buildr buildfile for javautil
# ---------------------------------------------------------------------------

# Dependencies.
JAVAX           = 'javax.activation:activation:jar:1.1-rev-1'
JAVAMAIL        = 'javax.mail:mail:jar:1.4.4'
ASM             = 'asm:asm:jar:3.3.1'
ASM_COMMONS     = 'asm:asm-commons:jar:3.3.1'
COMMONS_LOGGING = transitive('commons-logging:commons-logging:jar:1.1.1')

LOG4J           = 'log4j:log4j:jar:1.2.16'

# Where we publish
UPLOAD_REPO     = 'sftp://maven.clapper.org/var/www/maven.clapper.org/html'

# Some local tasks and task aliases
Project.local_task :publish

# The project definition itself.
define 'javautil' do
  project.version = '3.0.1'
  project.group   = 'org.clapper'

  package :jar

  compile.using :target => '1.5', :lint => 'all', :deprecation => true
  compile.with JAVAX, JAVAMAIL, ASM, ASM_COMMONS, COMMONS_LOGGING

  test.using :environment => {}, :fork => true
  test.with LOG4J

  repositories.remote << 'http://www.ibiblio.org/maven2/'
  repositories.release_to[:url] = UPLOAD_REPO
  repositories.release_to[:username] = 'bmc'

  # Task alias, because I forget that it's "upload".
  task :publish => :upload
end
