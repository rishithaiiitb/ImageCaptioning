pipeline {
    agent any

    environment {
        DOCKERHUB_CREDENTIALS = 'DockerHubCred'
        DOCKER_COMPOSE_FILE = 'docker-compose.yml'
        GITHUB_REPO_URL = 'https://github.com/rishithaiiitb/ImageCaptioning.git'
    }

    stages {
        stage('Cleanup Docker Images') {
            steps {
                script {
                    sh 'docker rm -f imagecapdb model-service imagecap-service imagecapapp || true'
                    sh 'docker rmi -f rishithaiiitb/bemodel rishithaiiitb/backend rishithaiiitb/frontend || true'
                }
            }
        }

        stage('Github Checkout') {
            steps {
                script {
                    git branch: 'main', url: "${GITHUB_REPO_URL}"
                }
            }
        }

        stage('Build Docker Images') {
            steps {
                script {
                    docker.build('rishithaiiitb/bemodel', './model-service')
                    docker.build('rishithaiiitb/backend', './backend')
                    docker.build('rishithaiiitb/frontend', './frontend')
                }
            }
        }

        stage('Push Docker Images') {
            steps {
                script {
                    docker.withRegistry('', "${DOCKERHUB_CREDENTIALS}") {
                        // Push Model Service Docker Image
                        sh 'docker tag rishithaiiitb/bemodel rishithaiiitb/bemodel:latest'
                        sh 'docker push rishithaiiitb/bemodel:latest'

                        // Push Backend Docker Image
                        sh 'docker tag rishithaiiitb/backend rishithaiiitb/backend:latest'
                        sh 'docker push rishithaiiitb/backend:latest'

                        // Push Frontend Docker Image
                        sh 'docker tag rishithaiiitb/frontend rishithaiiitb/frontend:latest'
                        sh 'docker push rishithaiiitb/frontend:latest'
                    }
                }
            }
        }

        stage('Run Ansible Playbook') {
            steps {
                script {
                    ansiblePlaybook(
                        playbook: 'deploy.yml',
                        inventory: 'inventory'
                    )
                }
            }
        }
    }

    post {
        always {
            emailext(
                subject: "Pipeline Status: ${currentBuild.result}",
                body: "Build URL: ${BUILD_URL}",
                to: "rishichinnu27@gmail.com",
                from: "jenkins@yourdomain.com"
            )
        }
    }
}

