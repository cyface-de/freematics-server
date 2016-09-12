#!groovy
node {
	stage 'Checkout'
	checkout scm

	stage 'Build'
	if(isUnix()) {
		sh 'gradlew clean build'
	} else {
		bat 'gradlew clean build'
	}
}
