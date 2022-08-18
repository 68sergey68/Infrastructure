package Infrastructure_KubernetesInfra.buildTypes

import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.script

object Infrastructure_KubernetesInfra_DashboardInstall : BuildType({
    name = "Dashboard Install"

    params {
        param("cluster.config", "%infra.secrets.cluster.config%")
    }

    vcs {
        root(DslContext.settingsRoot, "-:.", """+:k8s\helm-charts\kubernetes-dashboard => kubernetes-dashboard""")
    }

    steps {
        script {
            name = "Prepare kuber config"
            scriptContent = """
                #!/bin/bash
                rm -rf ~/.kube
                mkdir ~/.kube
                echo "%cluster.config%" > ~/.kube/config
            """.trimIndent()
        }
        script {
            name = "Install kubernetes-dashboard"
            workingDir = "kubernetes-dashboard"
            scriptContent = """
                helm upgrade --install -f ./values.Infra.yaml kubernetes-dashboard . \
                --namespace kubernetes-dashboard --create-namespace
            """.trimIndent()
        }
        script {
            name = "Unnstall kubernetes-dashboard"
            enabled = false
            workingDir = "kubernetes-dashboard"
            scriptContent = "helm uninstall kubernetes-dashboard --namespace ingress-nginx"
        }
    }
})
