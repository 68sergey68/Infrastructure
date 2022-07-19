import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.ExecBuildStep
import jetbrains.buildServer.configs.kotlin.buildSteps.exec
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.projectFeatures.buildReportTab

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2022.04"

project {
    description = "Contains all other projects"

    params {
        password("infra.secrets.token", "zxx9b080e09c943c1b506e7423b448db17c0ee8db49993f5e47a26437ae12ccd5ca388b399210ef361e775d03cbe80d301b")
        param("infra.secrets.login", "teamcity")
    }

    features {
        buildReportTab {
            id = "PROJECT_EXT_1"
            title = "Code Coverage"
            startPage = "coverage.zip!index.html"
        }
    }

    cleanup {
        baseRule {
            preventDependencyCleanup = false
        }
    }

    subProject(Infrastructure)
}


object Infrastructure : Project({
    name = "Infrastructure"

    subProject(Infrastructure_Deckhouse)
})


object Infrastructure_Deckhouse : Project({
    name = "Deckhouse"
    description = "Развертывание кластера k8s средствами Deckhouse"

    buildType(Infrastructure_Deckhouse_ClusterInstall)
})

object Infrastructure_Deckhouse_ClusterInstall : BuildType({
    name = "ClusterInstall"
    description = "Развертывание кластера k8s"

    params {
        param("infra.secrets.deckhouse.resources", """
            apiVersion: deckhouse.io/v1
            kind: NodeGroup
            metadata:
              name: worker
            spec:
              cloudInstances:
                classReference:
                  kind: YandexInstanceClass
                  name: worker
                maxPerZone: 1
                minPerZone: 1
                # возможно, захотите изменить
                zones:
                - ru-central1-a
                - ru-central1-b
              disruptions:
                approvalMode: Automatic
              nodeType: CloudEphemeral
            ---
            apiVersion: deckhouse.io/v1
            kind: YandexInstanceClass
            metadata:
              name: worker
            spec:
              # возможно, захотите изменить
              cores: 4
              # возможно, захотите изменить
              memory: 8192
              # возможно, захотите изменить
              diskSizeGB: 30
            ---
            apiVersion: deckhouse.io/v1
            kind: IngressNginxController
            metadata:
              name: nginx
            spec:
              ingressClass: nginx
              inlet: LoadBalancer
              # описывает, на каких узлах будет находиться компонент. Лейбл node.deckhouse.io/group: <NODE_GROUP_NAME> устанавливается автоматически.
              nodeSelector:
                node.deckhouse.io/group: worker
            ---
            apiVersion: deckhouse.io/v1
            kind: ClusterAuthorizationRule
            metadata:
              name: admin
            spec:
              # список учётных записей Kubernetes RBAC
              subjects:
              - kind: User
                name: admin@nip.io
              # предустановленный шаблон уровня доступа
              accessLevel: SuperAdmin
              # разрешить пользователю делать kubectl port-forward
              portForwarding: true
            ---
            apiVersion: deckhouse.io/v1
            kind: User
            metadata:
              name: admin
            spec:
              email: admin@nip.io
              # это хэш пароля 02chw1yn44, сгенерированного сейчас
              # сгенерируйте свой или используйте этот, но только для тестирования
              # echo "02chw1yn44" | htpasswd -BinC 10 "" | cut -d: -f2
              # возможно, захотите изменить
              password: '${'$'}2a${'$'}10${'$'}4uppK0CaJ.4.PK7peTedu.xEF.ALB9fAmhDoSRjdrojTkbA9Ii6RG'
        """.trimIndent())
    }

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        script {
            name = "Set Deckhouse settings"
            scriptContent = """
                echo "%infra.secrets.deckhouse.config%" | tr -d '\r' > config.yml
                echo "%infra.secrets.deckhouse.resources%" | tr -d '\r' > resources.yml
            """.trimIndent()
        }
        exec {
            name = "Run Deckhouse with settings"
            path = "dhctl bootstrap --ssh-user=ubuntu --ssh-agent-private-keys=/tmp/.ssh/id_rsa --config=/config.yml --resources=/resources.yml"
            dockerImagePlatform = ExecBuildStep.ImagePlatform.Linux
            dockerPull = true
            dockerImage = """docker run --pull=always -it -v "${'$'}PWD/config.yml:/config.yml" -v "${'$'}HOME/.ssh/:/tmp/.ssh/" -v "${'$'}PWD/resources.yml:/resources.yml" -v "${'$'}PWD/dhctl-tmp:/tmp/dhctl" registry.deckhouse.io/deckhouse/ce/install :stable bash"""
        }
        script {
            name = "Remove setting after deploy"
            scriptContent = "rm -rf resources.yml config.yml"
        }
    }
})
