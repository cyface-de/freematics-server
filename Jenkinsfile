#!groovy
node {
    def exception = null;
    try {
        stage 'Checkout' {
            // get source code
            checkout scm
        }

        stage 'Build' {
            gradle = load 'jenkins/gradle.groovy'

            // check that the whole project compiles
            // gradle 'clean build'
            gradle.cleanAndCompile()
        }

        stage 'Test' {
            gradle.test()
            step([$class: 'JUnitResultArchiver', testResults:
                    '**/build/test-results/TEST-*.xml'])
        }

        stage 'Code Quality' {
            parallel(
                    'pmd': {
                        // static code analysis
                        gradle.codeQualityPmd()
                        step([$class: 'PmdPublisher', pattern: 'build/reports/pmd/*.xml'])
                    },
                    'checkstyle': {
                        gradle.codeQualityCheckstyle()
                        step([$class: 'CheckStylePublisher', pattern: 'build/reports/checkstyle/*.xml'])
                    },
                    'findbugs': {
                        gradle.codeQualityCheckstyle()
                        step([$class: 'FindBugsPublisher', pattern: 'build/reports/findbugs/*.xml'])
                    },
                    'jacoco': {
                        // Jacoco report rendering
                        gradle.aggregateJaCoCoReports()
                        //publish(target: [reportDir:'build/reports/jacoco/jacocoTestReport/html',reportFile: 'index.html', reportName: 'Code Coverage'])
                        //step([$class: 'JaCoCoPublisher', execPattern: 'build/jacoco/*.exec', classPattern: 'build/classes/main', sourcePattern: 'src/main/java'])
                    }
            )
        }

        stage 'Publish Metrics to Sonarqube' {
            def sonarqubeScannerHome = tool name: 'SonarQubeScanner', type: 'hudson.plugins.sonar.SonarRunnerInstallation'
            sh "${sonarqubeScannerHome}/bin/sonar-scanner -e " +
                    "-Dsonar.host.url=http://sonarqube:9000" +
                    // Required Metadata
                    "-Dsonar.projectKey=freematics-server" +
                    "-Dsonar.projectName=freematics-server" +
                    "-Dsonar.projectVersion=1.0.0-SNAPSHOT" +
                    // Paths to source directories
                    // Paths are relative to the sonar-project.properties file. Replace "\" by "/" on Windows.
                    // Do not put the "sonar-project.properties" file in the same directory with the source code.
                    // (i.e. never set the "sonar.sources" property to ".")
                    "-Dsonar.sources=src/main/java" +
                    // path to test source directories (optional)
                    "-Dsonar.tests=src/test/java" +
                    // path to project binaries (optional), for example directory of Java bytecode
                    "-Dsonar.binaries=build/classes" +
                    // The value of the property must be the key of the language.
                    "-Dsonar.language=java" +
                    // Encoding of the source code
                    "-Dsonar.sourceEncoding=UTF-8" +
                    "-Dsonar.java.source=1.8" +
                    "-Dsonar.jacoco.reportPath=build/jacoco/test.exec"
        }
    } catch (e) {
        exception = e;
    }

    stage 'Send notification' {
        if (exception != null) {
            echo "Caught Exception ${exception}"
            stage 'Send notifications'

            String recipient = 'klemens.muthmann@gmail.com'

            mail subject: "${env.JOB_NAME} (${env.BUILD_NUMBER}) failed",
                    body: "It appears that ${env.BUILD_URL} is failing, you should do something about that!\n\n" + exception,
                    to: recipient,
                    replyTo: recipient,
                    from: 'noreply@cyface.de'
            error "Failing build because of ${exception}"
        }
    }
}
