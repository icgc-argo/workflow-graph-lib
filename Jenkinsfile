def dockerHubRepo = "icgcargo/workflow-graph-lib"
def gitHubRepo = "icgc-argo/workflow-graph-lib"
def chartVersion = "0.5.0"
def commit = "UNKNOWN"
def version = "UNKNOWN"

def dockerHubRepo = "icgcargo/workflow-management"
def gitHubRepo = "icgc-argo/workflow-management"
def chartVersion = "0.5.0"
def commit = "UNKNOWN"
def version = "UNKNOWN"

pipeline {
    agent {
        kubernetes {
            label 'wf-management'
            yaml """
apiVersion: v1
kind: Pod
spec:
  containers:
  - name: jdk
    tty: true
    image: openjdk:11
    env:
      - name: DOCKER_HOST
        value: tcp://localhost:2375
  - name: dind-daemon
    image: docker:18.06-dind
    securityContext:
        privileged: true
    volumeMounts:
      - name: docker-graph-storage
        mountPath: /var/lib/docker
  - name: docker
    image: docker:18-git
    tty: true
    volumeMounts:
    - mountPath: /var/run/docker.sock
      name: docker-sock
  volumes:
  - name: docker-sock
    hostPath:
      path: /var/run/docker.sock
      type: File
  - name: docker-graph-storage
    emptyDir: {}
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
                container('jdk') {
                    sh "./mvnw test"
                }
            }
        }

        stage('Build & Publish') {
             when {
                branch "develop"
            }
            steps {
                container('jdk') {
                    sh "./mvnw deploy"
                }
            }
        }
    }
}
