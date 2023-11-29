package wtf.monsoon.misc.protection

import wtf.monsoon.misc.protection.check.ProtectionCheck
import java.lang.management.ManagementFactory
import sun.misc.Unsafe
import wtf.monsoon.misc.protection.check.impl.*
import java.lang.reflect.Field


class ProtectionManager {

    lateinit var jvmArguments: List<String>
    lateinit var checks: Array<ProtectionCheck>
    lateinit var repetitiveHandlerThread: Thread
    var initialized = false

    fun init() {
        try {
            jvmArguments = ManagementFactory.getRuntimeMXBean().inputArguments
            checks = arrayOf(
                MaliciousArgumentsCheck(),
                MaliciousClassCheck(),
                RunningJVMsCheck(),
                TamperedHostsFileCheck(),
                ProtectionIntegrityCheck(),
            )
            this.run(ProtectionCheck.Trigger.INITIALIZE)
            repetitiveHandlerThread = Thread {
                while (true) {
                    try {
                        run(ProtectionCheck.Trigger.REPETITIVE)
                        Thread.sleep(1000L)
                    } catch (throwable: Throwable) {
                        throwable.printStackTrace()
                        crash()
                    }
                }
            }
            repetitiveHandlerThread.start()

            this.run(ProtectionCheck.Trigger.POST_INITIALIZE)
        } catch (ignored: Throwable) {
            ignored.printStackTrace()
            crash()
        }
    }

    fun run(trigger: ProtectionCheck.Trigger) {
        try {
            for (module in checks) {
                if (module.trigger === trigger) {
                    if (module.check()) {
                        println(module.javaClass.name)
                        hang()
                    }
                }
            }
        } catch (ignored: Throwable) {
            ignored.printStackTrace()
            crash()
        }
    }

    private fun hang() {
        while (true);
    }

    fun crash() {
        while (true) {
            try {
                val unsafeField: Field = Unsafe::class.java.getDeclaredField("theUnsafe")
                unsafeField.isAccessible = true
                val unsafe: Unsafe = unsafeField.get(null) as Unsafe
                var address: Long = 0
                while (true) {
                    unsafe.setMemory(address, Long.MAX_VALUE, Byte.MIN_VALUE)
                    ++address
                }
            } catch (t: Throwable) {
                crash()
            }
            hang()
        }
    }

}