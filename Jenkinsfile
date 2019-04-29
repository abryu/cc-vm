def project = 'wyownproject'
def appName = 'vm'
def imageTag = "${env.BUILD_NUMBER}"
def registry = "abryu082/vm"
    
pipeline {
    agent {
    kubernetes {
      label 'cc-vm'
      defaultContainer 'jnlp'
      yaml """
apiVersion: v1
kind: Pod
metadata:
labels:
  component: ci
spec:
  # Use service account that can deploy to all namespaces
  serviceAccountName: cd-jenkins
  securityContext:
    runAsUser: 0
  volumes:
  - name: sock-storage
    hostPath:
        path: /var/run/docker.sock
        type: File
  containers:
  - name: maven
    image: gcr.io/cloud-builders/mvn
    command:
    - cat
    tty: true
  - name: docker
    image: gcr.io/cloud-builders/docker
    command:
    - cat
    tty: true
    volumeMounts:
    - name: sock-storage
      mountPath: /var/run/docker.sock
    securityContext:
      privileged: true
  - name: kubectl
    image: gcr.io/cloud-builders/kubectl
    command:
    - cat
    tty: true
"""
}
  }
    stages {
        stage('Checkout') {
            steps {
                git branch: 'master',
                credentialsId: 'gh',
                url: 'https://git@github.com/abryu/cc-vm.git'
            sh "ls -lat"
            }
        }
        stage('Build') {
            steps {
                echo 'Building..'
                container('maven') {
                    sh """
                        ln -s `pwd` /usr/src/cc-vm
                        cd /usr/src/cc-vm
                        mvn clean package
                    """
                }
            }
        }
        stage('Test') {
            steps {
                echo 'Testing..'
            }
        }
        stage('Create Docker images') {
            steps{
                container('docker') {
                    withCredentials([[$class: 'UsernamePasswordMultiBinding',
                    credentialsId: 'dk',
                    usernameVariable: 'DOCKER_HUB_USER',
                    passwordVariable: 'DOCKER_HUB_PASSWORD']]) {
                    sh """
                        docker login -u ${DOCKER_HUB_USER} -p ${DOCKER_HUB_PASSWORD}
                        docker build -t ${DOCKER_HUB_USER}/${appName}:${imageTag} .
                        docker push ${DOCKER_HUB_USER}/${appName}:${imageTag}
                    """
                    }
                }
            }
        }      
        stage('Deploy') {
            steps {
                echo 'Deploying....'
                container('kubectl') {
                    sh """
                        kubectl set image deployment/${appName} ${appName}=${registry}:${imageTag}
                    """
                }
            }
        }
    }
}
