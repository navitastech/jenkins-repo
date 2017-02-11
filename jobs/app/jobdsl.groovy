
apiJobList = [ "app-build-on-commit", "app-deploy-dev","app-deploy-dev-container", "app-build-rc", "app-deploy-prod-container","app-deploy-prod", "app-pr-builder"]

listView('Demo-Api') {
    description('Demo-App build/deploy jobs.')
    filterBuildQueue()
    filterExecutors()
    jobs {

        apiJobList.each {
            name("${it}")
        }


    }
    columns {
        status()
        weather()
        name()
        lastSuccess()
        lastFailure()
        lastDuration()
        buildButton()
    }
}


def createApiJobs() {
apiJobList.each { jobName ->
    pipelineJob("${jobName}") {

      
      
       definition {
       cpsScm {
            scm {
                git {
                remote {
                    url ('https://github.com/')
                }
                branch ('master-only-no-sec')
           		 }
       scriptPath("Jenkinsfile")
            }
        }
    }

    }
}
}
createApiJobs()
