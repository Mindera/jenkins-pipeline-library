#!/usr/bin/env groovy

def call(Map vars, Closure body) {

    vars = vars ?: [:]

    def description = vars.get("description", "action")
    def timeoutMinutes = vars.get("timeoutMinutes", 1)
    def markAsUnstableOnSkip = vars.get("markAsUnstableOnSkip", true)

    echo "🚦 Prompting for next action... (timing out after ${timeoutMinutes} minute(s))"

    def userChoice

    try {
        timeout(time: timeoutMinutes, unit: 'MINUTES') {
             userChoice = input(
                message: "🤖 Run ${description}?",
                parameters: [
                    choice(
                        name: "Next Action",
                        choices: ['Run', 'Skip'].join('\n'),
                        description: "Whats your next action?"
                    )
                ]
            )
        }
    } catch (final org.jenkinsci.plugins.workflow.steps.FlowInterruptedException timeoutException) {
        def cause = timeoutException.causes.get(0)
        if (cause.getUser().toString() == 'SYSTEM') {
            echo "⏱ Input timed out! Running ${description}..."
            body()
        } else {
            echo "❌ Aborting input on ${description}."
            throw timeoutException
        }
    }

    switch (userChoice) {
        case 'Run':
            echo "⚙️ Running ${description}..."
            body()
            break
        case 'Skip':
            if (markAsUnstableOnSkip) {
                echo "⏭ Skipping ${description}, marking build is as unstable."
                currentBuild.result = "UNSTABLE"
            } else {
                echo "⏭ Skipping ${description}, assuming build is ok."
            }
            break
    }
}