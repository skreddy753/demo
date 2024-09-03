pipeline {
  agent any
  tools {
    maven 'maven'
  }

  environment {
    registry = "skreddy753/demo:1.0"
    registryCredential = '5859cb06-ef3d-42d4-9fdb-1036be8b0620'
  }

  stages {
    stage('Maven Build') {
      steps {
        checkout scmGit(branches: [
          [name: '*/main']
        ], extensions: [], userRemoteConfigs: [
          [credentialsId: '7d7cc0da-445b-48a0-9355-c345ae8b6441', url: 'https://github.com/skreddy753/demo.git']
        ])
        sh 'mvn clean install'
      }
    }

    stage('Build docker image') {
        steps{
            script {
                sh 'docker build -t skreddy753/demo .'
            }
        }
    }

    stage('Push image to docker hub') {
        steps{
            script {
                withCredentials([string(credentialsId: 'docker-pwd', variable: 'docker-pwd')]) {
                    sh 'docker login -u skreddy753 -p ${docker-pwd}'
                }

                sh 'docker push skreddy753/demo'
            }
        }
    }

    stage('Deploy') {
        steps{
            kubernetesDeploy(configs: 'k8s-deployment.yml', kubeconfigId: 'k8s-pwd')
        }
    }

  }
}