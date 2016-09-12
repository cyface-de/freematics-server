#!groovy
node {
	if(isUnix()) {
		sh 'gradlew clean build'
	} else {
		bat 'gradlew clean build'
	}
}
