package wtf.monsoon.backend.setting

import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse

/**
 * @author surge
 * @since 09/02/2023
 */
class Bind(var code: Int, var device: Device) {

    fun isPressed(): Boolean = if (code == 0) { false } else Keyboard.isKeyDown(code) && device == Device.KEYBOARD || Mouse.isButtonDown(code) && device == Device.MOUSE
    fun getButtonName(): String = if (device == Device.KEYBOARD) Keyboard.getKeyName(code) else Mouse.getButtonName(code)

    enum class Device {
        KEYBOARD,
        MOUSE
    }

}