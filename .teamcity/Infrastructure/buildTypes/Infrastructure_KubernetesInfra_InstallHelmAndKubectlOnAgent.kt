package Infrastructure.buildTypes

import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.script

object Infrastructure_KubernetesInfra_InstallHelmAndKubectlOnAgent : BuildType({
    name = "Install Helm and Kubectl on agent"

    steps {
        script {
            name = "Install Helm v.3.9.2"
            enabled = false
            scriptContent = """
                wget https://get.helm.sh/helm-v3.9.2-linux-amd64.tar.gz \
                && tar xf helm-v3.9.2-linux-amd64.tar.gz \
                && cp linux-amd64/helm /bin \
                && chmod +x /bin/helm
            """.trimIndent()
        }
        script {
            name = "Install Kubectl"
            scriptContent = """
                #!/bin/bash
                curl -LO https://storage.googleapis.com/kubernetes-release/release/`curl -s https://storage.googleapis.com/kubernetes-release/release/stable.txt`/bin/linux/amd64/kubectl
                chmod +x ./kubectl
                mv ./kubectl /usr/local/bin/kubectl
                kubectl version --client
            """.trimIndent()
        }
    }
})
