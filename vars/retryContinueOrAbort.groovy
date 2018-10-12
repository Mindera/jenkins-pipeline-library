#!/usr/bin/env groovy

// Credits to https://gist.github.com/beercan1989/b66b7643b48434f5bdf7e1c87094acb9 🙏

def call(Map vars, Closure body) {

    vars = vars ?: [:]

    def description = vars.get("description", "action")
    def count = vars.get("count", 0)
    def timeoutMinutes = vars.get("timeoutMinutes", 10)
    def markAsUnstableOnContinue = vars.get("markAsUnstableOnContinue", true)

    echo "⚙️ Trying ${description}, attempt number ${count}."

    try {
        body()
    } catch (final exception) {

        echo "💥 ${description} failed with error '${exception}'"
        echo "🚦 Prompting for next action... (timing out after ${timeoutMinutes} minute(s))"

        def userChoice

        try {
            timeout(time: timeoutMinutes, unit: 'MINUTES') {
                 userChoice = input(
                    message: "🤖 Something went wrong, what do you want to do next?",
                    parameters: [
                        choice(
                            name: "Next Action",
                            choices: ['Retry', 'Continue', 'Abort'].join('\n'),
                            description: "Whats your next action?"
                        )
                    ]
                )
            }
        } catch (final org.jenkinsci.plugins.workflow.steps.FlowInterruptedException timeoutException) {
            def cause = timeoutException.causes.get(0)
            if (cause.getUser().toString() == 'SYSTEM') {
                echo "⏱ Input timed out!"
                throw exception
            } else {
                echo "❌ Aborting input on ${description}."
                throw timeoutException
            }
        }

        switch (userChoice) {
            case 'Retry':
                echo "♻️ Retrying ${description} again."
                retryContinueOrAbort(description: description, count: count + 1, timeoutMinutes: timeoutMinutes) { body() }
                break
            case 'Continue':
                if (markAsUnstableOnContinue) {
                    echo "⚠️ Continuing the pipeline past ${description}, marking build as unstable."
                    currentBuild.result = "UNSTABLE"
                } else {
                    echo "⏩ Continuing the pipeline past ${description}, assuming build is ok."
                }
                break
            default:
                echo "❌ Aborting ${description}."
                throw exception
        }
    }
}