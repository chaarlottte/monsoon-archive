package wtf.monsoon.misc.protection.check.impl

import com.sun.tools.attach.VirtualMachine
import wtf.monsoon.misc.protection.check.ProtectionCheck
import java.util.*

class RunningJVMsCheck : ProtectionCheck(Trigger.JOIN) {

    private val badProcesses = arrayOf(
        "dump",
        "packetlog",
        "logger",
        "recaf",
        "jbyte",
        "bytecode",
        "decompile",
        "log"
    )

    override fun check(): Boolean {
        return VirtualMachine.list().stream().anyMatch { descriptor ->
            val name: String = descriptor.displayName().lowercase(Locale.getDefault()).trim()
            Arrays.stream(badProcesses).anyMatch { s: CharSequence? -> name.contains(s!!) }
        }
    }

}