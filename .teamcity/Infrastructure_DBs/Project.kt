package Infrastructure_DBs

import Infrastructure_DBs.buildTypes.*
import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.Project

object Project : Project({
    id("Infrastructure_DBs")
    name = "DBs"

    buildType(Infrastructure_RabbitMQ_RabbitMQInstall)
    buildType(Infrastructure_RabbitMQ_DataBasesInstall)
})
