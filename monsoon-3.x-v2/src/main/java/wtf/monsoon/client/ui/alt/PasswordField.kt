package wtf.monsoon.client.ui.alt

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiScreen
import net.minecraft.util.ChatAllowedCharacters


class PasswordField(
    private val fontRenderer: FontRenderer?,
    private val xPos: Int,
    private val yPos: Int,
    private val width: Int,
    private val height: Int
) : Gui() {
    private var text = ""
    private var maxStringLength = 50
    private var cursorCounter = 0
    var enableBackgroundDrawing = true
    private var canLoseFocus = true
    var isFocused = false
    private val isEnabled = true
    private var i = 0
    private var cursorPosition = 0
    var selectionEnd = 0
        private set
    private var enabledColor = 14737632
    private val disabledColor = 7368816
    private var b = true
    fun updateCursorCounter() {
        ++cursorCounter
    }

    fun setText(par1Str: String) {
        if (par1Str.length > maxStringLength) {
            text = par1Str.substring(0, maxStringLength)
        } else {
            text = par1Str
        }
        setCursorPositionEnd()
    }

    fun getText(): String {
        return text.replace(" ".toRegex(), "")
    }

    val selectedtext: String
        get() {
            val var1 = if (cursorPosition < selectionEnd) cursorPosition else selectionEnd
            val var2 = if (cursorPosition < selectionEnd) selectionEnd else cursorPosition
            return text.substring(var1, var2)
        }

    fun writeText(par1Str: String?) {
        var var2 = ""
        val var3 = ChatAllowedCharacters.filterAllowedCharacters(par1Str)
        val var4 = if (cursorPosition < selectionEnd) cursorPosition else selectionEnd
        val var5 = if (cursorPosition < selectionEnd) selectionEnd else cursorPosition
        val var6 = maxStringLength - text.length - (var4 - selectionEnd)
        val var7 = false
        if (text.length > 0) {
            var2 = var2 + text.substring(0, var4)
        }
        val var8: Int
        if (var6 < var3.length) {
            var2 = var2 + var3.substring(0, var6)
            var8 = var6
        } else {
            var2 = var2 + var3
            var8 = var3.length
        }
        if (text.length > 0 && var5 < text.length) {
            var2 = var2 + text.substring(var5)
        }
        text = var2.replace(" ".toRegex(), "")
        cursorPos(var4 - selectionEnd + var8)
    }

    fun func_73779_a(par1: Int) {
        if (text.length != 0) {
            if (selectionEnd != cursorPosition) {
                this.writeText("")
            } else {
                deleteFromCursor(getNthWordFromCursor(par1) - cursorPosition)
            }
        }
    }

    fun deleteFromCursor(par1: Int) {
        if (text.length != 0) {
            if (selectionEnd != cursorPosition) {
                this.writeText("")
            } else {
                val var2 = par1 < 0
                val var3 = if (var2) cursorPosition + par1 else cursorPosition
                val var4 = if (var2) cursorPosition else cursorPosition + par1
                var var5 = ""
                if (var3 >= 0) {
                    var5 = text.substring(0, var3)
                }
                if (var4 < text.length) {
                    var5 = var5 + text.substring(var4)
                }
                text = var5
                if (var2) {
                    cursorPos(par1)
                }
            }
        }
    }

    fun getNthWordFromCursor(par1: Int): Int {
        return getNthWordFromPos(par1, getCursorPosition())
    }

    fun getNthWordFromPos(par1: Int, par2: Int): Int {
        return type(par1, getCursorPosition(), true)
    }

    fun type(par1: Int, par2: Int, par3: Boolean): Int {
        var var4 = par2
        val var5 = par1 < 0
        val var6 = Math.abs(par1)
        var var7 = 0
        while (var7 < var6) {
            if (!var5) {
                val var8 = text.length
                var4 = text.indexOf(32.toChar(), var4)
                if (var4 == -1) {
                    var4 = var8
                } else {
                    while (par3) {
                        if (var4 >= var8) {
                            break
                        }
                        if (text[var4] != ' ') {
                            break
                        }
                        ++var4
                    }
                }
            } else {
                while (par3) {
                    if (var4 <= 0) {
                        break
                    }
                    if (text[var4 - 1] != ' ') {
                        break
                    }
                    --var4
                }
                while (var4 > 0 && text[var4 - 1] != ' ') {
                    --var4
                }
            }
            ++var7
        }
        return var4
    }

    fun cursorPos(par1: Int) {
        setCursorPosition(selectionEnd + par1)
    }

    fun setCursorPosition(par1: Int) {
        cursorPosition = par1
        val var2 = text.length
        if (cursorPosition < 0) {
            cursorPosition = 0
        }
        if (cursorPosition > var2) {
            cursorPosition = var2
        }
        func_73800_i(cursorPosition)
    }

    fun setCursorPositionZero() {
        setCursorPosition(0)
    }

    fun setCursorPositionEnd() {
        setCursorPosition(text.length)
    }

    fun textboxKeyTyped(par1: Char, par2: Int): Boolean {
        return if (!isEnabled || !isFocused) {
            false
        } else when (par1) {
            '\u0001' -> {
                setCursorPositionEnd()
                func_73800_i(0)
                true
            }

            '\u0003' -> {
                GuiScreen.setClipboardString(selectedtext)
                true
            }

            '\u0016' -> {
                this.writeText(GuiScreen.getClipboardString())
                true
            }

            '\u0018' -> {
                GuiScreen.setClipboardString(selectedtext)
                this.writeText("")
                true
            }

            else -> {
                when (par2) {
                    14 -> {
                        if (GuiScreen.isCtrlKeyDown()) {
                            func_73779_a(-1)
                        } else {
                            deleteFromCursor(-1)
                        }
                        true
                    }

                    199 -> {
                        if (GuiScreen.isShiftKeyDown()) {
                            func_73800_i(0)
                        } else {
                            setCursorPositionZero()
                        }
                        true
                    }

                    203 -> {
                        if (GuiScreen.isShiftKeyDown()) {
                            if (GuiScreen.isCtrlKeyDown()) {
                                func_73800_i(getNthWordFromPos(-1, selectionEnd))
                            } else {
                                func_73800_i(selectionEnd - 1)
                            }
                        } else if (GuiScreen.isCtrlKeyDown()) {
                            setCursorPosition(getNthWordFromCursor(-1))
                        } else {
                            cursorPos(-1)
                        }
                        true
                    }

                    205 -> {
                        if (GuiScreen.isShiftKeyDown()) {
                            if (GuiScreen.isCtrlKeyDown()) {
                                func_73800_i(getNthWordFromPos(1, selectionEnd))
                            } else {
                                func_73800_i(selectionEnd + 1)
                            }
                        } else if (GuiScreen.isCtrlKeyDown()) {
                            setCursorPosition(getNthWordFromCursor(1))
                        } else {
                            cursorPos(1)
                        }
                        true
                    }

                    207 -> {
                        if (GuiScreen.isShiftKeyDown()) {
                            func_73800_i(text.length)
                        } else {
                            setCursorPositionEnd()
                        }
                        true
                    }

                    211 -> {
                        if (GuiScreen.isCtrlKeyDown()) {
                            func_73779_a(1)
                        } else {
                            deleteFromCursor(1)
                        }
                        true
                    }

                    else -> {
                        if (ChatAllowedCharacters.isAllowedCharacter(par1)) {
                            this.writeText(Character.toString(par1))
                            return true
                        }
                        false
                    }
                }
            }
        }
    }

    fun mouseClicked(par1: Int, par2: Int, par3: Int) {
        val var4 = par1 >= xPos && par1 < xPos + width && par2 >= yPos && par2 < yPos + height
        if (canLoseFocus) {
            isFocused = (isEnabled && var4)
        }
        if (isFocused && par3 == 0) {
            var var5 = par1 - xPos
            if (enableBackgroundDrawing) {
                var5 -= 4
            }
            val var6 = fontRenderer!!.trimStringToWidth(text.substring(i), width)
            setCursorPosition(fontRenderer.trimStringToWidth(var6, var5).length + i)
        }
    }

    fun drawTextBox() {
        if (func_73778_q()) {
            if (enableBackgroundDrawing) {
                drawRect(xPos - 1, yPos - 1, xPos + width + 1, yPos + height + 1, -6250336)
                drawRect(xPos, yPos, xPos + width, yPos + height, -16777216)
            }
            val var1 = if (isEnabled) enabledColor else disabledColor
            val var2 = cursorPosition - i
            var var3 = selectionEnd - i
            val var4 = fontRenderer!!.trimStringToWidth(text.substring(i), width)
            val var5 = var2 >= 0 && var2 <= var4.length
            val var6 = isFocused && cursorCounter / 6 % 2 == 0 && var5
            val var7 = if (enableBackgroundDrawing) xPos + 4 else xPos
            val var8 = if (enableBackgroundDrawing) yPos + (height - 8) / 2 else yPos
            var var9 = var7
            if (var3 > var4.length) {
                var3 = var4.length
            }
            if (var4.length > 0) {
                if (var5) {
                    var4.substring(0, var2)
                }
                var9 = Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(
                    text.replace("(?s).".toRegex(), "*"),
                    var7.toFloat(),
                    var8.toFloat(),
                    var1
                )
            }
            val var10 = cursorPosition < text.length || text.length >= getMaxStringLength()
            var var11 = var9
            if (!var5) {
                var11 = if (var2 > 0) var7 + width else var7
            } else if (var10) {
                var11 = var9 - 1
                --var9
            }
            if (var4.length > 0 && var5 && var2 < var4.length) {
                Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(
                    var4.substring(var2),
                    var9.toFloat(),
                    var8.toFloat(),
                    var1
                )
            }
            if (var6) {
                if (var10) {
                    drawRect(var11, var8 - 1, var11 + 1, var8 + 1 + fontRenderer.FONT_HEIGHT, -3092272)
                } else {
                    Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(
                        "_",
                        var11.toFloat(),
                        var8.toFloat(),
                        var1
                    )
                }
            }
            if (var3 != var2) {
                val var12 = var7 + fontRenderer.getStringWidth(var4.substring(0, var3))
                drawCursorVertical(var11, var8 - 1, var12 - 1, var8 + 1 + fontRenderer.FONT_HEIGHT)
            }
        }
    }

    private fun drawCursorVertical(par1: Int, par2: Int, par3: Int, par4: Int) {
        var par1 = par1
        var par2 = par2
        var par3 = par3
        var par4 = par4
        if (par1 < par3) {
            val var5 = par1
            par1 = par3
            par3 = var5
        }
        if (par2 < par4) {
            val var5 = par2
            par2 = par4
            par4 = var5
        }
        //final Tessellator var6 = Tessellator.getInstance();
        //final WorldRenderer var7 = var6.getWorldRenderer();
        //GL11.glColor4f(0.0f, 0.0f, 255.0f, 255.0f);
        //GL11.glDisable(3553);
        //GL11.glEnable(3058);
        //GL11.glLogicOp(5387);
        //var7.begin(7, var7.getVertexFormat());
        //var7.pos(par1, par4, 0.0);
        //var7.pos(par3, par4, 0.0);
        //var7.pos(par3, par2, 0.0);
        //var7.pos(par1, par2, 0.0);
        //var7.finishDrawing();
        //GL11.glDisable(3058);
        //GL11.glEnable(3553);


        //final Tessellator var6 = Tessellator.getInstance();
        //final BufferBuilder var7 = var6.getBuffer();
        //GL11.glColor4f(0.0f, 0.0f, 255.0f, 255.0f);
        //GL11.glDisable(3553);
        //GL11.glEnable(3058);
        //GL11.glLogicOp(5387);
        //var7.begin(7, var7.getVertexFormat());
        //var7.pos(par1, par4, 0.0);
        //var7.pos(par3, par4, 0.0);
        //var7.pos(par3, par2, 0.0);
        //var7.pos(par1, par2, 0.0);
        //var7.finishDrawing();
        //GL11.glDisable(3058);
        //GL11.glEnable(3553);
    }

    fun setMaxStringLength(par1: Int) {
        maxStringLength = par1
        if (text.length > par1) {
            text = text.substring(0, par1)
        }
    }

    fun getMaxStringLength(): Int {
        return maxStringLength
    }

    fun getCursorPosition(): Int {
        return cursorPosition
    }

    fun func_73794_g(par1: Int) {
        enabledColor = par1
    }

    /*fun setFocused(par1: Boolean) {
        if (par1 && !isFocused) {
            cursorCounter = 0
        }
        isFocused = par1
    }*/

    /*fun getWidth(): Int {
        return if (enableBackgroundDrawing) width - 8 else width
    }*/

    fun func_73800_i(par1: Int) {
        var par1 = par1
        val var2 = text.length
        if (par1 > var2) {
            par1 = var2
        }
        if (par1 < 0) {
            par1 = 0
        }
        selectionEnd = par1
        if (fontRenderer != null) {
            if (i > var2) {
                i = var2
            }
            val var3 = width
            val var4 = fontRenderer.trimStringToWidth(text.substring(i), var3)
            val var5 = var4.length + i
            if (par1 == i) {
                i -= fontRenderer.trimStringToWidth(text, var3, true).length
            }
            if (par1 > var5) {
                i += par1 - var5
            } else if (par1 <= i) {
                i -= i - par1
            }
            if (i < 0) {
                i = 0
            }
            if (i > var2) {
                i = var2
            }
        }
    }

    fun setCanLoseFocus(par1: Boolean) {
        canLoseFocus = par1
    }

    fun func_73778_q(): Boolean {
        return b
    }

    fun func_73790_e(par1: Boolean) {
        b = par1
    }
}