package wtf.monsoon.client.util.math

import kotlin.math.*

fun length(x:Float, y:Float) : Float {
    return sqrt(x * x + y * y)
}

fun dot(a:Float, b:Float) : Float {
    return a * b + a * b
}

fun clamp01(a:Float) : Float {
    if (a < 0) return 0f
    else if (a > 1) return 1f
    else return a
}

fun smoothMin(a:Float, b:Float, k:Float) : Float {
    val h = clamp01((b - a + k)/(2 * k))
    return a * h + b * (1 - h) - k * h * (1 - h)
}

/**
 * @param min slider minimal value (such as 0 or -10)
 * @param max slider maximum value (such as 0 or 10)
 * @param step how much should the slider move by (such as 1 or 0.5) [default 1]
 * @param decimals how many decimals should the return be rounded to (such as 2 = 10.12 or 5 = 10.12485) [default 0]
 */
fun slider(min:Float, max:Float, pos:Float, step:Float = 1.0f, decimals:Int = 0) : Float {
    val sliderNoStep = round(pos * (max - min) + min, decimals);
    return round(max(min, min(max, sliderNoStep)) * (1.0f / step)) / (1.0f / step);
}

fun round(a: Float, places:Int) : Float {
    return (a * 10.0.pow(places.toDouble()).toFloat()).roundToInt() / 10.0.pow(places.toDouble()).toFloat()
}

fun randomNumber(max: Double, min: Double): Double {
    return Math.random() * (max - min) + min
}

fun randomNumber(max: Int, min: Int): Int {
    return (Math.random() * (max - min) + min).toInt()
}

fun randomNumber(max: Long, min: Long): Long {
    return (Math.random() * (max - min) + min).toLong()
}

fun getClosestMultipleOfDivisor(valueToRound: Double, divisor: Double): Double {
    val quotient = (valueToRound / divisor).roundToInt().toDouble()
    return divisor * quotient
}