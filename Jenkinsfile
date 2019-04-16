pipeline {
    agent any
    tools {
        maven 'Maven 3.3.9'
        jdk 'jdk8'
    }
    stages {
        stage('Initialize') {
            steps {
                echo 'Initialize..'
                sh ''' echo "PATH = ${PATH}"
                       echo "M2_HOME = ${M2_HOME}"
                       echo $JAVA_HOME
                       echo mvn -version
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
