pipeline {
    agent any
    environment {
        DOCKER_CONTAINER = "workSporesAdmin"
        DOCKER_IMAGE = "work_spores-admin_service"
        DOCKER_TAG = "${DOCKER_IMAGE}:${BUILD_NUMBER}"
        DOCKER_NETWORK = "work_spores_main-network"
        DOCKER_PORT = "8082:8080"
    }
    stages {
        stage ('Initialize') {
            steps {
                sh '''
                    echo "PATH = ${PATH}"
                    echo "WORKSPACE = ${WORKSPACE}"
                    echo "DOCKER_TAG = ${DOCKER_TAG}"
                    echo "DOCKER_NETWORK = ${DOCKER_NETWORK}"
                    echo "DOCKER_PORT = ${DOCKER_PORT}"
                    echo "GIT_COMMIT = ${GIT_COMMIT}"
                '''
            }
        }
        stage ('Build Tag') {
            steps {
                sh 'docker build -f ./admin/Dockerfile . -t ${DOCKER_TAG}'
            }
            post {
                success {
                    sh '''
                        echo "Build Tag successfully"
                    '''
                }
            }
        }
        stage ('Stop & Remove Container') {
            steps {
                sh 'docker ps -q --filter "name=${DOCKER_CONTAINER}" | xargs -r docker stop'
                sh 'docker ps -aq --filter "name=${DOCKER_CONTAINER}" | xargs -r docker rm -v'
            }
            post {
                success {
                    sh '''
                        echo "Stop & Remove Docker container successfully"
                    '''
                }
            }
        }
        stage ('Run Tag') {
            steps {
                sh 'docker run -v -it -d -p ${DOCKER_PORT} --net ${DOCKER_NETWORK} --name ${DOCKER_CONTAINER} ${DOCKER_TAG}'
            }
            post {
                success {
                    sh '''
                        echo "Build Docker successfully"
                    '''
                }
            }
        }
    }
}

