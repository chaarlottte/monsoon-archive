package wtf.monsoon.misc.protection.check.impl

import wtf.monsoon.Wrapper
import wtf.monsoon.misc.protection.check.ProtectionCheck

class ProtectionIntegrityCheck : ProtectionCheck(Trigger.JOIN) {
    override fun check(): Boolean {
        val thread: Thread = Wrapper.monsoon.protectionManager.repetitiveHandlerThread

        if (!thread.isAlive || thread.isInterrupted) {
            // Wrapper.monsoon.protectionManager.crash();
            return true;
        }

        return false;
    }
}