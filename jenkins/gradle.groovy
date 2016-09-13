import com.cloudbees.groovy.cps.NonCPS

def cleanAndCompile(String switches = null) {
    run 'clean build', switches
}

def test(String project = null, String switches = null  ) {
    if (project == null) {
        run ':test', switches
    } else {
        run ':' + project + ':test', switches
    }

}

def codeQualityPmd(String switches = null) {
    run 'pmdMain', switches
}

def codeQualityFindbugs(String switches = null) {
    run 'findbugsMain', switches
}

def codeQualityCheckstyle(String switches = null) {
    run 'checkstyleMain', switches
}

def aggregateJaCoCoReports(String switches = null) {
    run 'jacocoTestReport', switches
}

def assembleApplication(String switches = null) {
    run 'assemble', switches
}

def publishApplication(String switches = null) {
    run 'publish', switches
}

def aggregateSerenityReports(String switches = null) {
    run ':acceptance-test:aggregate', switches
}

@NonCPS
Map conf(String content, String env = 'test') {
    def parsedConfig = new ConfigSlurper(env).parse(content)
    def map = [:]
    parsedConfig.flatten(map)
    return map
}

void run(String tasks, String switches = null) {
    String gradleCommand = "";
    gradleCommand += './gradlew --console=plain --no-daemon --info --stacktrace '
    gradleCommand += tasks

    if(switches != null) {
        gradleCommand += ' '
        gradleCommand += switches
    }

    if(isUnix()) {
        sh gradleCommand.toString()
    } else {
        bat gradleCommand.toString()
    }
}

return this;