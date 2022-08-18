package Infrastructure_KubernetesInfra.buildTypes

import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.script

object Infrastructure_KubernetesInfra_IngressNginxInstall_2 : BuildType({
    name = "Ingress-nginx Install"

    params {
        param("cluster.config", "%infra.secrets.cluster.config%")
    }

    vcs {
        root(DslContext.settingsRoot, "-:.", """+:k8s\helm-charts\ingress-nginx => ingress-nginx""")
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
            name = "Install Ingress-nginx"
            workingDir = "ingress-nginx"
            scriptContent = """
                helm upgrade --install -f ./values.Infra.yaml ingress-nginx ingress-nginx \
                --repo https://kubernetes.github.io/ingress-nginx \
                --namespace ingress-nginx --create-namespace
            """.trimIndent()
        }
        script {
            name = "Unnstall Ingress-nginx"
            enabled = false
            workingDir = "ingress-nginx"
            scriptContent = "helm uninstall ingress-nginx --namespace ingress-nginx"
        }
    }
})
