import DockerKeys._
import sbtdocker.{ Dockerfile, ImageName}
import com.typesafe.sbt.packager.Keys._

name := "poc_cluster_akka_docker_seed"

maintainer in Docker := "Xebia France <poc@xebia.fr>"

packageArchetype.java_server

sbtdocker.Plugin.dockerSettings

mappings in Universal += baseDirectory.value / "docker" / "start" -> "bin/start"

docker <<= docker.dependsOn(com.typesafe.sbt.packager.universal.Keys.stage.in(Compile))

// Define a Dockerfile
dockerfile in docker <<= (name, stagingDirectory in Universal) map {
  case (appName, stageDir) =>
    val workingDir = s"/opt/${appName}"
    new Dockerfile {
      // Use a base image that contain Java
      from("relateiq/oracle-java8")
      maintainer("Xebia France <poc@xebia.fr>")
      expose(1600)
      add(stageDir, workingDir)
      run("chmod",  "+x",  s"/opt/${appName}/bin/${appName}")
      run("chmod",  "+x",  s"/opt/${appName}/bin/start")
      workDir(workingDir)
      entryPointShell(s"bin/start", appName, "$@")
    }
}


imageName in docker := {
  ImageName(
    namespace = Some("xebia.fr"),
    repository = name.value
    //,tag = Some("v" + version.value))
  )
}

libraryDependencies += "com.typesafe.akka" %% "akka-persistence-experimental" % "2.3.6"

libraryDependencies += "com.typesafe.akka" %% "akka-http-core-experimental" % "0.7"

libraryDependencies += "com.typesafe.akka" %% "akka-cluster" % "2.3.6"

libraryDependencies += "com.typesafe.akka" %% "akka-stream-experimental" % "0.7"

resolvers += "krasserm at bintray" at "http://dl.bintray.com/krasserm/maven"

libraryDependencies += "com.github.krasserm" %% "akka-persistence-kafka" % "0.3.2"

libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.11.0"

