job('seed-job') {



  properties {
      githubProjectUrl('https://github.com/steadystatecd/jenkins-repo/')
  }

scm {

  git {
    remote {
      url ('https://github.com/steadystatecd/jenkins-repo.git')
    }
    branch ('master')

  }

}



wrappers {
  colorizeOutput('xterm')
}

    steps {
      dsl {
        external('jobs/**/*.groovy')
      }
    }


}
