package wtf.monsoon.client.command

import wtf.monsoon.Wrapper
import wtf.monsoon.backend.command.Command
import kotlin.math.cos
import kotlin.math.sin

class ReloadEvents : Command("Reload", "Reload events. For debugging purposes only") {
    override fun process(args: MutableList<String>) {
        if(Wrapper.developerBuild) {
            try {
                //val reloaded = Wrapper.monsoon.bus.reload()
                //log("Reloaded a total of $reloaded events.", Level.INFO)
            } catch (exception: Exception) {
                exception.printStackTrace()
                log(exception.message!!, Level.ERROR)
            }
        }
    }

    override fun getAliases(): Array<String> {
        return arrayOf(token, "rl")
    }
}