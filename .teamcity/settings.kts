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
        password("infra.secrets.deckhouse.config", "zxx8f554aee6638262e927319fdda3da443306403605e07d205a7dc753adf5e284ac5c84caf4f70fd294787ad703c18f6df46849c0df09216c47867cdcce999a30053bde0ef7bfde4e77a0ddd02882fa655adb74a444688c420894425414ec90bfa9793a0d9f94113557f5fe600ec6059969ea74ccecb1d6b594aefc6fba214e9c4967ffc7cebafa985fa60b06ec338ab75316bae1dee2e77733869c9627c4c7331d07ff46e1462b39a7966e61ffed9d8dab27755b42ee66b65f1bacf593c93b873f6408bc5fedf971821aba35ef75c4ba6b615869f8fba7696d94053a73d150af16507c3ee646b9dfc84ba372837d00fd9a136e9d1ce626abe1ad4bb713890ca80462c1a3e59264d9f1bef2d2a07f62c996fb9ac745dd65a42dbf43c56c19712a36c64643f9c783050ea3f4fef1487e5129f2d9b06360a21dd006c84cd401c69ba1ae6a73bbc2cad91a58a53f618981ce6112b1c13ce0b432a6a402696d570ce09d44611c4be72ade39fb069dc0835a698c5c84caf4f70fd297374cd52b0b6a814b47d7d53b5ebc3c0f26e3794e2a55072be10e3ea375acd3467f397ceb1afcbf1717ddc0e3c9f1ef3d7e1b68bee9cde95cf0a586c82b1265f0c5cf50fb611b2a9c669101e09d2ec6920caee554f5e49f44d9eba8f37c39f3cdaae5985ceb527ff6c89d63a476b6d6bc2866025ee2afb9bb93429e4526c7c48715b42894c5293846a5b900ec10edc39646c54347fc3b6500a78fcd12b790704ef242afbf64dc4227269e336ca47d633e583358c91f2d07a124bef2478ce59e77d63d4e16a7f07439c641462b73dbec188c7ceb856dda649bd07ab160605af2127eb800d7812f3630c5cf50fb611b2a90ada075b72f55613502f0530abe8f9732fb63b2ff1c05988b4c381c9a1fb4292e2874935f32052bfed23372ef76bee89c87cfbebf1aa258185ab59db867569c0f4c196d3450089e7e320fbab2ea9f040e6c4609ba003c7ad99a8f249e7e45f3de5b4530e3afa74dd5c793475fbfb1735583e58173a3694ac3b34d30b102e941c9bb7199bc5f6e488906b66b88bc7044f461a3b45ab6110023d7eaaa4156787cb316757feb18a39d684be1c2aeb2c9bcb455d501269929d9108a2ed6309078de82a8a672695062f8b154afe7d7d494dee05671fe0b703bae9a93ed5355214e52504358aff8f8cbd3573b0b23bddcb86b7349105a9cc2fbec3fe156938f753e9b1461a3b45ab6110023d7eaaa4156787cbbb0a539e0c19f04e33bf2e4b3ec2802e86eb114bf2c9a99a8656ea8b0ab43d5ea895c10fc4980fba1990939996397046b0b2774d1400d69d9fadc5a3836d70ecc779b4fc4a576aeab145ffb833c350ddb50c040823b7b74fed374349c385a52ca6331f25c048c9567c00cb06ec441e0dedee7464c5ee9029bb6f16796fbf79759695bb8549c9e0097867cdcce999a30007c4a14d7e38be0b8844004bec91aaaad84c546e74e255d6cc11b090d628914447d0fe1a0db7c212b1690d04670b1ba7cd0c3edbd7b4edf060f023051a4bc7850de529649872b274354af261de13bba6b79acab546136d9bd59073e86b4783d821aba35ef75c4ba622056d04c011edc08450fc5ae4da0a357f6c18cef6b0e92334d7faa018f6dde2adb74a444688c420894425414ec90bfa9793a0d9f94113557f5fe600ec6059969ea74ccecb1d6b594aefc6fba214e9c4967ffc7cebafa985fa60b06ec338ab75fc295f134c88fa751cd63adcb95f07bfcefbd53630cdce494543255a9daf78fe4afdf3bfb9446668024125fadb0fc61f0c1b50251f7af2f047d25bcf8fe1ba516e63fd92ba506a3bc8c28c1d46022ecb4b2c435109cd3faf8aa4fa25d84edda598d5da9ac6e1a30f024125fadb0fc61fd23d64a96c3e36a5315e9a48f418ea983c46680eaadf4130c754f9ad620ff2227f6c18cef6b0e923bfa7367d1f51f06c0b6f75d5c29386f0d75e5723ad42ed8469f0194d574765388be4f4f9a53816ea6f54783c2801602abc0ea2b09b4fc86231c0d4f48540ac558dd3a228639519719f3534d38ad9b9d218fbe890efab496fd00da748673e9f1c791d1ef93c53b16bb4411f4b493ee963a2fb1b38d3789d45e1edfe9e9f25bb44f15d6caa84ea6044f5dac54fcaecc72e6c8733f0debd57057bed865af2d53a34c699ff1f705c00985e79e6f80505323f52e79d02e1ccf6f4b31ae0404b84ab4d975f6d4d53ebd2d1a9603fe8d7479c21bc84340fb699a30c5d13633deda192c65ee546a0084fba21dcedac02a6913b49deeea0fbdbb8a48b5d2eb711e6f9bc803dbeeed7132fd3b9834b579e65b7120a474103644ecdeebf2d887cba2c75e3c95507cc5f50027fbe0724125eba5fbf893feeb5a73cc18d44124bef2478ce59e7e36d646fa40c16c06c8733f0debd5705f53b12f57ac81e8c14f1fd740a996645b987eb1591a9cd7d4ec7a3a5d145a3873f89732afc1895bb95cfaa8f7b82bdfbaaf03436c462885d301be3c6691922902eb2d6d5278da01aad912450b3501443c3056c0f270648f776fbee6bd05e84089a5601c6bb333766e27ad5b473f8f0f1d153d64b149ccc9abdcc89200aea3f6d298f0ced59e3cf9bd4a7b583426041ffb77028b4ea00e916bb9f5d6aff176286041c72df2449460497227663dc1cc138322e878254fc27005806b7eca1b0c1a4a501aa9f9d03ed3c1b642ebb15fda528ef7e5f83597f1580679d4159f6e86f981a3b2d6964a7f77ddad407e3721e1306672ad717420516bc396014bba2ddee4e0c5cf50fb611b2a9c669101e09d2ec69caaa379576cde9915d4192b980c339d7c16f8321354afeb76abf4552ac0488d739b0cc1bd613e0e98b7c8d4f34383cec4db6427f8ce744c65ca092094134854ec34e2131513c7b808279b08c50a1c066117e3aead7fd7ee1e580ba0df7ceaf7c2645ea7d54c08551e1f60c30257c79e2f4c5ad1e5d2e9d757a0ddd02882fa655adb74a444688c420b832a6b6046b1aac319bedcd96bb0882708572650af67f9f170824bfc0ce09cb65c6022278f94279ddbce67a9309c634fb434128ac01bfaea6752d9119f833824f80c8e4efd524188b93c444969de87dcec6acbe4e0343b1bc8ac0628642c2b1ac38e9bf8f74827d31dc328afe1e61b519c5b4961b10e1f3626df6e70e84b0a51eeb49e819969990a81e5ef02363fbf6838a27cb8e1dbb7d315e9a48f418ea983c46680eaadf413023bf3a1e8cf7df2c3a2258f57bba95710506755241b3bd583e8f234125238d2acfe01bccdcd29e7f61731cdb33a4eb11d2d647baed849a49f90d111f481913ab59b569ae4a56ce6402b96eb753b1d2ecac69301d6c737b3ab9c4614907013acbf778493a2edb58787269e14537053029cfd055f4f9d5dba2505745a124f807c6d88dd262d09cea92f3a5753c95515be7c3d5bcaa6e79e08fab59dc2965387d9349567dbc6d95e12aca5f77f172ec35244b2208667499d18e409f62be8aadec208abeaec4e1c8d91d72f54d6478fcb8868a76d8ed845ebfc4755fd58ee5bfe35833589b25cd7597d248919401f2755640945f83e561af656275dc5fc3952f2c838af603e7dcb1d8b7e9eaaea9ab0abbfe479a40e2a8aa06a8f3768d1283459e7af395070fc087a4653ebae8884b285a29a8749fb7cfddd2274df27d8df9bc97367966e61ffed9d8da570a4ee1c7fad34ae7870b3ec1576924f4d3be00ae9ac8844531fc0ae88c55e4599d3166b1fbbc3451e1da2700c96b836c9c20dd575f1e1b8f68bdee04a77abe0653a58a09a0ede19d639d3f473b3ed50087d648b75447abd2e88db37d3a41aa4aab90d954a2739bbd943c1ff8279912c9b4b4dfd891417f697dd7021f3dea5a9d414b0ce27858c05813f88ca5846efcf8a3b813c0e460e53ef3a9368b4bcc5405e7b2315af3e180cf9eadebac17034025f16cb5bed185304ad74c33be78dff6f50acbaf201ef3b55763ea3db63f7a9202398031c7c2183195fba3e12dca95eb99fe8f9300fd2b9aee791ed68247b2ba8d9869e483485b35be1bad7e1abb0cbb96b7d5de5af37486348c28d7429c380d443e9f08301b4bfdbd493883dafd7ff7bbbd85b3b6c1357d32af774e142f07c149c93d897cb9abe1886db5eb93c5ccb66260ed4c70195324fc59f75650f22e6b6a14de8a8c085dfb4a90526ebf9cc6d7a194bec63712d79a67a700f2f7da51722c66d140e7e169611bd142b14a863eccaa415c51153463062bf41dce947a24e6d44ffa3cb0f326ee484847319c397ed85cdc531e80fd54df4a8dd94ecd3fabd5a63a6b623bcec9101c5ab580f6dcc0b891ca7700a4a822269180bfb279da27426d33e8cc76cd940586cd9369247cfd5741d8d4adc515c792743fbf0207d5e782b4e9908eef975a47f66ab2e15a540c1149d713e68e4e8f1b9471a49c539ab8f5ea74b1494dd282f10c4aecc2d67c8e7cc898d6e03660f0c94b14f41cad42d6730a981ab7fc2e5310551a9dc9277efcb7e031b995a0f0f153b5caefcbb9ab988acd89ff4dc7386993fc96c47b83be7398f441fc160604717a6dbc5240b663130d88790db967696e6e1235e45c17c4e0c620f049e4fd96901b95fba3e12dca95eb99fe8f9300fd2b9a3cb1f261cbb2b967db1a529087dee3ab99337fb79c4590076ac267cac0e38eaeabe6eefdb3407f43f7dbd5b8882aad3c742c5279d733e8eb5ea1ee7cd5a66dd487a5742c96b48070fdaa8794ed28961adfc6de1594d5884aa9eeff95d56df31b19de9681c52931128cd7682e52ecb7c71271c46ea617fea77f31d1d3a2592bb20c6c7c9183ca7d205fbadf4e1a17ceb6fc550a893189b227ec143fdaee1c6b04fabdd1b22fc1ccfed6a8a5d8a7540ca84ee461b4a5f62f90b59d656d5ebfad2864cc3182f55a342ee4eb28524529f9aacbdd31526bc8289cd4209e33e58b869fc9b6233a532b416dd88067e8967d57529b8ec2c79948d179d08d6704b364f21b9c10a06f0674e191ab62a26466949d776c8f65d7ce0622086a916dbed15e2ec1f42d75229123ed21f7898bb2bccc29c061eec338c8c7970620cedfac4a02ed9abb3ca4385936a9a61c1bb424f1f441a289f969925b3862740101edf09c0a9ed0b39e9bd4b7fa2856553fe6122fa302d015b7e3519c5521c374f98f062601355aedce092592f39fb8c10b7122233c0b216a0e53399d853f60018ae55d5fdd6cde5ac923a6eee6681b7fee82b8ed45d774c6a53a1d8552c8a3a03e53a132f251762d236d702b3a9ad75e2f948229a1d08c2b0a6f7ff7e5856b2af5d9fccd5f447df28e650014099e2c85aa625a5a764a6e143c005407cc9f2078bf9be3bc482567e7ac81176e009abde6a90c5368568700a8bc72e02b998265aabec1a4c154da9bda6746319d85ff7d964ec809d545276ef28debb4e239a34c214be4598c643da3b32cd433948cc529322a27d2da4342bbd291eb2980beb14305f83be9216923dae68affc6cfde8e19d8a2c82db88ea17ede85c051dd4059ce3a4d724e0da7dfb6aff96ec41d7481b7916ac725700f51c38b92ee76a2dbd6f9685ba0f98cb27f5f63fba679774e31369a0ab18bb74c84bc46a50c12f0e7072ebb1e956a4971120de175d457e39b09b3f862189ba0186060d0b0094f0d161a03db27b3a60ad9112dd50d9128f822acb30712d272ee215c8f562aa2b7bf2c9c2185eba133433e162ce0828265f145a62f6b09e8aa5d1497176eb1a4d6b1d4dc37ea1198120410aca3e0bf5997f598f7bd384eb9c46d61852a01f44bc19b8ab1ee541e00df88c08910686070cecb61f6ec7905ba21c98478e4a23dbfa09f85c293ddcd75b1a8a229071f2651c3631d7784fe3d5b544eaa81bd303ddb44b182ab7edfbe173698b3f0d9ef6239ecb9721de1899bc5a7685f02f5fea482a468adf278590b00c781a98448ad590bcb133ed7f755fb2bdf0898c11480ff0f53555bea0d51426b9abd3b9e81e13da5629b6d4972ac0f03d32a44afa94d72a9c90a9f9414037dcfdcce6b63343a89e6b169bbe77e4a1ca02c483f7bb0b4baf87a76e8c31864166125439d6eb51dc920d98fadc521ea876add4310bb1bec76833ca91e38dfbf35ed6f58b1d864dabed92aa7039ae1ed822e2864301a40741c61622ae34b4ddd409a4503544e62bd30f127a95c0fe5c8f789316d5904f6383795a715ea57c7715fa731311fa9706a1e7d331e551af92ea829d42f964d8ff92174b89b615f5b83744a020e493bad6b8aa4bb40af751bde1e889976e554bac064e04100e7497846b847bf95dee91980f1465576860d68fcbec813c16199bc4e91dc3f25c0da14ab15592bb297a034014a4e53fd74865f90ed08cb9337f3d2ecd266ddc0d7c0abbca1d5e69b6f2fd65d54306cc08decdb678b449e41e5b828fef9f81bf7b008a37c7d4b98bf52a6cde01287987bbfc4ef097f0ec2db9f81915a59120faa68d6651da75a7d06479a80f91229f9ae10d69e26d4051927d2df425220a9d067386c5984749c5dc00189c185ffd06a7bb4130284ca15a6d796b5caf2df6b5648e2669b1b430e673b4ee3e5b975458c1130ed431c335576f6db014e55f341ebd6cf3625d92febf71486b4d2c042e925ed246df507e88b9f80328fa14e137a8f3b50f3784145f015b2a4facc74af960123c3a12ed87bd4491ba7433564f97a1ca3f801553c1b7321c6e6c1bad357df39424a46a6ad40e1411ad90004d16c1321593bb7e58fab160e7f282f7266ec82dd70068cde09499cb0f85f6bdcefcd9141f8a165c4b8b939711a50b7f5b6de36e4ddbcd18638f5043116fd75c2ef055a4c4828c8016cea0aa42ea462a3c04aacb1d8d9da37efc2bd24d2afdb6a3b72c25b70b212c8831f3932671c1304be1a999415c59b518ae5ba9354373ba8d1bbebaa299fbb854bee97abac5958ff2869fcaa33674c6ea6255df11bdb7fdf7bcd8a421d33a07c6eede809875762e72c47dbcfa17aa049403dbf80603dfab0e7dc768fba689b2d599eb9df5d5fe8a11941c98033072632588034d089d06cd06f832e1495d68fd7e98057dd7b95e31320f049e4fd96901b4560d6931a3a9e6a425f7300fd332edb0fa02d6455b593f61853977be8c6bf3deab45688dbf69cc7989796e21a084b1b427e70addb4f9b363bff96e43358d4c3acff57c72b217919f1783eb835d79e4c972f5917661a2d153f0084c10903a10b7480d6a9b4ecee309b94be7d42053d202986593f9fd6e3b8b7c022bcb0ff3222f82b9ce32891abbbb5b8920fdb7bd53945ddbfaafcc9b16c415186231af17698ed31ea6ec7a2c771cd5d36071d1d9f0a752c4e1777cd30c80430074242368be9543a4b8240aa1204fa85880e3ccc83374423e39fb23b3c6c78e52d3a96e0fca1f5b0d3f4e570b759dc0886cb2a5d320d939793d0324f8adc81237ff48ffc53062b51d0825e914e9146f6fa8d14d1a25fa62d0048328c97de8dadfcc48b120a42c5c84caf4f70fd297374cd52b0b6a814553eefd96bd717693e6cc78ca21145f1966391c5791a65c51f8e91da68614aca686e32eecae5763db3059d8b9dc19f6901df2387e9f44944e258adfce7e79f114204dbd5b147c3aca0690d8f09d90a87ea6c26e123ce38fe60ce0c813f37951ac56da44c2075927f1a165eb210b7f1cf94639415ba831d090257ed99e0c44a0da79b3e1ec0f21590718d454c766cca4eecaa940411eee1423cf547498a094ce41cc099462b8052b5f4e5040fbddee2ab3f629d0d19c0774ec1cf4fd78c7796786cd46c084c9bbc934ec5f984043b8272a9603fe8d7479c21bc84340fb699a30cae0c4e854df90acd8b78b2b4a5b9df4853067dc24ea4c1a7be7577290143ae254551fcd4544e3d94fec612dee24c7471985d01a4b54cf9b17ec46eaa7e763cc051d8b4704ec61300a37e46b2cfc1c1a7a3d53cbba32b2db507a5002a24db6c7673f0d94b1e6f813eddd221bd59807c0b4bdbbb525903ef8bee1c4c08dee4d5b605692f6d7e94a6e8f82b9ce32891abbb3e3c881317ebc2281db65fcfedf95becd87652ac36bf72fb2c6e224b23b6f5be61fa771af9cf2075653a8b82897479dbdb5f596c5e345aa7a5d26a33eb13e642ffd4b48372fe57010e21c983844af9f671558dc4a0ebfa3e3232a7785dd2073165f40d1bdece8b0f7a0774c99d8b58f85a87855da88122ac2f27c36c6219bf5e37e4c171452e96fa0e0d0603b56a9fefefdad86be7233ddc55a163a428d1cd4b49ad960c4325f2a315c6b0f752c805e82829d92781387d2a156771cd293cc512a7b182eb6f6d17a130a9db1bed926c676f72f3d6e0f1eec205a2e0536d009b8b75a5779eb6cd0cbdf12bc3eb3895113ab7322efe9b9e818a9ac296d03c400a990962a7c1c44783f03daa10e971429e4ff42b54a956e0f01e8be0dfaae23704e194491433ee827791502b9f42465b807c34ca4cb9ce72678d9d37f105abac39300273dbc036b6a25072564322deb697cf93c84fcad9394288fe32543e423666faeedc610820de4be37de037f8ade65d9f34095a1dadd915e7aa90fec54a346e7dd845296d2228bca26a936272708e7a5761ca75e8dc61754eca658b719ea39b124964361ac9da4bac8d612de59653cf4c30084c8c8b66b2adde968274b2a47d8d57945eabbb5bf1d44f51bf22ef29ccb282e574d7c94f69af6365ae47f66eaee0d22ebea06bae93409ddea583f26ead90e624c53fe85ae2d70d858fc8ec33cfb1b41c6abfacca0e5f7cea74df732539de28e4410bd56b4554830b4a84b7c9797e2e6019958f5caf1a89cc27ef404c50365f4e94e42d67c0b54bdbbb525903ef8bee1c4c08dee4d5b61bc61881185769cce118d12a55181e306b269a6580de7419fe4651d5bffce059ca849e5682b580261e9e166df94ee9ef85eecefd94321c2bd49bc359e5764270e3d17a6e5375b0d7a6c13842a0fdf3d3b40229744a0b70f9e48ef3f0462164ef6e8aef85f6fe0c604bb4739cdfd52d068d646a83a76ceade80859ecc04846dd4611ab60f82a6c76bb4d05292475c58e0cff1cbe8c856ffa6d94caaad75620d05e4346a22caec4fe15907b9f8d0d01e6509d560b485d6059aabace4f9c5ae65e930316233ef065b7b626df6e70e84b0a5221a04a8575dda7d052ebb7009a4ef1b65940e4cac740e4e20ed7d2e1bc512363d5a4a679d67d7ee25c828383cbf885717e495b3a18e0dc8769b0d71564fca3e8756737840c61570eda4bfd3ada04d2e9ea1ea7c4b89950e199dd0322adc83ed67d8a1686925661bfb69af3841ae3b252aa0ae67308d454e57adecf017a8676277d6f56ca45cac630e87deab3b1d0cd7e62e636029e8bb90896407d68f4ef4dca711dd065d75a85b966c314cb911ce34642e32c27e236642e9e695e779fd25fdf525d9e1139eea11e3e26e6b944b12e13a5bace0359555f226701cde63bddf552b8e1a58d59cac8f16df9dee19f5ff411fbb21809eb0a3d3bc1bd7412607722db36098eeaacf7e8362dbc3b9d9a5acaa5b53942026448b7d9889868579c07aa8dfbc7d1a96bafd3aafb5a9ba2e3ba4737bcfe59649329a84154826b431adf16aacaa9e43a55c32e217160e18bdb98006ce31ec2cb21776d1ef21656241838f33f3ca0d4c9019b27162169119f70bbf4374aadbf7515c30aca8dbb7402fbba37450bf6081eae9065b5685be0d38989120155a70a4772878ce210fcaad611ac54421f867971038858890f4f3f3761c626fb033fbf11c8c0af42b519998dd51715da0716db7fdf45b66632510c2f5fa79eea52c4260c129fb0ec29d2596f5e89fc2908bc62414c776ff1d32911ffec8dc029db2e26cc8a53c7bcd13c1797ca3fb889b47678c9a6fc7e574193cb33255048285506652f90f623b70693755558b63009a57a84e888905c90c39261e1f3701244d38cc6d44fc7a7931f5b7d7bb55b5d94c2a88270a134e01afbefa4483e9aa43c02895a3abf1d2d1")
    }

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        script {
            name = "Set Deckhouse settings"
            scriptContent = """
                cat "%infra.secrets.deckhouse.config%" | > config.yml
                cat "%infra.secrets.deckhouse.resources%" | > resources.yml
            """.trimIndent()
        }
        exec {
            name = "Run Deckhouse with settings"
            path = "dhctl bootstrap --ssh-user=ubuntu --ssh-agent-private-keys=/tmp/.ssh/id_rsa --config=/config.yml --resources=/resources.yml"
            dockerPull = true
            dockerImage = """docker run --pull=always -it -v "${'$'}PWD/config.yml:/config.yml" -v "${'$'}HOME/.ssh/:/tmp/.ssh/" -v "${'$'}PWD/resources.yml:/resources.yml" -v "${'$'}PWD/dhctl-tmp:/tmp/dhctl" registry.deckhouse.io/deckhouse/ce/install :stable bash"""
        }
        script {
            name = "Remove setting after deploy"
            scriptContent = "rm -rf resources.yml config.yml"
        }
    }
})
