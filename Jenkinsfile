#!groovy
node {
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
                    gradle.codeQuality()
                    step([$class: 'PmdPublisher', pattern: 'build/reports/pmd/*.xml'])
                },
                'jacoco': {
                    // Jacoco report rendering
                    gradle.aggregateJaCoCoReports()
                    publish(target: [reportDir:'build/reports/jacoco/jacocoRootTestReport/html',reportFile: 'index.html', reportName: 'Code Coverage'])
                }
        )

    } catch (e) {
        stage 'Message'
        echo "Caught: ${e}"

        String recipient = 'klemens.muthmann@gmail.com'

        mail subject: "${env.JOB_NAME} (${env.BUILD_NUMBER}) failed",
                body: "It appears that ${env.BUILD_URL} is failing, somebody should do something about that",
                to: recipient,
                replyTo: recipient,
                from: 'noreply@cyface.de'

    }
}
