pipeline {
    agent any

    environment {
        DOCKERHUB_CREDENTIALS = 'DockerHubCred'
        DOCKER_COMPOSE_FILE = 'docker-compose.yml'
        GITHUB_REPO_URL = 'https://github.com/rishithaiiitb/ImageCaptioning.git'
    }

    stages {
        stage('Github Checkout') {
            steps {
                script {
                    git branch: 'main', url: "${GITHUB_REPO_URL}"
                }
            }
        }

        stage('Build Backend with Maven') {
            steps {
                script {
                    dir('imagecaptioning') {
                        sh 'mvn clean install'
                    }
                }
            }
        }


        stage('Build Docker Images') {
            steps {
                script {
                    docker.build('rishithaiiitb/backend', './imagecaptioning')
                    docker.build('rishithaiiitb/frontend', './icfe')
                }
            }
        }

        stage('Push Docker Images') {
            steps {
                script {
                    docker.withRegistry('', "${DOCKERHUB_CREDENTIALS}") {
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
            script {
                sh 'docker logout'
            }
            emailext(
                subject: "Pipeline Status: ${currentBuild.result}",
                body: "Build URL: ${BUILD_URL}",
                to: "rishichinnu27@gmail.com",
                from: "jenkins@yourdomain.com"
            )
        }
    }
}

