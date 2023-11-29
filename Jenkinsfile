pipeline {
    agent any
    triggers {
        githubPush()
    }
    stages {
        stage("Test") {
            agent {
                docker {
                    image 'maven:3.9.3-amazoncorretto-8'
                    reuseNode true
                }
            }
            steps {
                script {
                    sh "mvn test"
                }
            }
        }
    }
}
