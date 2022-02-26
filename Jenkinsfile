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
                discordSend description: "**Build:** [${currentBuild.id}](${env.BUILD_URL})\n**Status:** [${currentBuild.currentResult}]" , footer: 'ProjectG', link: env.BUILD_URL, result: currentBuild.currentResult, title: "ProjectG-Plugins/CrossplatForms/${env.BRANCH_NAME}", webhookURL: "https://discord.com/api/webhooks/853664946487296021/O2RKI76V1XRMI15-qG940htCrn7YeXRM9afwYubx4mKR4P66mM1N2hCRACpGo4XW4cw9"
            }
        }
    }
}
