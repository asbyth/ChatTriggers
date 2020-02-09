package com.chattriggers.ctjs.minecraft.wrappers

import com.chattriggers.ctjs.minecraft.libs.ChatLib
import com.chattriggers.ctjs.minecraft.libs.renderer.Renderer
import com.chattriggers.ctjs.minecraft.objects.KeyBind
import com.chattriggers.ctjs.utils.kotlin.External
import com.mojang.blaze3d.systems.RenderSystem
import net.java.games.input.Keyboard
import net.minecraft.client.MainWindow
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screen.ChatScreen
import net.minecraft.client.gui.screen.Screen
import net.minecraft.network.INetHandler
import net.minecraft.network.IPacket
import kotlin.math.roundToInt

@External
object Client {
    @JvmStatic
    val settings = Settings

    internal val mc = Minecraft.getInstance()

    /**
     * Gets Minecraft's Minecraft object
     *
     * @return The Minecraft object
     */
    @JvmStatic
    fun getMinecraft(): Minecraft = mc

    /**
     * Gets Minecraft's NetHandlerPlayClient object
     *
     * @return The NetHandlerPlayClient object
     */
    @JvmStatic
    fun getConnection() = mc.connection

    /**
     * Gets the Minecraft GuiNewChat object for the chat gui
     *
     * @return The GuiNewChat object for the chat gui
     */
    @JvmStatic
    fun getChatGUI() = mc.ingameGUI?.chatGUI

    @JvmStatic
    fun isInChat(): Boolean = mc.currentScreen is ChatScreen

    @JvmStatic
    fun getTabGui() = mc.ingameGUI?.tabList

    @JvmStatic
    fun isInTab(): Boolean = mc.gameSettings.keyBindPlayerList.isKeyDown

    // /**
    //  * Gets whether or not the Minecraft window is active
    //  * and in the foreground of the user's screen.
    //  *
    //  * @return true if the game is active, false otherwise
    //  */
    // @JvmStatic
    // fun isTabbedIn(): Boolean = Display.isActive()

    // @JvmStatic
    // fun isControlDown(): Boolean =
    //
    // @JvmStatic
    // fun isShiftDown(): Boolean = GuiScreen.isShiftKeyDown()
    //
    // @JvmStatic
    // fun isAltDown(): Boolean = GuiScreen.isAltKeyDown()

    /**
     * Get the [KeyBind] from an already existing
     * Minecraft KeyBinding, otherwise, returns null.
     *
     * @param keyCode the keycode to search for, see Keyboard below. Ex. Keyboard.KEY_A
     * @return the [KeyBind] from a Minecraft KeyBinding, or null if one doesn't exist
     * @see [Keyboard](http://legacy.lwjgl.org/javadoc/org/lwjgl/input/Keyboard.html)
     */
    @JvmStatic
    fun getKeyBindFromKey(keyCode: Int): KeyBind? {
        return mc.gameSettings.keyBindings
            .firstOrNull { it.matchesKey(keyCode, keyCode) }
            ?.let { KeyBind(it) }
    }

    /**
     * Get the [KeyBind] from an already existing
     * Minecraft KeyBinding, else, return a new one.
     *
     * @param keyCode the keycode to search for, see Keyboard below. Ex. Keyboard.KEY_A
     * @return the [KeyBind] from a Minecraft KeyBinding, or null if one doesn't exist
     * @see [Keyboard](http://legacy.lwjgl.org/javadoc/org/lwjgl/input/Keyboard.html)
     */
    @JvmOverloads
    @JvmStatic
    fun getKeyBindFromKey(keyCode: Int, description: String, category: String  = "ChatTriggers"): KeyBind {
        return mc.gameSettings.keyBindings
                .firstOrNull { it.matchesKey(keyCode, keyCode) }
                ?.let { KeyBind(it) }
                ?: KeyBind(description, keyCode, category)
    }

    /**
     * Get the [KeyBind] from an already existing
     * Minecraft KeyBinding, else, null.
     *
     * @param description the key binding's original description
     * @return the key bind, or null if one doesn't exist
     */
    @JvmStatic
    fun getKeyBindFromDescription(description: String): KeyBind? {
        return mc.gameSettings.keyBindings
            .firstOrNull { it.keyDescription == description }
            ?.let { KeyBind(it) }
    }

    // @JvmStatic
    // fun getFPS(): Int = Minecraft.getDebugFPS()

    @JvmStatic
    fun getVersion(): String = mc.version

    @JvmStatic
    fun getMaxMemory(): Long = Runtime.getRuntime().maxMemory()

    @JvmStatic
    fun getTotalMemory(): Long = Runtime.getRuntime().totalMemory()

    @JvmStatic
    fun getFreeMemory(): Long = Runtime.getRuntime().freeMemory()

    @JvmStatic
    fun getMemoryUsage(): Int = ((getTotalMemory() - getFreeMemory()) * 100 / getMaxMemory().toFloat()).roundToInt()

    // TODO: MC system time?
    @JvmStatic
    fun getSystemTime(): Long = System.nanoTime()

    @JvmStatic
    fun getMouseX(): Float {
        val mx = mc.mouseHelper.mouseX.toFloat()
        val rw = Renderer.screen.getWidth().toFloat()
        val dw = mc.mainWindow.width.toFloat()
        return mx * rw / dw
    }

    @JvmStatic
    fun getMouseY(): Float {
        val my = mc.mouseHelper.mouseY.toFloat()
        val rh = Renderer.screen.getHeight().toFloat()
        val dh = mc.mainWindow.height.toFloat()
        return rh - my * rh / dh - 1f
    }

    @JvmStatic
    fun isInGui(): Boolean = currentGui.get() != null

    /**
     * Gets the chat message currently typed into the chat gui.
     *
     * @return A blank string if the gui isn't open, otherwise, the message
     */
    @JvmStatic
    fun getCurrentChatMessage(): String {
        return if (isInChat()) {
            val chatGui = mc.currentScreen as ChatScreen
            chatGui.inputField.text
        } else ""
    }

    /**
     * Sets the current chat message, if the chat gui is not open, one will be opened.
     *
     * @param message the message to put in the chat text box.
     */
    @JvmStatic
    fun setCurrentChatMessage(message: String) {
        if (isInChat()) {
            val chatGui = mc.currentScreen as ChatScreen
            chatGui.inputField.text = message
        } else mc.displayGuiScreen(ChatScreen(message))
    }

    @JvmStatic
    fun <T : INetHandler> sendPacket(packet: IPacket<T>) {
        mc.connection?.sendPacket(packet)
    }

    /**
     * Display a title.
     *
     * @param title    title text
     * @param subtitle subtitle text
     * @param fadeIn   time to fade in
     * @param time     time to stay on screen
     * @param fadeOut  time to fade out
     */
    @JvmStatic
    fun showTitle(title: String, subtitle: String, fadeIn: Int, time: Int, fadeOut: Int) {
        val gui = Client.mc.ingameGUI
        gui.displayTitle(ChatLib.addColor(title), null, fadeIn, time, fadeOut)
        gui.displayTitle(null, ChatLib.addColor(subtitle), fadeIn, time, fadeOut)
        gui.displayTitle(null, null, fadeIn, time, fadeOut)
    }

    object currentGui {
        /**
         * Gets the Java class name of the currently open gui, for example, "GuiChest"
         *
         * @return the class name of the current gui
         */
        @JvmStatic
        fun getClassName(): String = get()?.javaClass?.simpleName ?: "null"

        /**
         * Gets the Minecraft gui class that is currently open
         *
         * @return the Minecraft gui
         */
        @JvmStatic
        fun get(): Screen? = mc.currentScreen

        /**
         * Closes the currently open gui
         */
        @JvmStatic
        fun close() {
            Player.getPlayer()?.closeScreen()
        }
    }

    object camera {
        @JvmStatic
        fun getX(): Double = mc.renderManager.info.projectedView.x

        @JvmStatic
        fun getY(): Double = mc.renderManager.info.projectedView.y

        @JvmStatic
        fun getZ(): Double = mc.renderManager.info.projectedView.z
    }
}
