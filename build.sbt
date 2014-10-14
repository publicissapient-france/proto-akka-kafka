lazy val messages = project

lazy val seed = project.dependsOn(messages)

lazy val tokeniser = project.dependsOn(messages)

lazy val anonymiser = project.dependsOn(messages)
