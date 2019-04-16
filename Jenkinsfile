pipeline {
    agent any
    tools {
        maven 'Maven 3.3.9'
        jdk 'jdk8'
    }
    stages {
        staeg('Initialize') {
            steps {
                echo 'Initialize..'
                sh ''' echo "PATH = ${PATH}"
                       echo "M2_HOME = ${M2_HOME}"
                   '''
            }
        }
        stage('Build') {
            steps {
                echo 'Building..'
                sh 'mvn clean'
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
