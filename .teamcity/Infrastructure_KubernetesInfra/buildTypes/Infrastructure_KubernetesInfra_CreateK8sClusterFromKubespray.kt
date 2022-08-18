package Infrastructure_KubernetesInfra.buildTypes

import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.script

object Infrastructure_KubernetesInfra_CreateK8sClusterFromKubespray : BuildType({
    name = "Create k8s-cluster from Kubespray"

    params {
        password("infra.secrets.sshkey", "zxx5a5969a5d4fc034eb74d8410758919c746b8242696b83740bd5830bcb318e3f94b156a98e56a1b2219e379db6108b4face6da1c5c64eb6f8597a73726d80c6d78aa600dcfd2022888067805eea2877a739963fbf2975f750b479c500991577d03b887cf51ed2d8d3c1813964b6e624f02c9f7b2860ee78c030272df7d7cf7f06b5adc715dfafb8e317b4a5bc7ce5376d6b7ff99a0e3b45de85a68496be459dc38bb64ea368fac4d802893fa02441fef067086bc2c01627ddc20aee5d6d7166bcee66e4ae95c11def4b67104a16453e289fccbece0e63fe17f7cc31240e582fb7c2f0d52d1ec264ec33a39a0a8f453eebc26b4bc53b9d95f78c1c1d210da0ef544360526e0a95bc8caa97eaf594d7292ece969531020031b323afc2f71f5bee9db26d6df9a32617a7f9e24caba1eb1377d3f2a2c575bea8993ead66d841c74208bdb925d876916b7f11d895e1db3442f2ef445510074957607315bed29899a67042f18013d64bbd1d55f8c3faf81ee46a9376a548d8f237a97749c3a88af4f8753634c3031eb28e15c35c5b23d8263078749255c9126906a57d2c179795785f3c8d1f48f4b4855d23d871f328ebf724ce55116244edff355c7c5c46771aa1cbba72a7d6369d2ec67dd6d9c4a22dea3e13fa6b3c745f29658dcd12eea6042c8921845be250c030a1dd84402a3c4d912237dc23b21937994de2347067ff63dd8e860f553f02d0fd53741996b751fd564e4297a267e25a6d10753e697fcd0e42d92243ca4a2283e6963ae517ddeefdd463ec8af5476ea6352b8c19c84a5ddc4a6cf84049a009b0172969370b8d748218f0fabc6d2c5140a82d4e43fcb27f8fb72846eb70478ca4760953aff19f595ced5bad3dc2ddf7f045ddf3f887eac0d7e9e00228e20b079b788b9e28383e4c545f7621b6bfb68c5ecfb91e32a3a3f7a73b4dd41eec924c87d4338eb43017784db4452dd5a54708dcf940a7ba4d50669367819f1ec4d352b319a55ab2a0a715b5ac27ff5946c0edcf2d154aa83b32bd7de64bc191b293fa3d159c20d78c8d0a2d7ab66a")
        param("ansible.cfg", """
            [defaults]
            library = ./modules
            module_utils = ./module_utils
        """.trimIndent())
        password("infra.secrets.yc_token", "zxxd820d5a6d158a41779d1ccdfc4def2384cca4d8b6b1fcecb1961d384cadc84a5af534f65526d981c")
    }

    vcs {
        root(DslContext.settingsRoot, "-:.", """+:k8s\kubespray-2.19.0 => kubespray""")

        cleanCheckout = true
    }

    steps {
        script {
            name = "Prepare virtual env for ansible (pip3)"
            workingDir = "kubespray"
            scriptContent = """
                #!/bin/bash
                virtualenv --python=python3 venv
                source venv/bin/activate
                pip3 install -r ./requirements-2.12.txt
            """.trimIndent()
        }
        script {
            name = "Create k8s-cluster"
            workingDir = "kubespray"
            scriptContent = """
                #!/bin/bash
                source ./venv/bin/activate
                export ANSIBLE_HOST_KEY_CHECKING=False
                ansible-playbook -b -become-method=sudo -become-user=root -u ubuntu -i inventory/hosts.yml cluster.yml -e kube_version="v1.22.12"
            """.trimIndent()
        }
    }
})
