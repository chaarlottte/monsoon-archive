package wtf.monsoon.server.util

class Stopwatch {
    var millis: Long = 0
        private set

    init {
        reset()
    }

    fun finished(delay: Long): Boolean {
        return System.currentTimeMillis() - delay >= millis
    }

    fun reset() {
        millis = System.currentTimeMillis()
    }

    val elapsedTime: Long
        get() = System.currentTimeMillis() - millis
}