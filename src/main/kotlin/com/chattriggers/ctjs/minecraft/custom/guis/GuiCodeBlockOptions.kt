package com.chattriggers.ctjs.minecraft.custom.guis

import com.chattriggers.ctjs.minecraft.custom.tileEntities.TileEntityCodeBlock
import com.chattriggers.ctjs.minecraft.libs.ChatLib
import com.chattriggers.ctjs.minecraft.libs.renderer.Renderer
import com.chattriggers.ctjs.minecraft.objects.gui.GuiHandler
import com.chattriggers.ctjs.minecraft.wrappers.Client
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.GlStateManager

class GuiCodeBlockOptions(private val tileEntity: TileEntityCodeBlock) : GuiScreen() {
    private val backButton = GuiButton(0, 0, 0, "Back")
    private val redstoneButton = GuiButton(1, 0, 20, "")

    init {
        redstoneButton.updateText("Connects To Redstone: ", tileEntity.connectsToRedstone)
    }

    private fun GuiButton.updateText(text: String, option: Boolean) {
        if (option) {
            this.displayString = ChatLib.addColor("$text&aON")
        } else {
            this.displayString = ChatLib.addColor("$text&cOFF")
        }
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, button: Int) {
        if (redstoneButton.mousePressed(Client.getMinecraft(), mouseX, mouseY)) {
            tileEntity.connectsToRedstone = !tileEntity.connectsToRedstone
            redstoneButton.updateText("Connects To Redstone: ", tileEntity.connectsToRedstone)
        }

        if (backButton.mousePressed(Client.getMinecraft(), mouseX, mouseY)) {
            GuiHandler.openGui(GuiCodeBlock(tileEntity))
        }
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        GlStateManager.pushMatrix()

        Renderer.drawRect(0xaa000000.toInt(), 0f, 0f, Renderer.screen.getWidth().toFloat(), Renderer.screen.getHeight().toFloat())

        backButton.xPosition = Renderer.screen.getWidth() - backButton.width
        backButton.yPosition = Renderer.screen.getHeight() - backButton.height
        backButton.drawButton(Client.getMinecraft(), mouseX, mouseY)

        Renderer.translate(Renderer.screen.getWidth() / 2f - Renderer.getStringWidth("Code Block Options") / 2f, 5f)
        //Renderer.scale(1.5f)
        Renderer.drawString("Code Block Options", 0f, 0f)

        redstoneButton.xPosition = Renderer.screen.getWidth() / 2 - redstoneButton.width / 2
        redstoneButton.drawButton(Client.getMinecraft(), mouseX, mouseY)

        GlStateManager.popMatrix()
    }
}