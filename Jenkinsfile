#!groovy
node {
    def exception = null;
    try {
        stage 'Checkout'
        // get source code
        checkout scm

        stage 'Build'
        gradle = load 'jenkins/gradle.groovy'

        // check that the whole project compiles
        // gradle 'clean build'
        gradle.cleanAndCompile()

        stage 'Test'
        gradle.test()
        step([$class: 'JUnitResultArchiver', testResults:
                '**/build/test-results/TEST-*.xml'])

        stage 'Code Quality'
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
                        step([$class: 'JacocoPublisher', execPattern: 'build/jacoco/*.exec', classPattern: 'build/classes/main', sourcePattern: 'src/main/java'])
                }
        )

    } catch (e) {
        exception = e;
    }

    stage 'Send notification'
    if (exception != null) {
        echo "Caught Exception ${e}"
        stage 'Send notifications'

        String recipient = 'klemens.muthmann@gmail.com'

        mail subject: "${env.JOB_NAME} (${env.BUILD_NUMBER}) failed",
                body: "It appears that ${env.BUILD_URL} is failing, you should do something about that!\n\n" + e,
                to: recipient,
                replyTo: recipient,
                from: 'noreply@cyface.de'
        error "Failing build because of ${e}"
    }
}
