package wtf.monsoon.backend

import org.lwjgl.nanovg.NVGColor
import java.awt.Color
import java.util.*

/**
 * @author surge
 * @since 10/02/2023
 */
fun Color.colourise(): NVGColor {
    return NVGColor.calloc()
        .r(this.red / 255f)
        .g(this.green / 255f)
        .b(this.blue / 255f)
        .a(this.alpha / 255f)
}

fun String.format(): String {
    val formatted = StringBuilder()

    var index = 0
    var isFirst = true
    var isNewWord = false

    for (c in this.toCharArray()) {
        if (c == '_') {
            isNewWord = true
            continue
        }

        if (isFirst) {
            if (c.toString().lowercase(Locale.getDefault()) == c.toString()) formatted.append(c) else formatted.append(
                c.toString().uppercase(
                    Locale.getDefault()
                )
            )
            isFirst = false
        } else if (isNewWord) {
            if (c.toString().lowercase(Locale.getDefault()) == c.toString()) formatted.append(" ")
                .append(c) else formatted.append(
                c.toString().uppercase(
                    Locale.getDefault()
                )
            )
            isNewWord = false
        } else {
            formatted.append(c.toString().lowercase(Locale.getDefault()))
        }
        index++
    }

    return formatted.toString()
}