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
  - name: mvn
    tty: true
    image: maven:3.6.3-openjdk-11
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
                container('mvn') {
                    sh "mvn test"
                }
            }
        }

        stage('Build & Publish') {
             when {
                anyOf {
                    branch "master"
                    branch "develop"
                }
            }
            steps {
                container('mvn') {
                    sh "mvn deploy"
                }
            }
        }
    }
}
