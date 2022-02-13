pipeline {
    agent any
    tools {
        jdk 'Jdk17'
        maven 'maven'
    }
    stages {
        stage('Build') {
            steps {
                echo 'Building..'
                sh 'mvn clean package'
            }
        }
        stage('Post') {
            steps {
                archiveArtifacts 'target/CrossplatForms*.jar'
                discordSend description: "**Build:** [${currentBuild.id}](${env.BUILD_URL})\n**Status:** [${currentBuild.currentResult}]" , footer: 'ProjectG', link: env.BUILD_URL, result: currentBuild.currentResult, title: "ProjectG-Plugins/CrossplatForms/${env.BRANCH_NAME}", webhookURL: "https://discordapp.com/api/webhooks/853951848948170762/JFgacab-AdmEfjp05MFfb26a0zIN7kdmVL0f3MMcWodNxMX20xFDqs9TGJJG-aH1Iwpu"

                  }

                }
        }
}
