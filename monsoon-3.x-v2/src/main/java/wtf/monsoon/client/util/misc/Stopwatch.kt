package wtf.monsoon.client.util.misc

import wtf.monsoon.client.util.Util

class Stopwatch : Util() {
    private var lastMS = System.currentTimeMillis()

    fun reset() {
        lastMS = System.currentTimeMillis()
    }

    var time: Long
        get() = System.currentTimeMillis() - lastMS
        set(time) {
            lastMS = time
        }

    fun hasTimeElapsed(time: Long): Boolean {
        if (System.currentTimeMillis() - lastMS > time) {
            return true
        }
        return false
    }

    fun hasTimeElapsed(time: Double): Boolean {
        return hasTimeElapsed(time.toLong())
    }
}