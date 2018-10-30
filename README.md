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

#### Parameters

Parameter | Type | Default value | Description
------------ | ------------- | ------------- | -------------
description | `String` | `"action"` | description of the action (for logging purposes)
timeoutMinutes | `int` | `10` | number of minutes before timing out waiting for user input
markAsUnstableOnContinue | `bool` | `true` | flag indicating whether to mark the build as `"UNSTABLE"` if Continue is chosen
body | `Closure` | | The work to perform

#### Return

N/A

#### Example

```groovy
@Library('jenkins-pipeline-library')_

stage('Demo') {

  retryContinueOrAbort(description: "'my awesome work'", timeoutMinutes: 1) {
      echo "doing awesome work"
      (...)
    }

}
```

### runOrSkip

Action that receives a closure to perform arbitrary work but prior to running it asks the user for input on whether to Run or Skip it.

#### Parameters

Parameter | Type | Default value | Description
------------ | ------------- | ------------- | -------------
description | `String` | `"action"` | description of the action (for logging purposes)
timeoutMinutes | `int` | `1` | number of minutes before timing out waiting for user input
markAsUnstableOnSkip | `bool` | `true` | flag indicating whether to mark the build as `"UNSTABLE"` if Skip is chosen
body | `Closure` | | The work to perform

#### Return

Return Type | Description
------------ | -------------
`boolean` | `true` if the block was run, `false` otherwise (i.e. was skipped)

#### Example

```groovy
@Library('jenkins-pipeline-library')_

stage('Demo') {
  environment {
    didRun = false
  }

  didRun = runOrSkip(description: "'my awesome work'", timeoutMinutes: 1) {
      echo "doing awesome work"
      (...)
    }

  script {
    if (didRun) {
      echo "do stuff only when awesome work is run"
      (...)
    }
  }

}
```

### abortBuildIfNewerExists

Action that aborts a build if the current job has a newer build to execute.

#### Parameters

N/A

#### Return

N/A

#### Example

```groovy
@Library('jenkins-pipeline-library')_

stage('Demo') {

  abortBuildIfNewerExists()

}
```
