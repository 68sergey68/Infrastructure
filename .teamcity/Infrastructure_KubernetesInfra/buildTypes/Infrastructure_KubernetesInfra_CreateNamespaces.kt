package Infrastructure_KubernetesInfra.buildTypes

import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.script

object Infrastructure_KubernetesInfra_CreateNamespaces : BuildType({
    name = "Create namespaces"

    params {
        param("cluster.config", "%infra.secrets.cluster.config%")
    }

    vcs {
        root(DslContext.settingsRoot, "-:.", """+:k8s\manifests => manifests""")
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
            name = "Apply manifests - namespaces"
            scriptContent = "kubectl apply -f ./manifests/ns"
        }
        script {
            name = "Delete kuber key"
            scriptContent = "rm -rf ~/.kube"
        }
    }
})
