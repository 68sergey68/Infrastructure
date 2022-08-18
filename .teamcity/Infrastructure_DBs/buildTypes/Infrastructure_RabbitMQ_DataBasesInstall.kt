package Infrastructure_DBs.buildTypes

import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.script

object Infrastructure_RabbitMQ_DataBasesInstall : BuildType({
    name = "DataBases Install"

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
            name = "Unnstall db"
            scriptContent = "helm uninstall databases --namespace db"
        }
        script {
            name = "Install DB"
            workingDir = "app/db"
            scriptContent = """
                helm upgrade --install -f ./values.yaml databases . \
                --namespace=sock-shop
            """.trimIndent()
        }
        script {
            name = "Delete kuber key"
            enabled = false
            scriptContent = "rm -rf ~/.kube"
        }
    }
})
