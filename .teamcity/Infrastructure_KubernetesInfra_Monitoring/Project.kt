package Infrastructure_KubernetesInfra_Monitoring

import Infrastructure_KubernetesInfra_Monitoring.buildTypes.*
import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.Project

object Project : Project({
    id("Infrastructure_KubernetesInfra_Monitoring")
    name = "Monitoring"

    buildType(Infrastructure_KubernetesInfra_Monitoring_DeployGrafana)
    buildType(Infrastructure_KubernetesInfra_Monitoring_VictoriaMetricsComponents)
})
