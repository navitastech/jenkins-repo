
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
    job("${jobName}") {

      logRotator {
          numToKeep(5)
      }

      concurrentBuild()

      authenticationToken('remote12345')

      if (jobName.contains("commit")) {
      scm {

            git {
                remote {
                    url ('https://github.com/VariQ/Navitas_Flash.git')
                }
            }
          }
      }


      wrappers {
        colorizeOutput('xterm')
        preBuildCleanup()
      }

      configure { project ->
            project / 'buildWrappers' / 'com.michelin.cio.hudson.plugins.maskpasswords.MaskPasswordsBuildWrapper' / 'varPasswordPairs'() {
        }
      }

        steps {


        shell(
          readFileFromWorkspace("jobs/app/${jobName}.sh")
        )

        }

        publishers {

          if (jobName.contains("commit")) {

            downstreamParameterized {
              trigger("app-deploy-dev") {
                   condition('SUCCESS')
                   parameters {
                       currentBuild()
                    }
              }
            }
          }
        }

    }
}
}
createApiJobs()
