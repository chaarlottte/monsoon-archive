package wtf.monsoon.misc.protection.check

abstract class ProtectionCheck(val trigger: Trigger) {

    abstract fun check(): Boolean

    enum class Trigger {
        INITIALIZE,
        REPETITIVE,
        POST_INITIALIZE,
        JOIN
    }
}