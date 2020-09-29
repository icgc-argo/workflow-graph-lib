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
  - name: graal
    command: ['cat']
    tty: true
    image: icgcargo/graalvm:java11-20.2.0
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
                container('graal') {
                    sh "./mvnw test"
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
                container('graal') {
                    configFileProvider(
                        [configFile(fileId: '894c5ba8-e7cf-4465-98d4-b213eeaa77ef', variable: 'MAVEN_SETTINGS')]) {
                        sh './mvnw -s $MAVEN_SETTINGS clean package deploy'
                    }
                }
            }
        }
    }
}
