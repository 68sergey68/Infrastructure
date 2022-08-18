package Infrastructure_KubernetesInfra_Monitoring.buildTypes

import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.script

object Infrastructure_KubernetesInfra_Monitoring_VictoriaMetricsComponents : BuildType({
    name = "Victoria Metrics Components"

    params {
        param("cluster.config", "%infra.secrets.cluster.config%")
    }

    vcs {
        root(DslContext.settingsRoot, """+:k8s\monitoring => monitoring""")
    }

    steps {
        script {
            name = "Deploy Operator"
            enabled = false
            workingDir = "monitoring/vm/charts/victoria-metrics-operator/"
            scriptContent = """
                #!/bin/bash
                rm -rf ~/.kube
                mkdir ~/.kube
                echo "%cluster.config%" > ~/.kube/config
                
                helm upgrade vm-operator . --install -n monitoring
            """.trimIndent()
        }
        script {
            name = "Deploy node-exporter"
            enabled = false
            workingDir = "monitoring/prometheus-node-exporter"
            scriptContent = "helm upgrade node-exporter . --install -f values.Infra.yaml -n monitoring"
        }
        script {
            name = "Deploy VMCluster"
            enabled = false
            workingDir = "monitoring/vm/cluster/"
            scriptContent = "kubectl -n monitoring apply -f ."
        }
        script {
            name = "Deploy VMAgent"
            enabled = false
            workingDir = "monitoring/vm/charts/victoria-metrics-agent/"
            scriptContent = "helm upgrade vm-agent . --install -f values.Infra.yaml -n monitoring"
        }
        script {
            name = "Deploy Alertmanager and VMAlert"
            workingDir = "monitoring/vm/charts/victoria-metrics-alert"
            scriptContent = "helm upgrade alertmanager . --install -f values.Infra.yaml -n monitoring"
        }
    }
})
