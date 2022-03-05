pipeline {
    agent any
    tools {
        jdk 'Jdk17'
        gradle 'gradle'
    }
    stages {
        stage('Build') {
            steps {
                echo 'Building..'
                sh './gradlew clean build'
            }
        }
        stage('Post') {
            steps {
                archiveArtifacts 'spigot/build/libs/CrossplatForms-Spigot.jar'
                discordSend description: "**Build:** [${currentBuild.id}](${env.BUILD_URL})\n**Status:** [${currentBuild.currentResult}]" , footer: 'ProjectG', link: env.BUILD_URL, result: currentBuild.currentResult, title: "ProjectG-Plugins/CrossplatForms/${env.BRANCH_NAME}", webhookURL: "webhook"
            }
        }
    }
}
