package Infrastructure_App.buildTypes

import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.script

object Infrastructure_App_AppInstall : BuildType({
    name = "APP Install"

    params {
        param("cluster.config", "%infra.secrets.cluster.config%")
    }

    vcs {
        root(DslContext.settingsRoot, "-:.", "+:app => app")
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
            name = "Install APP"
            workingDir = "app/sock-shop"
            scriptContent = """
                helm upgrade --install -f ./values.yaml sock-shop . \
                --namespace sock-shop
            """.trimIndent()
        }
        script {
            name = "Unnstall Ingress-nginx"
            enabled = false
            workingDir = "ingress-nginx"
            scriptContent = "helm uninstall ingress-nginx --namespace ingress-nginx"
        }
        script {
            name = "Delete kuber key"
            enabled = false
            scriptContent = "rm -rf ~/.kube"
        }
    }
})
