package wtf.monsoon.server

import wtf.monsoon.server.discord.MonsoonBot
import wtf.monsoon.server.manager.impl.CommunityManager
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import kotlin.system.exitProcess

fun main(args: Array<String>) = Main.main(args)

object Main {

    lateinit var server: MonsoonServer
    lateinit var communityManager: CommunityManager
    lateinit var discordBot: MonsoonBot
    const val skipAuth: Boolean = true
    var shouldRun: Boolean = true

    private var threadPool: Executor = Executors.newCachedThreadPool()

    fun main(args: Array<String>) {
        this.server = MonsoonServer()
        this.threadPool.execute { this.server.start() }

        // this.discordBot = MonsoonBot()
        // this.threadPool.execute { this.discordBot.start() }

        this.communityManager = CommunityManager()

        while(shouldRun) {
            Thread.sleep(1000)
        }
        exitProcess(0)
    }
}