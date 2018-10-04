# jenkins-pipeline-library

A Shared Library for Jenkins pipelines containing useful actions.

## Setup instructions 🛠

1. Configure the shared library in Jenkins:

    1. **Globally**: go to Manage Jenkins -> Configure System. -> _Global Pipeline Libraries_, add a library with the following settings:
    2. **Single pipeline**: go to [your pipeline] -> Configure -> _Pipeline Libraries_.

    Add the following settings:

    - Name: `jenkins-pipeline-library`
    - Default version: Specify a Git reference (branch or commit SHA), e.g. `master`
    - Retrieval method: _Modern SCM_
    - Select the _Git_ type
    - Project repository: `https://github.com/Mindera/jenkins-pipeline-library.git`
    - Credentials: (leave blank)

2. Then create a Jenkins job with the following pipeline (note that the underscore `_` is not a typo):

    ```groovy
    @Library('jenkins-pipeline-library')_

    stage('Demo') {

      retryContinueOrAbort(description: "'Hello World'", timeoutMinutes: 1) {
        echo '🤖 Hello World!'
      }

    }
    ```

This will output the following from the build:

```
[Pipeline] stage
[Pipeline] { (Demo)
[Pipeline] echo
⚙️ Trying 'Hello World', attempt number 0.
[Pipeline] echo
🤖 Hello World!
[Pipeline] }
[Pipeline] // stage
[Pipeline] End of Pipeline
Finished: SUCCESS
```

You're set! 🚀

## Available actions 🤖

### retryContinueOrAbort

Action that receives a closure to perform arbitrary work and on failure asks the user for input on whether to Retry, Continue, or Abort the action. It calls itself recursively on Retry, having no hard limit on the number of possible retries.

Parameter | Type | Default value | Description
------------ | ------------- | ------------- | -------------
description | `String` | `"action"` | description of the action (for logging purposes)
timeoutMinutes | `int` | `10` | number of minutes before timing out waiting for user input
markAsUnstableOnContinue | `bool` | `true` | flag indicating whether to mark the build as `"UNSTABLE"` if Continue is chosen
body | `Closure` | | The work to perform

Example:

```groovy
@Library('jenkins-pipeline-library')_

stage('Demo') {

  retryContinueOrAbort(description: "'my awesome work'", timeoutMinutes: 1) {
      echo "doing awesome work"
      (...)
    }

}
```
