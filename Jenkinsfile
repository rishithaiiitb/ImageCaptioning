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
                    	sh 'mvn clean package'
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
                    docker.build('rishithaiiitb/bemodel', './model')
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

                        // Push Model Docker Image
                        sh 'docker tag rishithaiiitb/bemodel rishithaiiitb/bemodel:latest'
                        sh 'docker push rishithaiiitb/bemodel:latest'
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
            script {
                emailext(
                    subject: "Pipeline Status: ${currentBuild.result}",
                    body: """Build Status: ${currentBuild.result}
                             <br><br>
                             Build URL: ${BUILD_URL}
                             <br><br>
                             Check the Jenkins console for details.""",
                    to: "rishichinnu27@gmail.com", 
                    from: "jenkins@yourdomain.com",
                    mimeType: 'text/html'
                )
            }
        }
    }
}

//check
