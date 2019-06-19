package com.chattriggers.ctjs.engine.module

import com.chattriggers.ctjs.engine.ModuleManager
import com.chattriggers.ctjs.minecraft.libs.ChatLib
import com.chattriggers.ctjs.minecraft.libs.renderer.Renderer
import com.chattriggers.ctjs.minecraft.libs.renderer.Text
import com.chattriggers.ctjs.minecraft.wrappers.Player
import net.minecraft.client.gui.GuiScreen
import org.lwjgl.input.Mouse

object ModulesGui : GuiScreen() {
    private val window = object {
        var title = Text("Modules").setScale(2.0).setShadow(true)
        var exit = Text(ChatLib.addColor("&cx")).setScale(2.0)
        var height = 0.0
        var scroll = 0.0
    }

    override fun doesGuiPauseGame() = false

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.drawScreen(mouseX, mouseY, partialTicks)

        val middle = Renderer.screen.getWidth() / 2.0
        var width = Renderer.screen.getWidth() - 100.0
        if (width > 500) width = 500.0

        Renderer.drawRect(0x50000000, 0.0, 0.0, Renderer.screen.getWidth().toDouble(), Renderer.screen.getHeight().toDouble())

        if (-window.scroll > window.height - Renderer.screen.getHeight() + 20)
            window.scroll = -window.height + Renderer.screen.getHeight() - 20
        if (-window.scroll < 0) window.scroll = 0.0

        if (-window.scroll > 0) {
            Renderer.drawRect(0xaa000000, Renderer.screen.getWidth() - 20.0, Renderer.screen.getHeight() - 20.0, 20.0, 20.0)
            Renderer.drawString("^", Renderer.screen.getWidth() - 12.0, Renderer.screen.getHeight() - 12.0)
        }

        Renderer.drawRect(0x50000000, middle - width / 2.0, window.scroll + 95.0, width, window.height - 90)

        Renderer.drawRect(0xaa000000, middle - width / 2.0, window.scroll + 95.0, width, 25.0)
        window.title.draw(middle - width / 2.0 + 5, window.scroll + 100.0)
        window.exit.setString(ChatLib.addColor("&cx")).draw(middle + width / 2.0 - 17, window.scroll + 99.0)

        window.height = 125.0
        ModuleManager.cachedModules.forEach {
            window.height += it.draw(middle - width / 2.0, window.scroll + window.height, width)
        }
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, button: Int) {
        super.mouseClicked(mouseX, mouseY, button)

        var width = Renderer.screen.getWidth() - 100.0
        if (width > 500) width = 500.0

        if (mouseX > Renderer.screen.getWidth() - 20 && mouseY > Renderer.screen.getHeight() - 20) {
            window.scroll = 0.0
            return
        }

        if (mouseX > Renderer.screen.getWidth() / 2.0 + width / 2.0 - 25 && mouseX < Renderer.screen.getWidth() / 2.0 + width / 2.0
        && mouseY > window.scroll + 95 && mouseY < window.scroll + 120) {
            Player.getPlayer()?.closeScreen()
            return
        }

        ModuleManager.cachedModules.forEach {
            it.click(mouseX, mouseY, width)
        }
    }

    override fun handleMouseInput() {
        super.handleMouseInput()

        val i = Mouse.getEventDWheel()
        window.scroll += i / 10
    }
}