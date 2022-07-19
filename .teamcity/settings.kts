import jetbrains.buildServer.configs.kotlin.*
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
        password("infra.secrets.deckhouse.resources", "zxx58e1c2f325874b23a68bfadcc267cc1abba1fc117eebc5658d2e273b09e30b604a28a7d8b9752e9513461c4bbc3898b9f4ad3b58496231962b9677e3c020b05f9f440ff3052f10bcf65c04d9cc030121cac213c82e6fbf052c294f082b58dfb04087ff4a3c7ed442cc455fc42629e0b7e89ea7f605e6384ae644deb822def1721524a9ce587759195e5f8bfb06cca70982ac2441ae2e957a09ce8dbe88d81c3645a08bc41596cc198d2800499b3141d03fd7d431075fe33f7bf19fbc82291bb98e8acdb3387bcd87d423bd3f22a01b6b97f15cf412433d1a3df2c7f55871696f0dc62ced01da8d5b9e31a65ad28ecc5c26af3c9d3e165eb235bc1e4281ea42440695b704b7ac810f51c17d768c44490c6864db094a2a68ac44f9c58ddde33e3d370482afd69097a448f343501bcb780366d9a95a8f9db7f51ed9549905bf72987fc4cf7464bbc1dd7cc8307fa9f2f3c90274135649a5d78071bd576fd006289d606a581f016b63242f867f72f6c2d5e5a820b01f220da4a83fd779b8c42ecf9d56b76854ab895048e317547964cf122d787867c42225f95b1f28e6e921d7ded5f71a773a074afb9b815e7d271a0a448a5c544677381839fdc1d43a5e8cdba8d3f11286b8ccae70e1d4dd7e7e0e6ef2f7d15dd15f27aa4da3156771cd293cc512a7b182eb6f6d17a130a9db1bed926c676f72f3d6e0f1eec205a2e0536d009b8b75a5779eb6cd0cbddb605c675dbabe06d6e00bb0cc4319fed50bacb915f99fe4ab5954c2199f7222c0cd8df5b81ab71b62d581f0b2c7f56c18d9aedbeaee07ef33a5743ef58f4f22fc87aa41fff7bcad2633665ec238690b0e2e86d10b6d86479f912b1f661fca648e47d33fbfe1a7fc0558b6e3ff44e0dea38d8337ab3e8cf0dacb1b5702d32c36e3dd4d30630dc4382ecb1fa07f4a4af6e3f7b75e064c7440da6704e48d98ee5a2fbed80c1e8eb576aed37c080a91ea816abf4552ac0488d76f205a4caab7765247877abdf1d70e7512c1733e3b9c4eadd629ab8c3ece7508da321756a47e045df4ad3b5849623196d1b60238d9e915f6ad7b915c900a61cf2d2dd7338f642d7ed036a5691e48acdcef3c3ebefab995ed9f132d2c023de9fd61ed04c1bf9419394073bf7a1a871d252c5cf21aff61cd8c483f553e3612869047a83e208db1e57448f5c0b7872519de2c69d3ccfedf09c568841198fcc382a09b94be7d42053d20ffa686388289f072627f1b7f38a0cfc505692f6d7e94a6e8a16a2b4522b8559da9053b319a95741a23cf3c318e13a259fcf8b5960c6c40332145e5a872cb17f64db6427f8ce744c6b6f72e0f66370a82c68158dc1c574b026827729d4a8e54519c8ce600ef7ddd4c7fe52471630e5c4c1bb61144ab77498795a98bfc31bb41d635e06efd73b9c8af18f7b645ac26351a01df2387e9f44944143bead1689f2d126e39898ac5a6cc708d21cc5c4e41bc607bb5c7005f3d5ff56abf4552ac0488d797d8ffec6e284dd8141ed3469580f668b85a0a7fb08e83f5901f798dd1e035c3467a12ed6356bf73f391162839d930ce223df4d9decfa5d2b603f11d32d647eb1f1eb78bb89563fb91d21a874808bb5e0320444ff0aed4eeb0a4dbafc2e9a2769d9d538e136826461e59c5d8aca4023f9da3d9d3020f37d1c51910ce1c50dea02a8305b9cfec45038ecd9bfbc21e6b7488dd8a2f051e1111024be937d2e23067a5e92858b0b0ff400989aa4da952c2f24c290c9708bc5bd7cfac5dde5d528d6b50db39972a0cdcf3c30e714c2755f6359d9d538e13682646b89dc5a503e6d5c0946f7ee6c9e83e3e07844e6b23aad2a2b1f4d190e59aca2e551b743ac91f68bc48919401f2755640e5a710405f13ef9f8b9b56001dca17f57efc0976ba72744f301be3c669192290d4dec1897f3427358186be164f23c422fb8bf2a5b738fa3ac39fa0c595afcc01a64a7f31b2e8d8f1e2fdfdeb7768deeb76269b358ebf17a68b8f3dbac3f6b11204f1ced774cb36f99d3d09b2ecef3b2f65803eb17ac3f64153044ff14c32b291cbdd07084d14d177a07b49c0c5db8ec1863aa983db7afd1408d9536b4116c216c97d6f161204ed12fd33769c92df66d356b76854ab895048e317547964cf122d787867c42225f95b1f28e6e921d7ded52a9b3a9a0f56dc68f61c8d0bd18ef2ffcb38ff735be53e9b3787c84d5075e68a8a35f06c1ffcab927bd850385bb9443e0ed0aa91c819d6cd56e878276cdb7aac6496f18197e0c82ba6e2ad4cebcf817c8216d404392ef5ae3b6ac67980e7e28c800c563c0e980f3a66652aa9c7dcda38ea6b438b58838f5c4ec6b7a323473b1dde95720c127de45f51f32689603ee6db6869071ea22998e53dee7d38ca5d81cccc31e47c8a34edc82f7bfa4ef57c80619d952be6ca40ea22cadaa2e9873cd0049793a0d9f94113557f5fe600ec605996f802da39c8443f8b5fbf0988dc61805f58f55b2b3b63e478e56eaecd0e20cc05df2e40f096dfdbd48b235e889d891a89444ecde487def018c1d06610c64ec21ee32f5380e397d2b35942ec0ec46623fde1b38fa63ae7a1011aa35c28172e50c221a28f71056cbeef366c0dba73124e5a366f16838b8c75229f4cb40adee2adad9f912b1f661fca648e47d33fbfe1a7fc0558b6e3ff44e0dea38d8337ab3e8cf0dacb1b5702d32c36e3dd4d30630dc4382ecb1fa07f4a4af6192f478978efc1c5e54a2190c4ccdbc7f5e748a0ed9601a1dd60de5918d3740eb3d89224895212668723de6ddabb6914817d12aa1f8f7cb7f1971102f42f47b41bf39a22c04f1ff5775d03cbe80d301b")
        password("infra.ssh.private_key", "zxx0ead0f89b53aab8c2573f035d30b445499cf9558f5bfafbb7f77ff200fb3a7dbc80fd7543ad61368454a10e77b00a1b100ae866fe688eb70bbe2b9ebbd412d73cef014834eeea6f015ffa75ac6bad28738f5bcce95b60db5edb42fbadb9ab45302387b3332caa789b21c5f6ad8f040391493c72bcbad3094e0e2182c050c0fbbedb913d1ca237c51afb1af3db8a5c967536c5aa0346162be04007d6a40d0fb5fbc96cbbc15a5aa685eb76825077c5debe224a75a62a3ca0ae939688d06906d78ce3ce0cd8abb019c3bf17895cdf5e427946743bc726379c96c53346103165122bf613834a81805e86a8d2c13aa1d6ed6ed0c126be441425927392ba2b09da9bb8558a40afa3cdf62a70669f91d0c4b19741111de314a9ddbd4c7416055b13889b989b928730e64b75f94b5d62f168fa47751864a0814d3b30297c3f5e95bf169d973fcd322a2936d149f876714b5e7cf2f41c134cbffd386b64cbf284618aa9add21eac48914cde39b62baf3d7cba67ba6171c97005492dea8bf5973a9c5441039c0451e25b1707fc49f630aa95690dfbc14ef9f16d47164059bf19594ba795f73fa051023a475faeb6a6a5253864b676d8f8f6b20c136cdf801229e9c40852aad7453c123334d5872474c17a0429ccbf52dee7d801b668cf4979120c8a52c156650d521f1e620b26afe85dc70b139905209b15c4c701b1489d548247ad1e3563f281b4edeebc499daddb5e26ddc8915a977a35d28459602825a6ce96aa86502bd2dfb08d15ea67e90272812348a91271b73daf636c7f94d8612815f02344dbd54ef94ff2474aff510c0b46aac2b2b1be6807dc26bdb7af05e3d649aea4e8d65035a1852d6ddf35ade580521fefeba2f8f77cac819ba83f36a93902751d21ecdf0a7e106b6384a80411f1d237e06e03b43a7f33c4d04a0971790ff89a146991f47baaa85be1cba09796d3af5b405a51ef092c796529f4158c99b7a2d38a5278691d01fb0ab465bb59b19b234042e415892786696b1e2cd090d26a92b5664b8988be17db3c01eb87abb444e74a6905c5cede4581f159c045219a6ce73f1cb519813053884e1a3abf53879e7e63d49262d2a90367d50aa5ab3685aed7555a2a984510feba9bde25e190fbc5d5c8b09d837e13b0994694c28cf3ba447beefd9ac2f9749754da163ac50bddd747c2703028c172fa61c98e0252dbdf64317cb899e32bd24d746403c8434b66597027113a99702013a3b71b4cc4256cf12f7ad45f85e3f254e5fa7113c78dbcf73afe32fb6a1d13f8e86237c97c8ab699868a49323dc762712f4424bf59ad6fa988e07c1c2caaf70492a037e0f965ed3e51cf8edcdec53e005acdce0458291e88068894fc70fabe930f1ad5e5d178f23e1621667bdd45a951bbe8950818ef3b3b034bf5888a1505b290b247a964a9ffedd50e4e2528ea5b33141a0794f89a2196a6497c3a88cef3fe8d08116e592177a4f82782e5896598e41f256d5d8a4a16be0563f47be25968dd3bc1a8403834e71fd0207605a1c7b75a765585af49f42dcc77e69035c42ce97c2c3f87222a80fdcf204c21fae52aa5013502167c4b5383c4be06e7a8d61906280b54a0ae04bd67293bf45a180131266edb9857bf25652a2bbac4e042b051add3c790ddf2f0166d183c792edb831c12620856a5897a6606c77c4d9083bd2b0016c29a9751260c6bc566c663573f516cf0a24633a27dfe9338f19543256aa68d231b55b758f649914cb3b8f7b2012750a7e24cc75cf15d40440020f420e8264abc3c54290ac188b396f0fafd79f38c6f6d36ec86ca5e1e4283239c9d17512e691ee3d84d37320d60dbdfcbab56e1ebc12d079513ad4bcdacde6d0c1f3d64afc5d14343baadf88d8a2563b33a9e4ec0e377a310d2b1864c6e57bcbddadce5322b9322bf36be3c41006309e8f2fbc9c5b2d607d659e98944d298a34a6718149cb1b07d8161efeaa1755432ee47b77aac60473407036090716ecf5186c6f8dbcd671d892088c7e82bafa7092dc753f5c37578f040b1fb2d00a0ba721807a59a5e45748cefa95721d434361bb7d06b9082e9da8646f4fc7cb84441485ab96d0be51f92ccb027dfc3fad48273f5d1021ebf2102ea0ab2de717ee5a13ecb1b356bd896e239fd94ebc2472424eb6302577e4886d11478f02f7c11eb2e6fb65a8571d315b7bfba9dbc7bd1432c9e700429fe970f5e81570f947034fd2f12cb64df57ace7fea1486783a71dfc98c793975dcd06535dd0fba541634bb9a724ed20b3b3f5a15c9104ce1bd60d46d2b6fbb145cbcc44db79373864c0fd8a9a12c2c2338421108875b4975d52c981a4c6d5471ca13876957e4ee417a5c5df31df57cd1899fe4bcc5fd8625ad4df1f48230c03d2501d377907753269f3954c65ded3df2c31e6597b2318d2063283ff51ebcecd346264758c1c161c7c5fd55d25eb94c2019ecc0a1c5c301828600d2d8e859f6d985689c9fe0b796b72d789980c86c8480f8526eac691931c120471a4eb11270a63bdf0c3fa9cdf70c2b423a0f95012a1a7b78517bdbee9f885d89eb058678b858598db8ed91725b89a865be7f733f32b2841b1ede3237fdb4cf10f76dc451328c110b720433c74da071078eca4344a404f2fe84f775868aaaa1e30d168e91cc26087306b3639447aa62fd18b675fe484896973230f1281cfc7699f74942fe7a49a203798a35d3ed1ff6322a5b63efd9fe46c2e45a7632d44e179232c282d6ec214ce7d3c13ae90fba339964d04f5703d4e5acb792dde64b5985998c120f070c65b5b4b0b97a3ec0839b524c1a0bf6f6c53af45260a475595e68f73ee7e3df7a0da235e5ca211ac961f12c542ff1032c7a60424287f32d8beff8848d0b4cf4af4ce29c4b8573e17a95febeed2b8ce9db595bc3623bcab3ba819dd712097d2dfc92b35ffcb1599339220b7db5b79ace6866d3787162d3ce4ed5c413b3f03d31304d59136f634cf228494211ad4b1ece5920e863a3eda305a4bd146ca4ac5b027b50fe728144b9bbf5caa47b5e9bd44062a1316ac047a5082b46b3e9cd55b1ca701be35ee37007f3ec11d3df05c19c85437ff753497bcfeae1ae85b15423ca4c03eaf24aee2b65f362d4102470802121462b106014e093a8049fecd072e9d25042c7970708d3c58d8b0100dcbb2eb6cb0da17d0c273602d612a2a0b25e72585ede11f82a8292b46bbe0361d54d60e111b0c55909ab632f343de93681643ed5b904df23244ee43ca4e337ade8e82c1b765fc551ecd5e560382b167bf4f19c064dde40b3817cceb2283214d924e93bf4bc30d32ee751a89ecc7dbe063f8c3a66e19c4fab6eb5f69deb64c5c3e5a7f7e1975244f119cf8588de5054e187ef9baa688f659c7b2d9b8a1f2fc5a9375bb456808439cec9e729d5209023929be35784b3460c451373f6dbc629ab04895cf9e6c2e105d21a0241ad27549aa5fecb87db7c87709c1e47688f569477f3cabd0d9610c32cb98053a2df3e901570487439fab0ec2b88cb72561ab1fbab0b7abd305f31dc29451130107fcf946133142d46f01a8104a33feedf4cef6623e308d1b96d22b4f2fa14200c929999bd751c79b52d4242decb7a037daee16f6de18fe2d95c891d600beb20f049e4fd96901b49ccdc45cbf23aa54560d6931a3a9e6a425f7300fd332edbe943baefd90ec8e8")
        password("infra.secrets.deckhouse.config", "zxx8f554aee6638262e927319fdda3da443306403605e07d205a7dc753adf5e284ac5c84caf4f70fd294787ad703c18f6df46849c0df09216c47867cdcce999a30053bde0ef7bfde4e77a0ddd02882fa655adb74a444688c420894425414ec90bfa9793a0d9f94113557f5fe600ec6059969ea74ccecb1d6b594aefc6fba214e9c4967ffc7cebafa985fa60b06ec338ab75316bae1dee2e77733869c9627c4c7331d07ff46e1462b39a7966e61ffed9d8dab27755b42ee66b65f1bacf593c93b873f6408bc5fedf971821aba35ef75c4ba6b615869f8fba7696d94053a73d150af16507c3ee646b9dfc84ba372837d00fd9a136e9d1ce626abe1ad4bb713890ca80462c1a3e59264d9f1bef2d2a07f62c996fb9ac745dd65a42dbf43c56c19712a36c64643f9c783050ea3f4fef1487e5129f2d9b06360a21dd006c84cd401c69ba1ae6a73bbc2cad91a58a53f618981ce6112b1c13ce0b432a6a402696d570ce09d44611c4be72ade39fb069dc0835a698c5c84caf4f70fd297374cd52b0b6a814b47d7d53b5ebc3c0f26e3794e2a55072be10e3ea375acd3467f397ceb1afcbf1717ddc0e3c9f1ef3d7e1b68bee9cde95cf0a586c82b1265f0c5cf50fb611b2a9c669101e09d2ec6920caee554f5e49f44d9eba8f37c39f3cdaae5985ceb527ff6c89d63a476b6d6bc2866025ee2afb9bb93429e4526c7c482ba4f6a76b67ac5b1874bcceee7f2d94d9635aacf9ae8e004fb058490125c99e8700f073c09a7b4bf0ae94d104cc6c37c24a0698cceede9e322ed6f266e3a51693c84fcad9394288e4346a22caec4fe16351fc39677ed9d1c81f701dec69af4d024301b7245f76b8c8aec871aab8030f137e6398a630f3b13c3d42d588ed82b9c1b78366c402144251a40ec1c1ca63765c2d58ce086da44d88eca061118aa1cb169c41d7c7baa83de6d3dc083cba4c750f6396db0171b6c22d87b4f05acb055fa1148dead487d51ca16323b3668d4f38db950d2dacc9e383eb2538afc76c0532e6d821c1ed3396339a03d9a4fef57a7752a18f5e9a260dfb3a0d3ddf4e4e2055f34ad1ac47e5847f7867cdcce999a300cbb1c43c0a45e55f52e00a5f9b6b4f10d403ab9a47fdb0dc0e638418030e39d0b3c8c71fbc6c707a3dbeeed7132fd3b99819b76b3b2efaee2f640b86b979262d4244bcc8ebee53223bff96e43358d4c376b9480fb140247d285c6c4e5f001cf6f34ad1ac47e5847f7867cdcce999a300872e0ebc6db2525a955e5e2a8cfd040a3f19a58ed816f2d45fd932e8b1a1152eb9a8acb79969e6eda0b9ddc4789e036428862f174e73ebb8184e1350a4e2d4809ea74ccecb1d6b594aefc6fba214e9c4f2389aa97e383847a5e92858b0b0ff4056d76a459025a40ea38a9104376f56d0fe164d9c8e048c72361001bc169dfa697be61a1a174de7f7124bef2478ce59e77d63d4e16a7f07431792cbad38f6ef2d4ddeb1b0e7ed73a25115dfcd41be1aca7694af15b999c81e866a0c293c32fb228279b08c50a1c0662b994ba37bde906f538b920425c55e07374c032d1c7724f22171008508453485162a50ad89a4bb950b9ba0303dcfa0c33d9d28674a13baefc1beaa496476dd26dbd5fafb8b9f2e5a197c6521830977ae8aa4fa25d84edda53a2936f59801dfb439b4d18d84e87694622f97f8426a91d4cdedc47e63a3a072d8937e2200b87e454ee3567b024c8d943c7a6d8925f3cdc27d8046adf1e8fb0a901f798dd1e035c3467a12ed6356bf73f391162839d930ce3bc0e2cd5857e3d6ca295ca499aed32f2cb00d269110c38c6e76083e83c616c0edb58fbb77e2b9070b9ba0303dcfa0c3810785728f08d88a09ea86faac1d8300945d5f92158a2d5cbb9f5d6aff176286562b78346cc0679d0afae335c23ab711b579881ee328da9fedb41691baa7c181d7a453235782f604011a487adb73425743f650ed1522d551503a7287166ea175a9603fe8d7479c2135bb38227cf867311c0d28dd0552446e4a5868c8c4e1e039ce66b24a60c17aa2feede4c64ff2a908123ad743cd583a5822a49efafc46513fb562710f8fcae2757559744e8e0d1b6b121f8cf1a21d94e044c319a458b1fb4a51e3f404000465dbdfed00b09ac1b4bf8d390d51ea22e42cb5711c4c05efa09fa1252c92fb7eb3ba9e5ec4ee27d873d8c574c9c720f5e3b8a6324deb0ce9754e2f3b6fbe6ea98f316a0f31ba8416ab050c5cf50fb611b2a904f1ced774cb36f94072e33cd52becaf3d76fe173cb1b24ed9fcf76dc9e5628c148d1439456eaf2f35357e5ec556fbc14e9ba342f7fad95311c06fc6affe511f7ee115f891f529c9c1cd5de8b0d0e56d888ce17be49913865f615f6407337244854ac3599ede80b5322ed6f266e3a51693c84fcad9394288d5fc627cdd087d828a689903571baa49e57db902081a886510a1263ec32c7224a5254e1d040606eefe36c4cd367b8ce92177db44b4a42cffb31ae0404b84ab4dd08b714fd9e5da51644badb4a6af401874755e4a110d1b56dc9875091744367ac7599a490ae4605d2236af1812bb520772377bcb3621460ec31ec60177d5cc4bfa3f76fd6f3ad107f00bb02fdb3a940caa13b74f994c2d8c4ad14c59c1f2a5526a2fd5933408d937a2c7c27f87717cefb4487c5c268712df46e35ac42cbc6f81bfb7441e8c2080bcbf60ae03674be815dfc33bf3075e3946d09316022c207c68044f1f15da4449871826663d454f68ffdb93cb99d5fc6041646424530958d0722441a04f4f01184dc8aec871aab8030f137e6398a630f3b1d5b75aeff076b21d29208be46a11ea1cc7baa21db3c52c3896ffa4428b92825688d7ae8f35f8916eaed37c080a91ea816abf4552ac0488d75dd44eb33e8301d1b92921b8996caf586971d2328483b61277eaadc3b317d3d45f6db31f2dd06da32b44523aafe5ab940f75c3440d4a34ee744b9c30e5c2dd5b59e8f40e493efc7a50a2fe7b8f47b38a98d5da9ac6e1a30f4f153122892fbf664015f4536255fc999b15f036b21c8f4881b0386528a27b4f93d034bcfdc2baff0c956a791d183c18ab4070b85986409e36a188f2c5ab6bd89fb8c34ea242d6eace27c104a40db2c70724125eba5fbf899c603dc7f47bb60f4334c55e170b8437e513d500c798332d36d3910d460e7acf81d2b06600ee780c8d318c1c6f2c2749d60283491b5efa0a2fce2515a94cafc8b579881ee328da9feb943c941f49767f77e80081742aee34ba930b3ccdc2288889eda2d566165eb1f11f6ffc297c9ff3e1e287f1f8bd3e23a02b23fe8a83d0ada79b3e1ec0f21590a58a53f618981ce64fe228191ea16e6e90876b2c322fcb8bfe23ceaa2b3cd642bbd64efc10deb9b6f4034df915c3af6f687493ad982a66f1572735c1f490aa767d3875a944cd37e7274f52b4c1dda41ea9d18895c739ebeb0e53f54c62313442944fca6a2d0fb2b96bddb53bf2c3e1372a4a2f0125a9b9b22bb8bcb858dc8a6a7273e89b869c715ada0128d490a7c6d5b5526c5091d2818bbbb733ca55ca319b3a0c4ffba845bbd8235aeabade1e286645a9dfefcfd1bf6bf7480588801559720490625241fe157083253c1d164d87334abdb4cd33ebddf936964c01474874cf0615d2d99b9e77b9ee2854506141d44c9ccd2452c7a5028bf87df3073939369ec4ef64ac9800bf536dbe40a0a751f7fab4abac6b2faa79b502d4bfdedfc6fe0b3bf2eaf788d3ac10a7845858ca337ec3a20becdef59d08a3cdcfbd770813a544524315f37474ffafffe21122b43846bec0ad3143c04540fe84dab1972269a6cfc739608c822605610e3dd018494f2cb074020cab1c33a6d4cb941e54bdc2014a900a6bb0683de37b23b61ea60530dc4b29ab2ee42b597abcc2241d02e34311b7b3e31ec4689774af4aa1a1ceccd62cf99745f9ff425d87fb89b97907fbe02dfee2ba60719c84535752f423d698db5eded4d0fd086ac62581ca3abc0a2077b876d2c29ba572616b344f1a21c2ecfb9393ae56bcf718cb4e6fbe1ad03e9326d28bf0b697fccb7b405041474d006a1cd3affca414fec6556d2663c9fb54bac4f810df197a7764a48af7bbbcc915c657b2f964fd64155d8415b3a176cb34831036a9cce306048308a5241da57bc3088dbc8c6ce79656d5b0465281f62c9258ddc4fe6d2e00acfbd5a0de62e7300d58d2babd260df8905d056bfff05244b3b5d2817b065b3a2ce5a631526706e22f6abf91923e411acc987221702980099f9ba7ee8e3f63d808014420bedb5c43002164720717e2b7a44d15531cc3f0e3dd71c14f5854182bbc1a3e578abf930ffb35f21781e8796b3c2f212acbc89d681d68e6edbe66153e80246ea231aa43bfa2f492f5c7dcfb2360cea69c890ead0f89b53aab8c90157c25fe55491356edbd0cc400615d7b4f3521eb35dde9f796937d0594934bc0ad3143c04540fe909a97c097d405481afc2b4c466942acd92171b84dfc0defff7f54cb08fa343efa6f63170cf52daab003b3cee711c0dc6af921ff733437eb40e4e1b56221624827fad83f9ed6d4aafae330b9916d120b38d15b83ea84e7e9db77216110da4c865f70db3ca70c79f664626abc5e9489bfcce541ab9d1ca01331ebc7f157954f675a8cbd7c4215dc327f99dbe552732790c8055edc4f950d41abff8a0bfa774a78873016c9cbf8cf92ab9f45f022c8a14870e3c4731dde8d11f47e12db4162011867e88c901be9ab687b3aaa0b882673a393853512ba31cb13269928039dadb7b5daa399e5d3bd265f84ef0685019062fd4206c6b7cabff15bcd68c3b1eba838cefe2d1a78ea9b3f9bb5e0e2cbfa1de401067c46cb499d5568667788218a4a8739f3e511df00c4a4c1d1c4f9abba7217aaa565fd7b6fb286ecc27dfa2f4283302d87a5dc32cbf5c7c01a8b94bb5989cea6918f5e87c2f5be0fc20d273e8edd7bfb08c3081460a1b6333149fad7f377da15fa0bfeb209f0d0f23924f587521e355673cdd638fd7764ec8d8853ccb020e1040c097e01788c08636751c0ac01febb49c9a4e86567a3c39eca1fb3d651498688c9f42caa613d014daba777225c5236d79cafb08b8e48c71e42650f328e313eb6522707e362aa7dc8033403b404bdc062c2a6024d453adfecb39c9b748883b6e86807662a8843d9012fabe0011e1bde897c2bbab644fbf3c56e3e15aa770576a966835ea3ac39e21d8f8115cccb78d374329f92c3f76509c59aa4b9bb92bdf3fb5ee6e5796d71a51b59c077bd5e8b0d8e845ce1cb5ff89d1570e13eda2a5ca70a0cedaf3e0d732de6c1e8bbccd59861b76b0a2ca279fe99014c214d04cca875053a95e08da5f1051a8770895b864ae57f38a9e9e7d353ebfc81d577b6b3bdd1f633ab5373364ed96e20c5aab514913d3a3dbc722aa73b93eec7aa7559360079ddf346b5c7e525faef38b97820f337dd2e38040c15e803b6636c9e3ad27a7234e8bb867cca2a3457d41d9ae1b6c931f1610b7221c818527c64cdfe9be3fbb249695919fd4e476c9a8e38dbe55584004113f96c2980d270a75b6e775f9d782117d04a6019fabdaf33fec32eb41df0f88b2086b55f267a31c710e6a49f40bf5385e85d7ba4c827092b3d7e4f86df12993e92aeba3ac02af0485ff2e0bf67eb25561ed336c5175887f3fc94c63264715ece33230b0ff24c4f55e79a729f230325008d09773944d36aa31dd99e16e6f8c2080b8ad0abdd8d8db675726fe4ed3b0c8cf072b44f095f7c139f596b291fe55f82c9f5cbb22a8e7c2d42c1e0f5c72b4e413e51c81e03db7933691433c08287e09629700d6ba070080e06bd5d045516b11de777f26e3bf533ef6c07c361dd8c7067bef58c3276022179a3844347e6de93bc1bdad76dc96b87e8f5e10334d64198a6595e609ae6b71379aa585bba7644b1ad2e1abf4afdc4623fccbbd9c2869dcacfb0507847c5b74c7054a3b8aab9a8744fa2636cf5cd79fc92b44d9d380b93c8ce1efc18e9fe265f9e6e38f2418ec622883a95e367477e55c3fc12c58bb28bcd8303fbe47a104f0caa2fe9c1e788a98be3d37fb37d534d224b7278f6fa57fa09a591e5e32db8d3030ac5e7cdaa56a70f234ac5bdd102034c0cbbb27ba0d98488286e7e0ea0c3075622e560aae853db671659d4fd9810e0539fcb48ca4c1fdc90803d07bea77c5d0a9a4b0dc21e8e0727915e9365d07ac213bc3625bd6c1abb9fa0b4f8ccf1b487ac5933bc48a69c3e835502b52cef2cd26b1a7b923d31b4106c1fe61b1f8b8b1f1682cf53fee0629857d21cce491324b83c2c90d8e4183c14e7d39974873b3329b30887fd4ca049d2d750e06e8b78322000af4bca5252ad9ece9a0a9d36036784d733765d6c5ae4b78ffafc637b8dc15d2b48e7b5211ef64e36d1a9247b5b15f23129db0c51534fb94c59226d20e2ddcb05bce653ab1674bfe35f0630cb9a53f3c218d749db44f80a59b9b40584478f7f37796547002a5701a45e8913a53f24636ad6c0eb8f88bc664813ebd6454e91530ded2bf7a6c6d1bd9f2a21d1db4d63caefd72332b162f5017ff2460a2c3172044f65100c8885963f87b95e4824c7ab03d1564bcf70a8f2e49e6c089c1dd251e099b9e9d282515d52a6823ffb6f9e27014888e984a561f7e9254517fb50c7baf1b975d1485e00e3c1455c0225e7c0a0dea490b630a76c89f027bae39539ae941aa8ad8f1043c0cc719ecb8d55fe6447c7ed8f43dc6d972d0f251e91bc1dac2118838117573fe3f1a83f52d4234eff207510b51e31e94cafcbe3b85ae6144748257a7c6b3888fab5905948fb6c1bbefebbc3581fc431c46bd337fe0dbd4ab225794b8c8938236b3a3595b789ed0212698c41ea45eee337a653240ea211ab7c0476cd095cbb4a0f64b058a40cebad46f1f67eed4ac9e58a9a7cf027c8e2c9774a5033fb529443508f04fac86e6bfd503f70c5276b01f147f0024f20b39b7d01a2a29a2e963228bf51c51f687e7eb2f9e36a4196d734d63ae5f3b4a7bf5d32117e912ffd99ab79f82a9683e62c36cc2661f2028e7fc71588f870fab101187017477a065b68308ebeea2b25459abe79436b292499a4071fbf77a2dfa939d915288265f7ac19127709be879f83c92438b3c557f60a2e4bec7992a0b9cce7dfc33bf3075e39463cbb59568285457676f23f46e1b8df5e78a4a9be7a1e135dc9a053fc2b868b3510c22a698ee3d54045cdecf05d9de0e12031b3cefb5a30bab5f178f5020db72c17142a4d7e0a58eb98ba9909a69d193861cc236011fa0c92e62e29e61c03dfb368259decf14ea038f5431fb3d4d96f0652076e8f70db380b1dad27c722d717560ff8c7c9ffc68ac1e1cc71c4b83c5c66e71cad11aebce188115fc85971bf598b06c7b5f1b71edbac863a8435fdf3f6e444c7c783de453e408bc01342ba3774e99b3cfd2e7a434dd1a296de94840118e09b592eeeb1b13c537f193f2cdc1226814e1e0b620f3bd2e9df2c3ba8f2510a9d635fd2d69160952bc9feeba58ecd3af266fa3c66af1fc02bd7d715a71a52a3dbad156b8ae7d45beab20521b8fcbeeb9ccbdeb3c1ce27bd0a9f7788f0ea0cbfcc84a37ed901755704107970f8ed0160326957dab8203d13edc9476cc286c7bc7c30084c8c8b66b2adde968274b2a47d8d7a43f02b389cc2c3262ebe6cd8f643eb3e6e655202ae16f4c8a341f587de2f8220cababd53bb221a78e52d3a96e0fca18e77ffbdd598b2c90a073086286055ee0470f9920108e2a8b94e1d9946adc6730436989590ff28e4b4d05292475c58e039dbfab8f8fb8e15c8aec871aab8030fd175bff5647fd116e46536e9654114155ee546a0084fba2108b362c4dd678dbe2718628f76cc4a22d22ebea06bae934052865514353e719c5553206b502ed005254259ff7e980b570e5a6af3f2cce8e88fb42f1327d6d267fe581f879ae59c3dd901d006f25b17a9bad65563c3fffe6c96e0531107744cc7683b381078a45f563d1c1c5b92c7a7491500fbdae184f27657936503612b0550d0fb4a506bb75457b5b7da6e54059b2c9b02ae23b2c6f51a9e02f810057ca231b68ebe26cd4632b3b9b0f368d4a165a16bb26752fd31229b8d31d0b48753cc1981c1d13055dbe2589142ff0042d84dd1cecaac07bce1d56f8f9fa6e948fc56fb1cef1a5a0b8bd314de4fde33b24360de0e727ebcccb10e10741ccc145b55fe7c45a4fd9a7eb7d70e256f199e80c4e5bc9e1b31231e787185bc12bf44fcb704da62eb11cee24835e6f2855402f629fdf60552ad3e765b68ba158e875653f5f69f18d73af2dda8503a64851ee1fe18647540c3b61d05893bb5148cfdbced593f2c9c884c54abdb6e530852d14bcb770068ff4c01e5a5f5157fb61b30b8fe51596b98213a9e1503bba06081bec14f31f27de9e431466fe52f1ab2296d6b95842fcbf16703c86b13c88202d8a32d0c6d9cf6384e0df9f75ae84656392f513180d6bfbec80040a84a296406bcbb448c052c9f1238b650be00bf91e7f1223237fe860dd936fbc76bc50dffd46c32983a04f578f83fea14eccbf52cf482aaee75dd8c8b08d314fcf6ff34612aa8c12d064f99db81c87da6ec22f6870bca5a04f808f9ba5ff8d1c4811c8a3c82ac7fdf204b3cd393640e6a316ded6a7b0e11c7653091ef90db65b0ca7713f437328429306d90080461cffa583769af0e97ea799b284d1be3a284833fab719568302e0dd861919f1839b06c1ec9bccd7d4e2891b2801cff7bc4681432b80c83e5abd20f9009dfd2c65fdca267229d341a4db98b20757293cb757a28d4338619a34112ff6bd2e4ec69b2323ed5ebc4896bb3424dec7884a788bea4101df42666f0589ee6cf126fe1b5e9ab105f1d8735cb55fa45753c91df642907597202ddde7eb42c88a027e681e4fd6d2cf18bf7b641169ea39e6b3186ae5e3d37e100852b1d1abd6659719ab967555bb8054f6244")
    }

    vcs {
        root(DslContext.settingsRoot)

        cleanCheckout = true
    }

    steps {
        script {
            name = "Set Deckhouse config.yml"
            scriptContent = """
                cat > %teamcity.agent.home.dir%/config.yml <<EOF 
                %infra.secrets.deckhouse.config%
                EOF
            """.trimIndent()
        }
        script {
            name = "Set Deckhouse resources"
            scriptContent = """
                cat > %teamcity.agent.home.dir%/resources.yml <<EOF 
                %infra.secrets.deckhouse.resources%
                EOF
            """.trimIndent()
        }
        script {
            name = "Set ssh_key"
            scriptContent = """
                mkdir %teamcity.agent.home.dir%/.ssh/
                echo "%infra.ssh.private_key%" | tr -d '\r' > %teamcity.agent.home.dir%/.ssh/id_rsa
            """.trimIndent()
        }
        exec {
            name = "Run Deckhouse with settings"
            enabled = false
            path = "dhctl bootstrap --ssh-user=ubuntu --ssh-agent-private-keys=/tmp/.ssh/id_rsa --config=/config.yml --resources=/resources.yml"
            dockerPull = true
            dockerImage = "registry.deckhouse.io/deckhouse/ce/install:stable"
            dockerRunParameters = """-v "%teamcity.agent.home.dir%/config.yml:/config.yml" -v "%teamcity.agent.home.dir%/.ssh/:/tmp/.ssh/" -v "%teamcity.agent.home.dir%/resources.yml:/resources.yml""""
        }
        exec {
            name = "Run Deckhouse with settings (1)"
            enabled = false
            path = "ls -a"
            dockerPull = true
            dockerImage = "registry.deckhouse.io/deckhouse/ce/install:stable"
            dockerRunParameters = """-d sleep 60 -v "%teamcity.agent.home.dir%/config.yml:/config.yml" -v "%teamcity.agent.home.dir%/.ssh/:/tmp/.ssh/" -v "%teamcity.agent.home.dir%/resources.yml:/resources.yml""""
        }
        script {
            name = "Run Deckhouse with settings (CMD)"
            scriptContent = """sudo docker run --name deckhouse -d  --pull=always -v "%teamcity.agent.home.dir%/config.yml:/config.yml" -v "%teamcity.agent.home.dir%/.ssh/:/tmp/.ssh/" -v "%teamcity.agent.home.dir%/resources.yml:/resources.yml" registry.deckhouse.io/deckhouse/ce/install:stable sleep 120"""
            dockerRunParameters = """-d sleep 60 -v "%teamcity.agent.home.dir%/config.yml:/config.yml" -v "%teamcity.agent.home.dir%/.ssh/:/tmp/.ssh/" -v "%teamcity.agent.home.dir%/resources.yml:/resources.yml""""
        }
        script {
            name = "Set per-s"
            scriptContent = "docker exec -i deckhouse chmod 600 /tmp/.ssh/id_rsa"
        }
        script {
            name = "Start Deckhouse"
            scriptContent = "docker exec -i deckhouse dhctl bootstrap --ssh-user=ubuntu --ssh-agent-private-keys=/tmp/.ssh/id_rsa --config=/config.yml --resources=/resources.yml"
        }
        script {
            name = "Stop and rm deckhouse"
            executionMode = BuildStep.ExecutionMode.ALWAYS
            scriptContent = """
                docker stop deckhouse
                docker rm deckhouse
            """.trimIndent()
        }
        script {
            name = "Remove settings after deploy"
            scriptContent = "rm -rf %teamcity.agent.home.dir%/settings"
        }
    }
})
