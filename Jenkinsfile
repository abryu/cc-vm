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
  containers:
  - name: maven
    image: gcr.io/cloud-builders/mvn
    args: '-v "$(pwd)/cc-vm":/usr/src/mymaven -w /usr/src/mymaven'
    command:
    - cat
    tty: true
  - name: gcloud
    image: gcr.io/cloud-builders/gcloud
    command:
    - cat
    tty: true
  - name: kubectl
    image: gcr.io/cloud-builders/kubectl
    command:
    - cat
    tty: true
"""
}
  }
    stages {
        stage('Initialize') {
            steps {
                echo 'Initialize..'
            }
        }
        stage('Build') {
            steps {
                echo 'Building..'
                container('maven') {
                    sh """
                        ls /usr/src
                        cd /usr/scr/mymaven
                        mvn clean package
                        ls
                    """
                }
            }
        }
        stage('Test') {
            steps {
                echo 'Testing..'
            }
        }
        stage('Deploy') {
            steps {
                echo 'Deploying....'
            }
        }
    }
}
