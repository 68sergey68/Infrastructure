package Infrastructure_DBs.buildTypes

import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.script

object Infrastructure_RabbitMQ_RabbitMQInstall : BuildType({
    name = "RabbitMQ Install"

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
            name = "Install RabbitMQ"
            workingDir = "app/rabbitmq"
            scriptContent = """
                helm upgrade --install -f ./values.yaml rabbitmq . \
                --namespace rabbitmq
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
