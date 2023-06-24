pipeline {
    agent any
    triggers {
        githubPush()
    }
    tools {
        maven "maven-3.9.2"
    }
    environment {
        GPG_PASSPHRASE = credentials("gpg-passphrase")
    }
    stages {
        stage("Build") {
            steps {
                script {
                    sh "mvn package"
                }
            }
        }
        stage("Deploy") {
            steps {
                script {
                    sh "mvn deploy -Dgpg.passphrase=$GPG_PASSPHRASE"
                }
            }
        }
    }
}