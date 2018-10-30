#!/usr/bin/env groovy

def call() {
    
    def jobName = env.JOB_NAME
    def currentBuildNumber = env.BUILD_NUMBER.toInteger()

    // Get job by name
    def job = Jenkins.instance.getItemByFullName(jobName)
    def newestBuild = job.builds.first()

    // If there is a build that has a higher build number
    if (newestBuild.number > currentBuildNumber) {
        
        currentBuild.result = 'ABORTED'
        error('Aborted due to a newer build on queue')
    }
}