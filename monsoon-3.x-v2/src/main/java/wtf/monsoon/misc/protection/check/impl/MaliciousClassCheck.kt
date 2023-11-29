package wtf.monsoon.misc.protection.check.impl

import wtf.monsoon.Wrapper
import wtf.monsoon.misc.protection.check.ProtectionCheck


class MaliciousClassCheck : ProtectionCheck(Trigger.INITIALIZE) {

    private val badClasses = arrayOf(
        "sun.instrument.InstrumentationImpl",
        "java.lang.instrument.Instrumentation",
        "java.lang.instrument.ClassDefinition",
        "java.lang.instrument.ClassFileTransformer",
        "java.lang.instrument.IllegalClassFormatException",
        "java.lang.instrument.UnmodifiableClassException"
    )

    override fun check(): Boolean {
        if(Wrapper.developerBuild) return false
        val classLoader = ClassLoader.getSystemClassLoader()
        for (className in badClasses) {
            try {
                Class.forName(className, false, classLoader)
                println(className)
                return true // Found a bad class
            } catch (e: ClassNotFoundException) {
                // Class not found, continue checking
            }
        }
        return false
    }

}