package wtf.monsoon.client.ui.alt

import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.GuiTextField
import net.minecraft.util.EnumChatFormatting
import org.lwjgl.input.Keyboard
import wtf.monsoon.Wrapper
import java.io.IOException
import java.util.*


open class GuiAltLogin(private val previousScreen: GuiScreen) : GuiScreen() {

    private var password: PasswordField? = null
    private var thread: AltLoginThread? = null
    private var username: GuiTextField? = null
    private var mode: Int = 0

    override fun actionPerformed(button: GuiButton) {
        val usernameS: String
        val passwordS: String
        when (button.id) {
            1 -> {
                mc.displayGuiScreen(previousScreen)
            }

            0 -> {
                if (username!!.text.contains(":")) {
                    val combo = username!!.text.split(":".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray()
                    usernameS = combo[0]
                    passwordS = combo[1]
                } else {
                    usernameS = username!!.text
                    passwordS = password!!.getText()
                }
                thread = AltLoginThread(usernameS, passwordS, mode)
                thread!!.start()
            }

            201 -> {
                thread = AltLoginThread("Monsoon_$saltString", "", 0)
                thread!!.start()
            }

            57840 -> {
                if(mode < 1) mode++
                else mode = 0
            }
        }
    }

    override fun drawScreen(x2: Int, y2: Int, z2: Float) {
        drawDefaultBackground()
        username!!.drawTextBox()
        password!!.drawTextBox()
        mc.fontRendererObj.drawString("Alt Login", width / 2, 20, -1)
        mc.fontRendererObj.drawString(
            if (thread == null) EnumChatFormatting.GRAY.toString() + "Idle..." else thread!!.status,
            width / 2,
            29,
            -1
        )
        if (username!!.text.isEmpty()) {
            mc.fontRendererObj.drawString("Username / E-Mail / Name / Combo", width / 2 - 96, 66, -7829368)
        }
        if (password!!.getText().isEmpty()) {
            mc.fontRendererObj.drawString("Password", width / 2 - 96, 106, -7829368)
        }
        super.drawScreen(x2, y2, z2)
    }

    override fun initGui() {
        val var3 = height / 4 + 24
        buttonList.add(GuiButton(0, width / 2 - 100, var3 + 72 + 12, "Login"))
        buttonList.add(GuiButton(201, width / 2 - 100, var3 + 72 + 12 + 48, "Generate cracked"))
        buttonList.add(
            GuiButton(
                57840,
                width / 2 - 100,
                var3 + 72 + 12 + 72,
                if (mode == 0) "Type: Microsoft" else "Type: Cookie"
            )
        )
        buttonList.add(GuiButton(1, width / 2 - 100, var3 + 72 + 12 + 96, "Back"))
        username = GuiTextField(var3, mc.fontRendererObj, width / 2 - 100, 60, 200, 20)
        password = PasswordField(mc.fontRendererObj, width / 2 - 100, 100, 200, 20)
        username!!.isFocused = true
        Keyboard.enableRepeatEvents(true)
    }

    override fun keyTyped(character: Char, key: Int) {
        try {
            super.keyTyped(character, key)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        if (character == '\t') {
            if (!username!!.isFocused && !password!!.isFocused) {
                username!!.isFocused = true
            } else {
                username!!.isFocused = password!!.isFocused
                password!!.isFocused = (!username!!.isFocused)
            }
        }
        if (character == '\r') {
            actionPerformed(buttonList[0] as GuiButton)
        }
        username!!.textboxKeyTyped(character, key)
        password!!.textboxKeyTyped(character, key)
    }

    override fun mouseClicked(x2: Int, y2: Int, button: Int) {
        try {
            super.mouseClicked(x2, y2, button)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        username!!.mouseClicked(x2, y2, button)
        password!!.mouseClicked(x2, y2, button)
    }

    override fun onGuiClosed() {
        Keyboard.enableRepeatEvents(false)
    }

    override fun updateScreen() {
        username!!.updateCursorCounter()
        password!!.updateCursorCounter()
    }

    private val saltString: String
         get() {
             val SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890abcdefghijklmnopqrstuvwxyz"
            val salt = StringBuilder()
            val rnd = Random()
            while (salt.length < 6) { // length of the random string.
                val index = (rnd.nextFloat() * SALTCHARS.length).toInt()
                salt.append(SALTCHARS[index])
            }
            return salt.toString()
        }
}
