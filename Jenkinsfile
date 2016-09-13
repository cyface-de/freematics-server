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
                    gradle.codeQualityPmd()
                    step([$class: 'PmdPublisher', pattern: 'build/reports/pmd/*.xml'])
                },
                'checkstyle': {
                    gradle.codeQualityCheckstyle()
                    step([$class: 'CheckstylePublisher', pattern: 'build/reports/checkstyle/*.xml'])
                },
                'findbugs': {
                    gradle.codeQualityCheckstyle()
                    step([$class: 'FindbugsPublisher', pattern: 'build/reports/findbugs/*.xml'])
                },
                'jacoco': {
                    // Jacoco report rendering
                    gradle.aggregateJaCoCoReports()
                    publish(target: [reportDir:'build/reports/jacoco/jacocoRootTestReport/html',reportFile: 'index.html', reportName: 'Code Coverage'])
                    step([$class: 'JacocoPublisher', execPattern:'build/jacoco/*.exec', classPattern: 'build/classes/main', sourcePattern: 'src/main/java'])
                }
        )

    } catch (e) {
        echo "Caught Exception ${e}"
        e.printStackTrace()
        stage 'Send notifications'

        String recipient = 'klemens.muthmann@gmail.com'

        mail subject: "${env.JOB_NAME} (${env.BUILD_NUMBER}) failed",
                body: "It appears that ${env.BUILD_URL} is failing, you should do something about that!",
                to: recipient,
                replyTo: recipient,
                from: 'noreply@cyface.de'
        error "Failing build because of ${e}"

    }
}
