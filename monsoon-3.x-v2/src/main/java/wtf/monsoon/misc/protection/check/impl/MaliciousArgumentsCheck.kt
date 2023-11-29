package wtf.monsoon.misc.protection.check.impl

import wtf.monsoon.Wrapper
import wtf.monsoon.misc.protection.check.ProtectionCheck
import java.util.*

class MaliciousArgumentsCheck : ProtectionCheck(Trigger.INITIALIZE) {

    private val badArgs = arrayOf(
        "javaagent",
        "agentlib",
        "Xdebug",
        "Xrunjdwp:",
        "noverify",
    )

    override fun check(): Boolean {
        val arguments: List<String> = Wrapper.monsoon.protectionManager.jvmArguments

        if(Wrapper.developerBuild) return false

        return arguments.stream().anyMatch { a ->
            Arrays.stream(badArgs).anyMatch { s: CharSequence? -> a.contains(s!!) }
        }
    }

}