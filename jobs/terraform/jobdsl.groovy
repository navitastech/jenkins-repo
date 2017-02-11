tfJobList = [ "create-tf-stack", "destroy-tf-stack","plan-tf-stack", "update-tf-stack"]

listView('Demo-Infrastructure') {
    description('Infrastructure Jobs')
    filterBuildQueue()
    filterExecutors()
    jobs {
        tfJobList.each {
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

def createtfJobs() {
tfJobList.each { jobName ->
    job("${jobName}") {

      logRotator {
          numToKeep(5)
      }



                  parameters {

                        choiceParam('STACK_REGION', ['us-east-1', 'us-west-2'])

                        choiceParam('APPENV', ['dev', 'prod'])

                        choiceParam('STACK_TYPE', ['JumpBox', 'Jenkins','Apache','Tomcat'])

                        stringParam('STACK_NAME', '', 'Name of the stack')


                  }

      concurrentBuild()

      authenticationToken('remote12345')



      scm {

            git {
                remote {
                    url ('https://github.com/skdandamudi/infrastructure-stack.git')
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
          readFileFromWorkspace("jobs/terraform/${jobName}.sh")
          )

        }

        publishers {


        }

    }
}
}

createtfJobs()
