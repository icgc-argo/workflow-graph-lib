def gitHubRepo = "icgc-argo/workflow-graph-lib"
def commit = "UNKNOWN"
def version = "UNKNOWN"


pipeline {
    agent {
        kubernetes {
            label 'wf-graph-lib'
            yaml """
apiVersion: v1
kind: Pod
spec:
  containers:
  - name: maven
    command: ['cat']
    tty: true
    image: maven:3.6.3-openjdk-11
"""
        }
    }
    stages {
        stage('Prepare') {
            steps {
                script {
                    commit = sh(returnStdout: true, script: 'git describe --always').trim()
                }
                script {
                    version = readMavenPom().getVersion()
                }
            }
        }
        stage('Test') {
            steps {
                container('maven') {
                    sh "mvn test"
                }
            }
        }
        stage('Build Artifact & Publish') {
             when {
                anyOf {
                    branch "master"
                    branch "develop"
                }
            }
            steps {
                container('maven') {
                    configFileProvider(
                        [configFile(fileId: '894c5ba8-e7cf-4465-98d4-b213eeaa77ef', variable: 'MAVEN_SETTINGS')]) {
                        sh 'mvn -s $MAVEN_SETTINGS clean package deploy'
                    }
                }
            }
        }
    }
}
