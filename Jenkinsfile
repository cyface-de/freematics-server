#!groovy
node {
	stage 'Checkout'
	checkout scm

	stage 'Build'
	if(isUnix()) {
		sh './gradlew --console=plain --no-daemon --info --stacktrace clean build'
	} else {
		bat './gradlew --console=plain --no-daemon --info --stacktrace clean build'
	}
}
