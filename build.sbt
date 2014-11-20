lazy val messages = project

lazy val blacklist = project.dependsOn(messages)

lazy val gatewaybank = project.dependsOn(messages)

lazy val supervisor = project.dependsOn(messages)

lazy val seed = project.dependsOn(messages)
