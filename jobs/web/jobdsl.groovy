webJobList = [ "web-build-on-commit",  "web-deploy-dev","web-deploy-dev-container", "web-build-rc", "web-deploy-prod-container","web-deploy-prod", "web-pr-builder"]

listView('Demo-Web') {
    description('Demo-App build/deploy jobs.')
    filterBuildQueue()
    filterExecutors()
    jobs {

        webJobList.each {
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



def createWebJobs() {
webJobList.each { jobName ->
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
          readFileFromWorkspace("jobs/web/${jobName}.sh")
        )

        }

        publishers {

          if (jobName.contains("commit")) {

            downstreamParameterized {
              trigger("web-deploy-dev") {
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
createWebJobs()
