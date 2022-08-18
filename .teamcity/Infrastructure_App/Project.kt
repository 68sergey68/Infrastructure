package Infrastructure_App

import Infrastructure_App.buildTypes.*
import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.Project

object Project : Project({
    id("Infrastructure_App")
    name = "APP"

    buildType(Infrastructure_App_AppInstall)
})
