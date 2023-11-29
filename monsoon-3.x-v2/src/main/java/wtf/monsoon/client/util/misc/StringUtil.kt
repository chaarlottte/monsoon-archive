package wtf.monsoon.client.util.misc

import com.github.javafaker.Faker
import java.util.*

/**
 * @author Surge
 * @since 30/07/2022
 */
object StringUtil {
    fun formatEnum(enumIn: Enum<*>): String {
        val text = enumIn.toString()
        val formatted = StringBuilder()
        try {
            if (enumIn.javaClass.getMethod("toString").declaringClass.toString().contains("monsoon")) {
                return text
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        if (text.equals("ncp", ignoreCase = true)) return "NCP"
        var index = 0
        var isFirst = true
        var isNewWord = false
        for (c in text.toCharArray()) {
            if (c == '_') {
                isNewWord = true
                continue
            }
            if (isFirst) {
                if (c.toString()
                        .lowercase(Locale.getDefault()) == c.toString()
                ) formatted.append(c) else formatted.append(
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

    fun getRandomString(length: Int): String {
        val characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890"
        val salt = StringBuilder()
        val rnd = Random()
        while (salt.length < length) {
            val index = (rnd.nextFloat() * characters.length).toInt()
            salt.append(characters[index])
        }
        return salt.toString()
    }

    val validUsername: String
        get() {
            val faker = Faker()
            val username = faker.superhero().prefix() + faker.name().firstName() + faker.address().buildingNumber()
            println(username)
            return username
        }
}