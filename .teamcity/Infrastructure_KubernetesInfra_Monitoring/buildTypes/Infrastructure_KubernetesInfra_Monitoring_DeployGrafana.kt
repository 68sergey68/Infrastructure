package Infrastructure_KubernetesInfra_Monitoring.buildTypes

import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.script

object Infrastructure_KubernetesInfra_Monitoring_DeployGrafana : BuildType({
    name = "Deploy Grafana"

    params {
        param("cluster.config", "%infra.secrets.cluster.config%")
    }

    vcs {
        root(DslContext.settingsRoot, """+:k8s\monitoring => monitoring""")
    }

    steps {
        script {
            name = "Deploy Grafana"
            workingDir = "monitoring/grafana"
            scriptContent = """
                #!/bin/bash
                rm -rf ~/.kube
                mkdir ~/.kube
                echo "%cluster.config%" > ~/.kube/config
                
                chmod +x tools/normalization.sh
                ./tools/normalization.sh
                
                helm upgrade grafana . --install -f values.Infra.yaml -n monitoring
                
                kubectl apply -f datasources/ -n monitoring
            """.trimIndent()
        }
    }
})
