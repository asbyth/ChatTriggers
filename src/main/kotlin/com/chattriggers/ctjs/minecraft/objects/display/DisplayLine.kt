package com.chattriggers.ctjs.minecraft.objects.display

import com.chattriggers.ctjs.engine.loader.ILoader
import com.chattriggers.ctjs.minecraft.libs.renderer.Renderer
import com.chattriggers.ctjs.minecraft.libs.renderer.Text
import com.chattriggers.ctjs.minecraft.wrappers.Client
import com.chattriggers.ctjs.triggers.OnRegularTrigger
import com.chattriggers.ctjs.triggers.OnTrigger
import com.chattriggers.ctjs.triggers.TriggerType
import com.chattriggers.ctjs.utils.kotlin.External
import com.chattriggers.ctjs.utils.kotlin.NotAbstract
import net.minecraft.util.math.Vec2f
import org.mozilla.javascript.NativeObject

@External
@NotAbstract
abstract class DisplayLine {
    private lateinit var text: Text
    private var textWidth = 0f
    private var textColor: Long? = null
    private var backgroundColor: Long? = null

    private var background: DisplayHandler.Background? = null
    private var align: DisplayHandler.Align? = null

    private var onClicked: OnTrigger? = null
    private var onHovered: OnTrigger? = null
    private var onDragged: OnTrigger? = null

    private var mouseState = HashMap<Int, Boolean>()
    private var draggedState = HashMap<Int, Vec2f>()

    constructor(text: String) {
        setText(text)
        for (i in 0..2) this.mouseState[i] = false
    }

    constructor(text: String, config: NativeObject) {
        setText(text)
        for (i in 0..2) this.mouseState[i] = false

        this.textColor = config.getOption("textColor", null)?.toLong()
        this.backgroundColor = config.getOption("backgroundColor", null)?.toLong()

        this.setAlign(config.getOption("align", null))
        this.setBackground(config.getOption("background", null))
    }

    private fun NativeObject?.getOption(key: String, default: Any?): String? {
        if (this == null) return default?.toString()
        return this.getOrDefault(key, default).toString()
    }

    fun getText(): Text = this.text
    fun setText(text: String) = apply {
        this.text = Text(text)
        this.textWidth = Renderer.getStringWidth(text) * this.text.getScale()
    }

    fun getTextColor(): Long? = this.textColor
    fun setTextColor(color: Long) = apply {
        this.textColor = color
    }

    fun getTextWidth(): Float = this.textWidth

    fun setShadow(shadow: Boolean) = apply { this.text.setShadow(shadow) }

    fun setScale(scale: Float) = apply {
        this.text.setScale(scale)
        this.textWidth = Renderer.getStringWidth(text.getString()) * scale
    }

    fun getAlign(): DisplayHandler.Align? = this.align
    fun setAlign(align: Any?) = apply {
        this.align = when (align) {
            is String -> DisplayHandler.Align.valueOf(align.toUpperCase())
            is DisplayHandler.Align -> align
            else -> null
        }
    }

    fun getBackground(): DisplayHandler.Background? = this.background
    fun setBackground(background: Any?) = apply {
        this.background = when (background) {
            is String -> DisplayHandler.Background.valueOf(background.toUpperCase().replace(" ", "_"))
            is DisplayHandler.Background -> background
            else -> null
        }
    }

    fun getBackgroundColor(): Long? = this.backgroundColor
    fun setBackgroundColor(color: Long) = apply {
        this.backgroundColor = color
    }

    fun registerClicked(method: Any) = run {
        this.onClicked = OnRegularTrigger(method, TriggerType.OTHER, getLoader())
        this.onClicked
    }

    fun registerHovered(method: Any) = run {
        this.onHovered = OnRegularTrigger(method, TriggerType.OTHER, getLoader())
        this.onHovered
    }

    fun registerDragged(method: Any) = run {
        this.onDragged = OnRegularTrigger(method, TriggerType.OTHER, getLoader())
        this.onDragged
    }

    enum class MouseButton(val id: Int) {
        LEFT(0),
        MIDDLE(1),
        RIGHT(2);

        fun isDown() = when (id) {
            0 -> Client.mc.mouseHelper.isLeftDown
            1 -> Client.mc.mouseHelper.isMiddleDown
            2 -> Client.mc.mouseHelper.isRightDown
            else -> throw Error("Not possible")
        }

        companion object {
            fun fromId(id: Int) = values().first { it.id == id }
        }
    }

    private fun handleInput(x: Float, y: Float, width: Float, height: Float) {
        for (button in 0..2) handleDragged(button)

        if (Client.getMouseX() > x && Client.getMouseX() < x + width
            && Client.getMouseY() > y && Client.getMouseY() < y + height
        ) {
            handleHovered()

            for (button in 0..2) {
                val mouseButton = MouseButton.fromId(button)
                if (mouseButton.isDown() == mouseState[button]) continue
                handleClicked(mouseButton)
                mouseState[button] = mouseButton.isDown()
                if (mouseButton.isDown())
                    draggedState[button] = Vec2f(Client.getMouseX(), Client.getMouseY())
            }
        }

        for (button in 0..2) {
            val mouseButton = MouseButton.fromId(button)
            if (mouseButton.isDown() || !draggedState.containsKey(button)) continue
            draggedState.remove(button)
        }
    }

    private fun handleClicked(button: MouseButton) {
        this.onClicked?.trigger(
            Client.getMouseX(),
            Client.getMouseY(),
            button.id,
            button.isDown()
        )
    }

    private fun handleHovered() {
        this.onHovered?.trigger(
            Client.getMouseX(),
            Client.getMouseY()
        )
    }

    private fun handleDragged(button: Int) {
        if (this.onDragged == null) return

        if (!this.draggedState.containsKey(button))
            return

        this.onDragged?.trigger(
            Client.getMouseX() - this.draggedState.getValue(button).x,
            Client.getMouseY() - this.draggedState.getValue(button).y,
            Client.getMouseX(),
            Client.getMouseY(),
            button
        )

        this.draggedState[button] = Vec2f(Client.getMouseX(), Client.getMouseY())
    }

    private fun drawFullBG(
        bg: DisplayHandler.Background,
        color: Long,
        x: Float,
        y: Float,
        width: Float,
        height: Float
    ) {
        if (bg === DisplayHandler.Background.FULL)
            Renderer.drawRect(color, x, y, width, height)
    }

    private fun drawPerLineBG(
        bg: DisplayHandler.Background,
        color: Long,
        x: Float,
        y: Float,
        width: Float,
        height: Float
    ) {
        if (bg === DisplayHandler.Background.PER_LINE)
            Renderer.drawRect(color, x, y, width, height)
    }

    fun drawLeft(
        x: Float,
        y: Float,
        maxWidth: Float,
        background: DisplayHandler.Background,
        backgroundColor: Long,
        textColor: Long
    ) {
        val bg = this.background ?: background
        val bgColor = this.backgroundColor ?: backgroundColor
        val textCol = this.textColor ?: textColor

        // full background
        drawFullBG(bg, bgColor, x - 1, y - 1, maxWidth + 2, 10 * this.text.getScale())

        // blank line
        if ("" == this.text.getString()) return

        // text and per line background
        var xOff = x

        if (this.align === DisplayHandler.Align.RIGHT) {
            xOff = x - this.textWidth + maxWidth
        } else if (this.align === DisplayHandler.Align.CENTER) {
            xOff = x - this.textWidth / 2 + maxWidth / 2
        }

        drawPerLineBG(bg, bgColor, xOff - 1, y - 1, (this.textWidth + 2), 10 * this.text.getScale())
        this.text.setX(xOff).setY(y).setColor(textCol).draw()

        handleInput(xOff - 1, y - 1, (this.textWidth + 2), 10 * this.text.getScale())
    }

    fun drawRight(
        x: Float,
        y: Float,
        maxWidth: Float,
        background: DisplayHandler.Background,
        backgroundColor: Long,
        textColor: Long
    ) {
        val bg = this.background ?: background
        val bgColor = this.backgroundColor ?: backgroundColor
        val textCol = this.textColor ?: textColor

        // full background
        drawFullBG(bg, bgColor, x - maxWidth - 1f, y - 1, maxWidth + 2, 10 * this.text.getScale())

        // blank line
        if ("" == this.text.getString()) return

        // text and per line background\
        var xOff = x - this.textWidth

        if (this.align === DisplayHandler.Align.LEFT) {
            xOff = x - maxWidth
        } else if (this.align === DisplayHandler.Align.CENTER) {
            xOff = x - (this.textWidth / 2) - maxWidth / 2
        }

        drawPerLineBG(bg, bgColor, xOff - 1, y - 1, (this.textWidth + 2), 10 * this.text.getScale())
        this.text.setX(xOff).setY(y).setColor(textCol).draw()

        handleInput(xOff - 1, y - 1, (this.textWidth + 2), 10 * this.text.getScale())
    }

    fun drawCenter(
        x: Float,
        y: Float,
        maxWidth: Float,
        background: DisplayHandler.Background,
        backgroundColor: Long,
        textColor: Long
    ) {
        val bg = this.background ?: background
        val bgColor = this.backgroundColor ?: backgroundColor
        val textCol = this.textColor ?: textColor

        // full background
        drawFullBG(bg, bgColor, x - maxWidth / 2 - 1f, y - 1, maxWidth + 2, 10 * this.text.getScale())

        // blank line
        if ("" == this.text.getString()) return

        // text and per line background
        var xOff = x - this.textWidth / 2

        if (this.align === DisplayHandler.Align.LEFT) {
            xOff = x - maxWidth / 2
        } else if (this.align === DisplayHandler.Align.RIGHT) {
            xOff = x + maxWidth / 2 - this.textWidth
        }

        drawPerLineBG(bg, bgColor, xOff - 1, y - 1, (this.textWidth + 2), 10 * this.text.getScale())
        this.text.setX(xOff).setY(y).setColor(textCol).draw()

        handleInput(xOff - 1, y - 1, (this.textWidth + 2), 10 * this.text.getScale())
    }

    internal abstract fun getLoader(): ILoader

    override fun toString() =
        "DisplayLine{" +
                "text=$text, textColor=$textColor, align=$align, " +
                "background=$background, backgroundColor=$backgroundColor, " +
                "}"
}