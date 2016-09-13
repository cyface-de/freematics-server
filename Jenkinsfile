#!groovy
node {
    def exception = null;
    try {
        stage('Checkout') {
            // get source code
            checkout scm
        }

        stage('Build') {
            gradle = load 'jenkins/gradle.groovy'

            // check that the whole project compiles
            // gradle 'clean build'
            gradle.cleanAndCompile()
        }

        stage('Test') {
            gradle.test()
            step([$class: 'JUnitResultArchiver', testResults:
                    '**/build/test-results/TEST-*.xml'])
        }

        stage('Code Quality') {
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

        // TODO this is a temporary fix until the Sonarqube plugin has been adapted to pipelines: https://github.com/jenkinsci/pipeline-plugin/blob/master/COMPATIBILITY.md
        // Requires the Credentials Binding plugin
        stage('Publish Metrics to Sonarqube') {
            withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'sonar-creds', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
                echo "Accessing sonarqube with username ${env.USERNAME} and password ${env.PASSWORD}."
                sh "/opt/sonar_scanner/bin/sonar-scanner -e -Dsonar.host.url=http://sonarqube:9000 -Dsonar.login=${env.USERNAME} -Dsonar.password=${env.PASSWORD} -Dsonar.projectKey=freematics-server -Dsonar.projectName=freematics-server -Dsonar.projectVersion=1.0.0-SNAPSHOT -Dsonar.sources=src/main/java -Dsonar.tests=src/test/java -Dsonar.binaries=build/classes -Dsonar.language=java -Dsonar.sourceEncoding=UTF-8 -Dsonar.java.source=1.8 -Dsonar.jacoco.reportPath=build/jacoco/test.exec"
            }
        }
    } catch (e) {
        exception = e;
    }

    stage('Send notification') {
        if (exception != null) {
            echo "Caught Exception ${exception}"

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
