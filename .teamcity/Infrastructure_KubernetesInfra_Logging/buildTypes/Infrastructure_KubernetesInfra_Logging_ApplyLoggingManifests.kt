package Infrastructure_KubernetesInfra_Logging.buildTypes

import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.script

object Infrastructure_KubernetesInfra_Logging_ApplyLoggingManifests : BuildType({
    name = "Install EFK"

    params {
        param("cluster.config", "%infra.secrets.cluster.config%")
    }

    vcs {
        root(DslContext.settingsRoot, """+:k8s\logging => logging""")
    }

    steps {
        script {
            name = "Elasticsearch"
            enabled = false
            workingDir = "logging/elasticsearch"
            scriptContent = """
                #!/bin/bash
                rm -rf ~/.kube
                mkdir ~/.kube
                echo "%cluster.config%" > ~/.kube/config
                
                
                helm upgrade elasticsearch . --install -f values.Infra.yaml -n logging
            """.trimIndent()
        }
        script {
            name = "Kibana"
            enabled = false
            workingDir = "logging/kibana"
            scriptContent = "helm upgrade kibana . --install -f values.Infra.yaml -n logging"
        }
        script {
            name = "Fluent-bit"
            workingDir = "logging/fluent-bit"
            scriptContent = "helm upgrade fluent-bit . --install -f values.Infra.yaml -n logging"
        }
    }
})
