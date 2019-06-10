package com.chattriggers.ctjs.minecraft.objects.display

import com.chattriggers.ctjs.utils.kotlin.External
import com.chattriggers.ctjs.utils.kotlin.NotAbstract
import jdk.nashorn.api.scripting.ScriptObjectMirror

@External
@NotAbstract
abstract class Display() {
    private var lines = mutableListOf<DisplayLine>()

    protected var renderX = 0f
    protected var renderY = 0f
    protected var shouldRender = true

    protected var backgroundColor = 0x50000000
    protected var textColor = 0xffffffff.toInt()

    private var background = DisplayHandler.Background.NONE
    private var align = DisplayHandler.Align.LEFT
    private var order = DisplayHandler.Order.DOWN

    protected var minWidth = 0f
    private var width = 0f
    private var height = 0f

    init {
        DisplayHandler.registerDisplay(this)
    }

    fun setBackgroundColor(backgroundColor: Int) = apply {
        this.backgroundColor = backgroundColor
    }

    fun setTextColor(textColor: Int) = apply {
        this.textColor = textColor
    }

    fun getBackground(): DisplayHandler.Background = this.background
    fun setBackground(background: Any) = apply {
        this.background = when (background) {
            is String -> DisplayHandler.Background.valueOf(background.toUpperCase().replace(" ", "_"))
            is DisplayHandler.Background -> background
            else -> DisplayHandler.Background.NONE
        }
    }

    fun getAlign(): DisplayHandler.Align= this.align
    fun setAlign(align: Any) = apply {
        this.align = when (align) {
            is String -> DisplayHandler.Align.valueOf(align.toUpperCase())
            is DisplayHandler.Align -> align
            else -> DisplayHandler.Align.LEFT
        }
    }

    fun getOrder(): DisplayHandler.Order = this.order
    fun setOrder(order: Any) = apply {
        this.order = when (order) {
            is String -> DisplayHandler.Order.valueOf(order.toUpperCase())
            is DisplayHandler.Order -> order
            else -> DisplayHandler.Order.DOWN
        }
    }

    fun setLine(index: Int, line: Any) = apply {
        while (this.lines.size -1 < index) this.lines.add(createDisplayLine(""))
        this.lines[index] = when (line) {
            is String -> createDisplayLine(line)
            is DisplayLine -> line
            else -> createDisplayLine("")
        }
    }

    fun getLine(index: Int): DisplayLine = this.lines[index]
    fun getLines(): List<DisplayLine> = this.lines
    fun setLines(lines: MutableList<DisplayLine>) = apply {
        this.lines = lines
    }

    @JvmOverloads
    fun addLine(index: Int = -1, line: Any) {
        val toAdd = when (line) {
            is String -> createDisplayLine(line)
            is DisplayLine -> line
            else -> createDisplayLine("")
        }

        if (index == -1) this.lines.add(toAdd)
        else this.lines.add(index, toAdd)
    }

    fun addLines(vararg lines: Any) = apply{
        lines.forEach {
            this.lines.add(when (it) {
                is String -> createDisplayLine(it)
                is DisplayLine -> it
                else -> createDisplayLine("")
            })
        }
    }

    fun clearLines() = apply {
        this.lines.clear()
    }

    fun setRenderX(renderX: Float) = apply {
        this.renderX
    }

    fun setRenderY(renderY: Float) = apply {
        this.renderY = renderY
    }

    fun setRenderLoc(renderX: Float, renderY: Float) = apply {
        this.renderX = renderX
        this.renderY = renderY
    }

    fun setShouldRender(shouldRender: Boolean) = apply {
        this.shouldRender = shouldRender
    }

    fun getWidth(): Float = this.width
    fun getHeight(): Float = this.height

    fun setMinWidth(minWidth: Float) = apply {
        this.minWidth = minWidth
    }

    fun render() {
        if (!this.shouldRender) return

        var maxWidth = this.minWidth
        lines.forEach {
            if (it.getTextWidth() > maxWidth)
                maxWidth = it.getTextWidth()
        }

        this.width = maxWidth

        var i = 0f
        lines.forEach {
            drawLine(it, this.renderX, this.renderY + (i * 10), maxWidth)
            when (this.order) {
                DisplayHandler.Order.DOWN -> i += it.getText().getScale()
                DisplayHandler.Order.UP -> i -= it.getText().getScale()
            }
        }

        this.height = i
    }

    private fun drawLine(line: DisplayLine, x: Float, y: Float, maxWidth: Float) {
        when (this.align) {
            DisplayHandler.Align.LEFT -> line.drawLeft(x, y, maxWidth, this.background, this.backgroundColor, this.textColor)
            DisplayHandler.Align.RIGHT -> line.drawRight(x, y, maxWidth, this.background, this.backgroundColor, this.textColor)
            DisplayHandler.Align.CENTER -> line.drawCenter(x, y, maxWidth, this.background, this.backgroundColor, this.textColor)
            else -> return
        }
    }

    internal abstract fun createDisplayLine(text: String): DisplayLine

    override fun toString() =
            "Display{" +
                    "shouldRender=$shouldRender, " +
                    "renderX=$renderX, renderY=$renderY, " +
                    "background=$background, backgroundColor=$backgroundColor, " +
                    "textColor=$textColor, align=$align, order=$order, " +
                    "minWidth=$minWidth, width=$width, height=$height, " +
                    "lines=$lines" +
                    "}"

}