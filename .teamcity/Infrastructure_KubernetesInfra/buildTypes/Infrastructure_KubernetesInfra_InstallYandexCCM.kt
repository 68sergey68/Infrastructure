package Infrastructure_KubernetesInfra.buildTypes

import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.script

object Infrastructure_KubernetesInfra_InstallYandexCCM : BuildType({
    name = "Install YandexCCM"

    params {
        param("cluster.config", "%infra.secrets.cluster.config%")
        password("ansible.vault.password", "zxx4f38b8056055174e775d03cbe80d301b", readOnly = true)
    }

    vcs {
        root(DslContext.settingsRoot, """+:k8s\manifests\yandex-ccm => yandex-ccm""")
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
            name = "Create CCM secret"
            workingDir = "yandex-ccm"
            scriptContent = """
                #!/bin/bash
                
                echo "%ansible.vault.password%" | tr -d '\r' > vault-password-file.txt
                ansible-vault view yandex-cloud-controller-manager-secret.yaml --vault-password-file vault-password-file.txt > secret.yaml
                kubectl apply -f secret.yaml
            """.trimIndent()
        }
        script {
            name = "Install CCM with RBAC"
            workingDir = "yandex-ccm"
            scriptContent = """
                #!/bin/bash
                kubectl apply -f yandex-cloud-controller-manager-rbac.yaml
                kubectl apply -f yandex-cloud-controller-manager.yaml
            """.trimIndent()
        }
    }
})
