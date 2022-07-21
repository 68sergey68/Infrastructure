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

    subProject(Infrastructure_KubernetesInfra)
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
        password("infra.secrets.deckhouse.config", "zxx8f554aee6638262e927319fdda3da443306403605e07d205a7dc753adf5e284ac5c84caf4f70fd294787ad703c18f6df46849c0df09216c47867cdcce999a30053bde0ef7bfde4e77a0ddd02882fa655adb74a444688c420894425414ec90bfa9793a0d9f94113557f5fe600ec6059969ea74ccecb1d6b594aefc6fba214e9c4967ffc7cebafa985fa60b06ec338ab75316bae1dee2e77733869c9627c4c7331d07ff46e1462b39a7966e61ffed9d8dab27755b42ee66b65f1bacf593c93b873f6408bc5fedf971821aba35ef75c4ba6b615869f8fba7696d94053a73d150af16507c3ee646b9dfc84ba372837d00fd9a136e9d1ce626abe1ad4bb713890ca80462c1a3e59264d9f1bef2d2a07f62c996fb9ac745dd65a42dbf43c56c19712a36c64643f9c783050ea3f4fef1487e5129f2d9b06360a21dd006c84cd401c69ba1ae6a73bbc2cad91a58a53f618981ce6112b1c13ce0b432a6a402696d570ce09d44611c4be72ade39fb069dc0835a698c5c84caf4f70fd297374cd52b0b6a814b47d7d53b5ebc3c0f26e3794e2a55072be10e3ea375acd3467f397ceb1afcbf1717ddc0e3c9f1ef3d7e1b68bee9cde95cf0a586c82b1265f0c5cf50fb611b2a9c669101e09d2ec6920caee554f5e49f44d9eba8f37c39f3cdaae5985ceb527ff6c89d63a476b6d6bc2866025ee2afb9bb93429e4526c7c48715b42894c5293846a5b900ec10edc39646c54347fc3b6500a78fcd12b790704ef242afbf64dc4227269e336ca47d633e583358c91f2d07a124bef2478ce59e77d63d4e16a7f07439c641462b73dbec188c7ceb856dda649bd07ab160605af2127eb800d7812f3630c5cf50fb611b2a90ada075b72f55613502f0530abe8f9732fb63b2ff1c05988b4c381c9a1fb4292e2874935f32052bfed23372ef76bee89c87cfbebf1aa258185ab59db867569c0f4c196d3450089e7e320fbab2ea9f040e6c4609ba003c7ad99a8f249e7e45f3de5b4530e3afa74dd5c793475fbfb1735583e58173a3694ac3b34d30b102e941c9bb7199bc5f6e488906b66b88bc7044f461a3b45ab6110023d7eaaa4156787cb316757feb18a39d684be1c2aeb2c9bcb455d501269929d9108a2ed6309078de82a8a672695062f8b154afe7d7d494dee05671fe0b703bae9a93ed5355214e52504358aff8f8cbd3573b0b23bddcb86b7349105a9cc2fbec3fe156938f753e9b1461a3b45ab6110023d7eaaa4156787cbbb0a539e0c19f04e33bf2e4b3ec2802e86eb114bf2c9a99a8656ea8b0ab43d5ea895c10fc4980fba1990939996397046b0b2774d1400d69d9fadc5a3836d70ecc779b4fc4a576aeab145ffb833c350ddb50c040823b7b74fed374349c385a52ca6331f25c048c9567c00cb06ec441e0dedee7464c5ee9029bb6f16796fbf79759695bb8549c9e0097867cdcce999a30007c4a14d7e38be0b8844004bec91aaaad84c546e74e255d6cc11b090d628914447d0fe1a0db7c212b1690d04670b1ba7cd0c3edbd7b4edf060f023051a4bc7850de529649872b274354af261de13bba6b79acab546136d9bd59073e86b4783d821aba35ef75c4ba622056d04c011edc08450fc5ae4da0a357f6c18cef6b0e92334d7faa018f6dde2adb74a444688c420894425414ec90bfa9793a0d9f94113557f5fe600ec6059969ea74ccecb1d6b594aefc6fba214e9c4967ffc7cebafa985fa60b06ec338ab75fc295f134c88fa751cd63adcb95f07bfcefbd53630cdce494543255a9daf78fe4afdf3bfb9446668024125fadb0fc61f0c1b50251f7af2f047d25bcf8fe1ba516e63fd92ba506a3bc8c28c1d46022ecb4b2c435109cd3faf8aa4fa25d84edda598d5da9ac6e1a30f024125fadb0fc61fd23d64a96c3e36a5315e9a48f418ea983c46680eaadf4130c754f9ad620ff2227f6c18cef6b0e923bfa7367d1f51f06c0b6f75d5c29386f0d75e5723ad42ed8469f0194d574765388be4f4f9a53816ea6f54783c2801602abc0ea2b09b4fc86231c0d4f48540ac558dd3a228639519719f3534d38ad9b9d218fbe890efab496fd00da748673e9f1c791d1ef93c53b16bb4411f4b493ee963a2fb1b38d3789d45e1edfe9e9f25bb44f15d6caa84ea6044f5dac54fcaecc72e6c8733f0debd57057bed865af2d53a34c699ff1f705c00985e79e6f80505323f52e79d02e1ccf6f4b31ae0404b84ab4d975f6d4d53ebd2d1a9603fe8d7479c21bc84340fb699a30c5d13633deda192c65ee546a0084fba21dcedac02a6913b49deeea0fbdbb8a48b5d2eb711e6f9bc803dbeeed7132fd3b9834b579e65b7120a474103644ecdeebf2d887cba2c75e3c95507cc5f50027fbe0724125eba5fbf893feeb5a73cc18d44124bef2478ce59e7e36d646fa40c16c06c8733f0debd5705f53b12f57ac81e8c14f1fd740a996645b987eb1591a9cd7d4ec7a3a5d145a3873f89732afc1895bb95cfaa8f7b82bdfbaaf03436c462885d301be3c6691922902eb2d6d5278da01aad912450b3501443c3056c0f270648f776fbee6bd05e84089a5601c6bb333766e27ad5b473f8f0f1d153d64b149ccc9abdcc89200aea3f6d298f0ced59e3cf9bd4a7b583426041ffb77028b4ea00e916bb9f5d6aff176286041c72df2449460497227663dc1cc138322e878254fc27005806b7eca1b0c1a4a501aa9f9d03ed3c1b642ebb15fda528ef7e5f83597f1580679d4159f6e86f981a3b2d6964a7f77ddad407e3721e1306672ad717420516bc396014bba2ddee4e0c5cf50fb611b2a9c669101e09d2ec69caaa379576cde9915d4192b980c339d7c16f8321354afeb76abf4552ac0488d739b0cc1bd613e0e98b7c8d4f34383cec4db6427f8ce744c65ca092094134854ec34e2131513c7b808279b08c50a1c066117e3aead7fd7ee1e580ba0df7ceaf7c2645ea7d54c08551e1f60c30257c79e2f4c5ad1e5d2e9d757a0ddd02882fa655adb74a444688c420b832a6b6046b1aac319bedcd96bb0882708572650af67f9f170824bfc0ce09cb65c6022278f94279ddbce67a9309c634fb434128ac01bfaea6752d9119f833824f80c8e4efd524188b93c444969de87dcec6acbe4e0343b1bc8ac0628642c2b1ac38e9bf8f74827d31dc328afe1e61b519c5b4961b10e1f3626df6e70e84b0a51eeb49e819969990a81e5ef02363fbf6838a27cb8e1dbb7d315e9a48f418ea983c46680eaadf413023bf3a1e8cf7df2c3a2258f57bba95710506755241b3bd583e8f234125238d2acfe01bccdcd29e7f61731cdb33a4eb11d2d647baed849a49f90d111f481913ab59b569ae4a56ce6402b96eb753b1d2ecac69301d6c737b3ab9c4614907013acbf778493a2edb58787269e14537053029cfd055f4f9d5dba2505745a124f807c6d88dd262d09cea92f3a5753c95515be7c3d5bcaa6e79e08fab59dc2965387d93bcb65428406852c24cdf46b607ba371c067ebfa8be23ac8520cd4f5c5ada9316b528ffc82c6978906af1a6a07d92e586ae8bfd9a32a8372b92e17b4cba493729646f94459a758358a1383038166cedccbf96ccb37c51fdbd2ea765c93e89b9fb78e070f64efb0680d02b41218158c4142cda1d4ed138a41762e7ff8864e1d69995363c55c92ca5f1d0a704db1d074ec75cfa4e1fee57d004e39a7bafe52e1c6372ee2dc50a96918b09c158211fe2417bbf76d6d9f40bfcb6f2b24186205409f1f47f1dac0136550fe6e6412367b9925f3b99ba509e920fed9d4567971aef6dfe8971be7d6a7f1e9eb0d499e0d0a9cb53abedd8dafe6a93bf4997f12d3c63cb4d66a15da8d1bee5a823a4a54c215001aee18e363a0961d9899f489db76c1403257bc60d84f5e98793b28ea0d16bd21d8523b61ea60530dc4b29ab2ee42b597abcc2241d02e34311b7b3e31ec4689774af4aa1a1ceccd62cf99745f9ff425d87fb89b97907fbe02dfe7d16e35068de6855c1044dc29cb782f9e20f2451c4a02adf384a9a3eac2b293bd4927118417822cf629dbda0eee3f637f0fbe071b7bebbdf4508f8782fd4d2d24be58e5cbfb5a46ade1905620d3401cf41474d006a1cd3affca414fec6556d2663c9fb54bac4f810df197a7764a48af7bbbcc915c657b2f964fd64155d8415b3a176cb34831036a96ae6b37d30045ea18c3001a4ce71546b7f745be55f0f9f8dda6ea09eed6e8841334f0f729553dde95958598bbdd86669bf2e28360c10cd113579242ed4b694012fc373040e2532e045e87565ea6476866706e22f6abf91923e411acc987221702980099f9ba7ee8e3f63d808014420bedb5c43002164720717e2b7a44d15531cc3f0e3dd71c14f584e175a5c992b32a792072864209e86214c854953192d26e105f74f2f2519f5499d4567971aef6dfe8971be7d6a7f1e9e6e9ddc60aa4de2cb83cf78e34ac21ed8ae5d93141e455e6102398031c7c218314560d6931a3a9e6a425f7300fd332edb93f9652ef6640e0cefb948645b2379e4abedd8dafe6a93bf4997f12d3c63cb4d52605aa51bf76340834bd731b4368f3ae467377f889e4402c28df61c7ca7aa9c637731e4d2e896d8e48b52fe643b63306af921ff733437eb40e4e1b56221624827fad83f9ed6d4aafae330b9916d120b38d15b83ea84e7e9db77216110da4c865f70db3ca70c79f6ab5183006761dda3e2b8da1ba5b4e0e1b04f76db3162981612ac2849c0c2a7788fcb3b2ce17790c44d7b0206e9409521f6258242dfa512adabc2af90ff96a709d8cbe93aac60a7a09df6d8625c16cc1b70e3c4731dde8d11f47e12db4162011867e88c901be9ab687b3aaa0b882673a393853512ba31cb13269928039dadb7b5daa399e5d3bd265fe429a53431625bc2aa284f07fdba28b6e8dce939c2f0ccf17a82f23487e2824ef310bb1ff01e216ccda0ba08e4685d3515e7188b53f9e353e8f912d88f9b32d692e431938c0ad5831e2950531d8dc93aa565fd7b6fb286ecc27dfa2f4283302d87a5dc32cbf5c7c01a8b94bb5989cea6918f5e87c2f5be0fc20d273e8edd7bfb08c3081460a1b6335740bf863bd7d4c0039fd2e34fd406de6a66c1fd5ee9bdc4453968b115add201f271639c1f68cdd0baebdc0422fd31da043a60047fd082d0810967be0ff9c769616a791157be8976b77c2a7b263afca7c9f42caa613d014daba777225c5236d79cafb08b8e48c71e42650f328e313eb6522707e362aa7dc8033403b404bdc062c2a6024d453adfec360dcc3e340d0e47234cc1260ad7d524e0df51288dcdeb966e2139c0853999351f4351861e58e2cff6be87e464759aecf0e5172fe6f03e170f876678dc8a3d228d4536f409fbedb8c911e823cbbaf6dd5ee6e5796d71a51b59c077bd5e8b0d8e845ce1cb5ff89d1570e13eda2a5ca70a0cedaf3e0d732de6c1e8bbccd59861b76b0a2ca279fe9901c929c5471f7394982a4a9c62cb4f4a60bde18402389aab12870002373a934a30d7857835716405ac6b9deefecea17dd679b7d12dfd19bfdcb1cfad0f3d38e13e3b85468ac79a312e6efc7a53a6bf8612f346b5c7e525faef38b97820f337dd2e38040c15e803b6636c9e3ad27a7234e8bb867cca2a3457d41d9ae1b6c931f1610b7221c818527c649d4bca3a8539c7bae0f888e7d4d752baf8ea86bed7e70a88ded0ee56dcebf6541dc3ad8e8fbcfe0367caa1820366c318ab07320fe61cf56106a97cd62ba426cb3dea4ff6bc645fc7a39259fbf28baf2b5d7ba4c827092b3d7e4f86df12993e92aeba3ac02af0485ff2e0bf67eb25561ed336c5175887f3fc94c63264715ece33230b0ff24c4f55e72c19ca66731a38c70a39a92c2a9ae66ea7deab3e84743b83130a789576af6e315210f27e8d00743a66f11457be768be76e021a8e76336fb7ee3e865d3d38b6080394450b6e4e5adb3dafbc5fbcfc2c5951c81e03db7933691433c08287e09629700d6ba070080e06bd5d045516b11de777f26e3bf533ef6c07c361dd8c7067bef58c3276022179a3207997f6b05e19a1f02ea44bbec5aee6f7e0e6acd24eb2b2a6f3d56f4bbf6677c573a3bedfd89be82ee7c0353e26f2391118c7a7f7dbd7f5f3c20a4e69b74222e3d0ef8a737f9c6835e68a92d43e1044636cf5cd79fc92b44d9d380b93c8ce1efc18e9fe265f9e6e38f2418ec622883a95e367477e55c3fc12c58bb28bcd8303fbe47a104f0caa2f2739eb3a6f325d2e99527d45c946606331f8f5b89ca9a7cc7cc4d99a73ad474242c9ddfd2b7bd44ddeb707a0dc5ecac45c66b3e7e9d30d26bd19d47b14900fc401f929159de5c509fe4f683c928cc39dd4fd9810e0539fcb48ca4c1fdc90803d07bea77c5d0a9a4b0dc21e8e0727915e9365d07ac213bc3625bd6c1abb9fa0b4f8ccf1b487ac59337886dec7ef848993091230b4e2573ea6f201a4cdd5508a7c3e9a5f2b54fc2511ddffa1b26b074dbcd473fcc30a21091346e8271b795f59162d4be615529d153cd1f1e9e794a4d4f5ba64d8cba9a563646e8b78322000af4bca5252ad9ece9a0a9d36036784d733765d6c5ae4b78ffafc637b8dc15d2b48e7b5211ef64e36d1a9247b5b15f23129db85a665fc34d93ab65c067ccd271ac01ae50fc19e852acd479027e99700d97556acdc2760dbb0bf56261c24bbd42828983cf38aa3afd6e647d610bfac592590fe181c5111477f2221d9b7564e44978ca5d6454e91530ded2bf7a6c6d1bd9f2a21d1db4d63caefd72332b162f5017ff2460a2c3172044f65100c8885963f87b95e4824c7ab03d1564ba0d8955c21aa7f02b371b4f8ab80e989d2e199fb1470a9602c1df86a43175d488407bbe871201d9f9287ababc040dda3c2d44e7d0e5c1f01038fffb2df38af2cacd4a13c6b5a927a5eb7f2031c6059df9539ae941aa8ad8f1043c0cc719ecb8d0a41f02939132bdf2269f959d0aaa9ed51723f4635ed4615bacfabbddf56e21b678e3924c5d12cb1a52a6aa842ee7d54927bed981672e9b5140e3110f265eb50362bf6a1dc540100d2b5daca94212800371e4bf95ad86173369575a3f6f16cb09c89571b301b1184c1beaa496476dd269258ab17a729925ff6b8dc5dcabee566e624c53fe85ae2d7ec4d259e29af49edef2129dca8a2361263802e1563b9b1612312392915311156062e47d48bf27076b2c0559f77fea7b70baddd50f759e42a39aec41946181e531e6678595baffb9a415443331ee2d1dd45ddbfaafcc9b16ca05848577699407eb31ae0404b84ab4dd8b59b2170127c617500955a785bce8a266e346f7e882310aa90fec54a346e7dd845296d2228bca26a936272708e7a578820191c8edc25d74facb8079b2a6f66223ea890505ff66c32a5198b1d4cbea177e311b6d4be5ed4dc105d3bd2a8702cc29d4c637e2bca6c9d7bb0a89e9f1fef5e5f8bfb06cca70914add279f6a7a80bd38ce2cfc3172a4f9f428fdea0cb68f5bee0326cf4db07a95407785d45914cf863c67acde287a03927a059fa174bc2739beccff5e6e2602eb5d77be5e9f2454db85609caa1f7a2365641c666ed24aa834bd1e0bae8dba1526e382159c88fa579e841eeb111bb6ffe7f0ffaf35bf3749fb08883751eb57430f2be1afc9e9b256738563a24424e4e4a4adecbf8cd0d4c6da6324deb0ce9754ee0994dde9bd08b7b3ee0c2916ea8ccc23283908624f72dba0a1d4e77970e4dd47d63d4e16a7f07430326e7adb1749395d079c5b9b1386c0fb0c8c7d3102135e2de56e215a4086d3ed5b9bdaea894ef768f43bdc6f288333329a8024e1346be73ef2c2c62b450320ab71f5d4cb631cb529480a89dbd3d52b07ed359f2af3f25d347f033cfb093cf2f6957dab8203d13edc9476cc286c7bc7c2d15c86e0368490f5e4f04aeffc357994083a920c3cc2c7721e08ddeb886a16d58af624f8ab6d7bd36cc2661f2028e7f28b11bfcba03d3f3655b3a1131ae150ed6dfdfe20ec54c1508ae0dd78ae837d77b39c749337ce6b4e6815ff7c8c121b18c787d86a659721ba30f6ef2dd83a3a9622f97f8426a91d433589b25cd7597d2e4346a22caec4fe1e9ea4d086b65959735829f25115e0a823e6e655202ae16f4aa5dba9d34876c504a392ce46a8989013cb63c7de86c57921fc6836efc6aefb6d7097b1b5af2a443db1fabd84c8ed92d0d1eff0dc46fdf2ae71840fdb0cef0b97f0dfac3ad536dadf9f95c7a583af58f6ad289e41e0b176e494cfba2b53c0e0ba5aa1f886294a83b9545437a7c54c0508ba64409f127e6c5ecc69cefa9f80e049c641462b73dbec1f399581a1cf0ebca89c84660d4e4a733f09ec798920c627e35363d83cf8d893fa58a53f618981ce63dadd7a36f913c9f9a6e9958fd55374f5a5969a5d4fc034eb74d8410758919c746b8242696b83740bd5830bcb318e3f9c115ed15f09b97387b505d52df32303b60b4f1b30adab1e98a7308b3f245893772e81854fb79e31b20aea6bbcbff754dc2e18ff800ab0b95f2af08e3ca322e27edb9c05ca56b3bcdac9228a28ee7093e41cdec47e84b66a92aaba825e37a456195bd96968a1ab2a9f6c99598120b158d4e59f15c2faf74b6d41954e1c6aa9a3cafb9a2613b4afa17dc797b35f56fbeb15e1fa2f3b491b6b1911f4c5acd51df0e36df0f7567fb46db6ffee16cee1212ea62295614e49adc03f690a454bde077a9127f0da4f35cce140bee3d039f1e0ca3dcf90ff9529ee7c96ed8256ba824771e20d1c32befe7dd0712afc2a3e1accdc54870a0bbd25d9dc43049de96e6dcbeea8406dd548446459e0eeada17424032832544de1e978c3f97acd105f7392b03e3b62ecadac9dfb88fb932989ef9a21623f4748eb491bb17afbb9cd69644349f3ddb907770220be0e0825b5b83f64beba0b4dea0f1146793c6927437fad473fe57cd845b98404f6364f66720d64eab0e0a7067a1cf0da6fa6a5962470d2718e1e6a85387bf1aa5ec90e054544350843b3f75fedb3ec6af2c3035122eceb31b868522431f5904b01de4b783d1ccc513131a601d264d203b3710a8650c219f0d54356e480d4464360f349e7c90c4a0ae61a0e5fb83a382b801d6cb535585d3c40eb1977175e802e4abfe33ae28bce17de96fc862e7165fab73ae45a2b3822458698d6821e98044bf231f5c031057a9e76592c92f4aa6dc18c6776e141ca5df10b35a")
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
            scriptContent = """sudo docker run --name deckhouse -d  --pull=always -v "%teamcity.agent.home.dir%/config.yml:/config.yml" -v "%teamcity.agent.home.dir%/.ssh/:/tmp/.ssh/" -v "${'$'}PWD/dhctl-tmp:/tmp/dhctl" -v "%teamcity.agent.home.dir%/resources.yml:/resources.yml" registry.deckhouse.io/deckhouse/ce/install:stable sleep 120"""
            dockerRunParameters = """-d sleep 60 -v "%teamcity.agent.home.dir%/config.yml:/config.yml" -v "%teamcity.agent.home.dir%/.ssh/:/tmp/.ssh/" -v "%teamcity.agent.home.dir%/resources.yml:/resources.yml""""
        }
        script {
            name = "Set per-s"
            scriptContent = "docker exec -i deckhouse chmod 600 /tmp/.ssh/id_rsa"
        }
        script {
            name = "Remove Deckhouse"
            enabled = false
            scriptContent = "docker exec -i deckhouse dhctl bootstrap-phase abort --ssh-user=ubuntu --ssh-agent-private-keys=/tmp/.ssh/id_rsa --config=/config.yml"
        }
        script {
            name = "Start Deckhouse"
            enabled = false
            scriptContent = "docker exec -i deckhouse dhctl bootstrap --ssh-user=ubuntu --ssh-agent-private-keys=/tmp/.ssh/id_rsa --config=/config.yml --resources=/resources.yml"
        }
        script {
            name = "Stop and rm deckhouse"
            enabled = false
            executionMode = BuildStep.ExecutionMode.ALWAYS
            scriptContent = """
                docker stop deckhouse
                docker rm deckhouse
            """.trimIndent()
        }
        script {
            name = "Remove settings after deploy"
            enabled = false
            scriptContent = "rm -rf %teamcity.agent.home.dir%/settings"
        }
    }
})


object Infrastructure_KubernetesInfra : Project({
    name = "Kubernetes-Infra"

    buildType(Infrastructure_KubernetesInfra_CreateVm)
})

object Infrastructure_KubernetesInfra_CreateVm : BuildType({
    name = "Create VM"

    params {
        password("infra.secrets.sshkey", "zxx5a5969a5d4fc034eb74d8410758919c746b8242696b83740bd5830bcb318e3f9c115ed15f09b97387b505d52df32303b60b4f1b30adab1e98a7308b3f245893772e81854fb79e31b20aea6bbcbff754dc2e18ff800ab0b95f2af08e3ca322e27edb9c05ca56b3bcdac9228a28ee7093e41cdec47e84b66a92aaba825e37a456195bd96968a1ab2a9f6c99598120b158d4e59f15c2faf74b6d41954e1c6aa9a3cafb9a2613b4afa17dc797b35f56fbeb15e1fa2f3b491b6b1911f4c5acd51df0e36df0f7567fb46db6ffee16cee1212ea62295614e49adc03f690a454bde077a9127f0da4f35cce140bee3d039f1e0ca3dcf90ff9529ee7c96ed8256ba824771e20d1c32befe7dd0712afc2a3e1accdc54870a0bbd25d9dc43049de96e6dcbeea8406dd548446459e0eeada17424032832544de1e978c3f97acd105f7392b03e3b62ecadac9dfb88fb932989ef9a21623f4748eb491bb17afbb9cd69644349f3ddb907770220be0e0825b5b83f64beba0b4dea0f1146793c6927437fad473fe57cd845b98404f6364f66720d64eab0e0a7067a1cf0da6fa6a5962470d2718e1e6a85387bf1aa5ec90e054544350843b3f75fedb3ec6af2c3035122eceb31b868522431f5904b01de4b783d1ccc513131a601d264d203b3710a8650c219f0d54356e480d4464360f349e7c90c4a0ae61a0e5fb83a382b801d6cb535585d3c40eb1977175e802e4abfe33ae28bce17de96fc862e7165fab73ae45a2b3822458698d6821e98044bf231f5c031057a9e76592c92f4aa6dc18c6776e141ca5df10b35a")
    }

    vcs {
        root(DslContext.settingsRoot, "-:.", """+:k8s\ansible => ansible""")

        cleanCheckout = true
    }

    steps {
        script {
            name = "Prepare virtual env for ansible (pip3)"
            scriptContent = """
                #!/bin/bash
                virtualenv --python=python3 venv
                source venv/bin/activate
                pip3 install -r requirements.txt
            """.trimIndent()
        }
        script {
            workingDir = "ansible"
            scriptContent = """
                #!/bin/bash
                source ../venv/bin/activate
                export ANSIBLE_HOST_KEY_CHECKING=False
                ansible-playbook -i inventory.ini create-vm.yml --extra-vars "yc_token=%infra.secrets.token% ssh_key=%infra.secrets.sshkey%"
            """.trimIndent()
        }
    }
})
