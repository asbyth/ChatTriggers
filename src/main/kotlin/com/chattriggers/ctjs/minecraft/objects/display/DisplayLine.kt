package com.chattriggers.ctjs.minecraft.objects.display

import com.chattriggers.ctjs.engine.Lang
import com.chattriggers.ctjs.engine.Loader
import com.chattriggers.ctjs.minecraft.libs.renderer.Renderer
import com.chattriggers.ctjs.minecraft.libs.renderer.Text
import com.chattriggers.ctjs.minecraft.wrappers.Client
import com.chattriggers.ctjs.triggers.OnRegularTrigger
import com.chattriggers.ctjs.triggers.OnTrigger
import com.chattriggers.ctjs.triggers.TriggerType
import com.chattriggers.ctjs.utils.kotlin.External
import com.chattriggers.ctjs.utils.kotlin.NotAbstract
import org.graalvm.polyglot.Value
import org.lwjgl.input.Mouse
import javax.vecmath.Vector2d

@External
@NotAbstract
open class DisplayLine(text: String) {
    private lateinit var text: Text
    private var textWidth = 0f
    private var textColor: Int? = null
    private var backgroundColor: Int? = null

    private var background: DisplayHandler.Background? = null
    private var align: DisplayHandler.Align? = null

    private var onClicked: OnTrigger? = null
    private var onHovered: OnTrigger? = null
    private var onDragged: OnTrigger? = null

    private var mouseState = HashMap<Int, Boolean>()
    private var draggedState = HashMap<Int, Vector2d>()

    lateinit var lang: Lang

    init {
        setText(text)
        for (i in 0..5) this.mouseState[i] = false
    }

    constructor(text: String, config: Value) : this(text) {
        if (!config.hasMembers()) return

        this.textColor = config.getMember("text_color")?.asInt()
        this.backgroundColor = config.getMember("background_color")?.asInt()

        run {
            val align = config.getMember("align") ?: return@run

            this.setAlign(if (align.isString) align.asString() else align.asHostObject())
        }

        run {
            val background = config.getMember("background") ?: return@run

            this.setBackground(if (background.isString) background.asString() else background.asHostObject())
        }
    }

    fun getText(): Text = this.text
    fun setText(text: String) = apply {
        this.text = Text(text)
        this.textWidth = Renderer.getStringWidth(text) * this.text.getScale()
    }

    fun setTextColor(color: Int) = apply {
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

    fun setBackgroundColor(color: Int) = apply {
        this.backgroundColor = color
    }

    fun registerClicked(method: Value) = run {
        this.onClicked = OnRegularTrigger(method, TriggerType.OTHER, lang)
        this.onClicked
    }
    fun registerHovered(method: Value) = run {
        this.onHovered = OnRegularTrigger(method, TriggerType.OTHER, lang)
        this.onHovered
    }
    fun registerDragged(method: Value) = run {
        this.onDragged = OnRegularTrigger(method, TriggerType.OTHER, lang)
        this.onDragged
    }

    private fun handleInput(x: Float, y: Float, width: Float, height: Float) {
        if (!Mouse.isCreated()) return

        for (button in 0..5) handleDragged(button)

        if (Client.getMouseX() > x && Client.getMouseX() < x + width
                && Client.getMouseY() > y && Client.getMouseY() < y + height) {
            handleHovered()

            for (button in 0..5) {
                if (Mouse.isButtonDown(button) == this.mouseState[button]) continue
                handleClicked(button)
                this.mouseState.put(button, Mouse.isButtonDown(button))
                if (Mouse.isButtonDown(button))
                    this.draggedState.put(button, Vector2d(Client.getMouseX().toDouble(), Client.getMouseY().toDouble()))
            }
        }

        for (button in 0..5) {
            if (Mouse.isButtonDown(button)) continue
            if (!this.draggedState.containsKey(button)) continue
            this.draggedState.remove(button)
        }
    }

    private fun handleClicked(button: Int) {
        this.onClicked?.trigger(
                Client.getMouseX(),
                Client.getMouseY(),
                button,
                Mouse.isButtonDown(button)
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
                Client.getMouseX() - this.draggedState[button]!!.x,
                Client.getMouseY() - this.draggedState[button]!!.y,
                Client.getMouseX(),
                Client.getMouseY(),
                button
        )

        this.draggedState[button] = Vector2d(Client.getMouseX().toDouble(), Client.getMouseY().toDouble())
    }

    private fun drawFullBG(bg: DisplayHandler.Background, color: Int, x: Float, y: Float, width: Float, height: Float) {
        if (bg === DisplayHandler.Background.FULL)
            Renderer.drawRect(color, x, y, width, height)
    }

    private fun drawPerLineBG(bg: DisplayHandler.Background, color: Int, x: Float, y: Float, width: Float, height: Float) {
        if (bg === DisplayHandler.Background.PER_LINE)
            Renderer.drawRect(color, x, y, width, height)
    }

    fun drawLeft(x: Float, y: Float, maxWidth: Float, background: DisplayHandler.Background, backgroundColor: Int, textColor: Int) {
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

        drawPerLineBG(bg, bgColor, xOff - 1, y - 1, (this.textWidth + 2).toFloat(), 10 * this.text.getScale())
        this.text.setX(xOff).setY(y).setColor(textCol).draw()

        handleInput(xOff - 1, y - 1, (this.textWidth + 2).toFloat(), 10 * this.text.getScale())
    }

    fun drawRight(x: Float, y: Float, maxWidth: Float, background: DisplayHandler.Background, backgroundColor: Int, textColor: Int) {
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
            xOff = x - (this.textWidth / 2).toFloat() - maxWidth / 2
        }

        drawPerLineBG(bg, bgColor, xOff - 1, y - 1, (this.textWidth + 2).toFloat(), 10 * this.text.getScale())
        this.text.setX(xOff).setY(y).setColor(textCol).draw()

        handleInput(xOff - 1, y - 1, (this.textWidth + 2).toFloat(), 10 * this.text.getScale())
    }

    fun drawCenter(x: Float, y: Float, maxWidth: Float, background: DisplayHandler.Background, backgroundColor: Int, textColor: Int) {
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

        drawPerLineBG(bg, bgColor, xOff - 1, y - 1, (this.textWidth + 2).toFloat(), 10 * this.text.getScale())
        this.text.setX(xOff).setY(y).setColor(textCol).draw()

        handleInput(xOff - 1, y - 1, (this.textWidth + 2).toFloat(), 10 * this.text.getScale())
    }

    override fun toString() =
            "DisplayLine{" +
                    "text=$text, textColor=$textColor, align=$align, " +
                    "background=$background, backgroundColor=$backgroundColor, " +
                    "}"
}