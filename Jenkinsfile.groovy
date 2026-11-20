
pipeline {
    agent {
        label 'worker'
    }

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    stages {

        stage('Checkout') {
            steps {
                echo 'Downloading latest GitHub code'
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo 'Building website'
                sh 'ls -la'
                sh 'test -f index.html'
            }
        }

        stage('Deploy') {
            steps {
                echo 'Deploying website to Nginx'

                sh '''
                    rsync -av --delete \
                    --exclude=Jenkinsfile \
                    ./ /var/www/html/
                '''
            }
        }

        stage('Verify Deployment') {
            steps {
                sh '''
                    curl -f http://localhost/
                '''
            }
        }
    }

    post {
        success {
            echo 'Website deployed successfully!'
        }

        failure {
            echo 'Deployment failed. Check Jenkins console output.'
        }
    }
}
