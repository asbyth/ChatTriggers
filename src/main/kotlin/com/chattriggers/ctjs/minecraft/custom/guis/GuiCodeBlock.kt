package com.chattriggers.ctjs.minecraft.custom.guis

import com.chattriggers.ctjs.minecraft.custom.tileEntities.TileEntityCodeBlock
import com.chattriggers.ctjs.minecraft.libs.ChatLib
import com.chattriggers.ctjs.minecraft.libs.FileLib
import com.chattriggers.ctjs.minecraft.libs.renderer.Renderer
import com.chattriggers.ctjs.minecraft.wrappers.Client
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.GuiTextField
import net.minecraft.client.renderer.GlStateManager
import java.io.File

class GuiCodeBlock(private val tileEntity: TileEntityCodeBlock) : GuiScreen() {
    private val baseFolder = "./config/ChatTriggers/CodeBlocks/"
    private val textField = GuiTextField(
            0, Renderer.getFontRenderer(),
            6 + Renderer.getStringWidth(baseFolder), 4,
            0, 10)

    private var systemTime = Client.getSystemTime()
    private var originalFile: String
    private var isFile: Boolean?
    private var code = ""

    init {
        textField.maxStringLength = 1000
        textField.text = tileEntity.file
        originalFile = tileEntity.file
        if (!originalFile.isFile()) originalFile = ""
        isFile = when {
            textField.text.isFile() -> true
            textField.text.isFolder() -> null
            else -> false
        }

        if (isFile == true) {
            code = FileLib.read(baseFolder + tileEntity.file)!!
        }
    }

    override fun onGuiClosed() {
        super.onGuiClosed()

        textField.text = ChatLib.removeFormatting(textField.text)
        if (textField.text == "") {
            resetFile("No file set!")
        } else if (!textField.text.isFile()) {
            resetFile("${textField.text} is not a file!")
        } else {
            ChatLib.chat("File set to ${textField.text}")
        }
    }

    private fun resetFile(output: String) {
        ChatLib.chat(output)
        tileEntity.file = originalFile
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, button: Int) {
        super.mouseClicked(mouseX, mouseY, button)
        textField.mouseClicked(mouseX, mouseY, 0)
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        super.keyTyped(typedChar, keyCode)
        textField.text = textField.text
        textField.textboxKeyTyped(typedChar, keyCode)

        when {
            textField.text.isFile() -> {
                tileEntity.file = textField.text
                textField.text = textField.text
                isFile = true
            }
            textField.text.isFolder() -> {
                textField.text = textField.text
                isFile = null
            }
            else -> {
                textField.text = textField.text
                isFile = false
            }
        }
    }

    private fun String.isFile(): Boolean {
        val file = File(baseFolder + this)
        return file.exists() && file.isFile
    }

    private fun String.isFolder(): Boolean {
        val file = File(baseFolder + this)
        return file.exists() && file.isDirectory
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.drawScreen(mouseX, mouseY, partialTicks)

        updateTextField()

        GlStateManager.pushMatrix()

        Renderer.drawRect(0xaa000000.toInt(), 0f, 0f, Renderer.screen.getWidth().toFloat(), Renderer.screen.getHeight().toFloat())
        when (isFile) {
            true -> Renderer.drawString(ChatLib.addColor("&a$baseFolder"), 5f, 5f)
            null -> Renderer.drawString(ChatLib.addColor("&e$baseFolder"), 5f, 5f)
            false -> Renderer.drawString(ChatLib.addColor("&c$baseFolder"), 5f, 5f)
        }

        textField.width = Renderer.screen.getWidth() - Renderer.getStringWidth(baseFolder) - 10
        textField.drawTextBox()

        Renderer.drawString(code, 5f, 20f)

        GlStateManager.popMatrix()
    }

    private fun updateTextField() {
        while (systemTime < Client.getSystemTime() + 50) {
            systemTime += 50
            textField.updateCursorCounter()
        }
    }
}