package Infrastructure_KubernetesInfra

import Infrastructure_KubernetesInfra.buildTypes.*
import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.Project

object Project : Project({
    id("Infrastructure_KubernetesInfra")
    name = "Kubernetes-Infra"

    buildType(Infrastructure_KubernetesInfra_CreateVm)
    buildType(Infrastructure_KubernetesInfra_DashboardInstall)
    buildType(Infrastructure_KubernetesInfra_CreateK8sClusterFromKubespray)
    buildType(Infrastructure_KubernetesInfra_IngressNginxInstall_2)
    buildType(Infrastructure_KubernetesInfra_InstallYandexCCM)
    buildType(Infrastructure_KubernetesInfra_CreateNamespaces)
    buildTypesOrder = arrayListOf(Infrastructure_KubernetesInfra_CreateVm, Infrastructure_KubernetesInfra_CreateK8sClusterFromKubespray, Infrastructure_KubernetesInfra_CreateNamespaces, Infrastructure_KubernetesInfra_InstallYandexCCM, Infrastructure_KubernetesInfra_IngressNginxInstall_2, Infrastructure_KubernetesInfra_DashboardInstall)

    subProject(Infrastructure_KubernetesInfra_Monitoring.Project)
    subProject(Infrastructure_KubernetesInfra_Logging.Project)
})
