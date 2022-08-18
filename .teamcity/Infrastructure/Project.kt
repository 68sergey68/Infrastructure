package Infrastructure

import Infrastructure.buildTypes.*
import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.Project

object Project : Project({
    id("Infrastructure")
    name = "Infrastructure"

    buildType(Infrastructure_KubernetesInfra_InstallHelmAndKubectlOnAgent)

    subProject(Infrastructure_KubernetesInfra.Project)
    subProject(Infrastructure_App.Project)
    subProject(Infrastructure_DBs.Project)
    subProject(Infrastructure_Deckhouse.Project)
})
