package Infrastructure_KubernetesInfra_Logging

import Infrastructure_KubernetesInfra_Logging.buildTypes.*
import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.Project

object Project : Project({
    id("Infrastructure_KubernetesInfra_Logging")
    name = "Logging"

    buildType(Infrastructure_KubernetesInfra_Logging_ApplyLoggingManifests)
})
