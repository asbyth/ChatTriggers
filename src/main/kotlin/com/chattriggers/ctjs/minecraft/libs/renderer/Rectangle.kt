package com.chattriggers.ctjs.minecraft.libs.renderer

import com.chattriggers.ctjs.utils.kotlin.External
import javax.vecmath.Vector2d

@External
class Rectangle(
        private var color: Long,
        private var x: Double,
        private var y: Double,
        private var width: Double,
        private var height: Double) {

    private var shadow = Shadow(this)
    private var outline = Outline(this)

    fun getColor(): Long = this.color
    fun setColor(color: Long) = apply { this.color = color }

    fun getX(): Double = this.x
    fun setX(x: Double) = apply { this.x = x }

    fun getY(): Double = this.y
    fun setY(y: Double) = apply { this.y = y }

    fun getWidth(): Double = this.width
    fun setWidth(width: Double) = apply { this.width = width }

    fun getHeight(): Double = this.height
    fun setHeight(height: Double) = apply { this.height = height }

    fun isShadow(): Boolean = this.shadow.on
    fun setShadow(shadow: Boolean) = apply { this.shadow.on = shadow }

    fun getShadowOffset(): Vector2d = this.shadow.offset
    fun getShadowOffsetX(): Double = this.shadow.offset.x
    fun getShadowOffsetY(): Double = this.shadow.offset.y
    fun setShadowOffset(x: Double, y: Double) = apply {
        this.shadow.offset.x = x
        this.shadow.offset.y = y
    }
    fun setShadowOffsetX(x: Double) = apply { this.shadow.offset.x = x }
    fun setShadowOffsetY(y: Double) = apply { this.shadow.offset.y = y }

    fun getShadowColor(): Long = this.shadow.color
    fun setShadowColor(color: Long) = apply { this.shadow.color = color }

    fun setShadow(color: Long, x: Double, y: Double) = apply {
        setShadow(true)
        setShadowColor(color)
        setShadowOffset(x, y)
    }

    fun getOutline(): Boolean = this.outline.on
    fun setOutline(outline: Boolean) = apply { this.outline.on = outline }

    fun getOutlineColor(): Long = this.outline.color
    fun setOutlineColor(color: Long) = apply { this.outline.color = color }

    fun getThickness(): Double = this.outline.thickness
    fun setThickness(thickness: Double) = apply { this.outline.thickness = thickness }

    fun setOutline(color: Long, thickness: Double) = apply {
        setOutline(true)
        setOutlineColor(color)
        setThickness(thickness)
    }

    fun draw() = apply {
        this.shadow.draw()
        this.outline.draw()
        Renderer.drawRect(this.color, this.x, this.y, this.width, this.height)
    }

    private class Shadow(
            val rect: Rectangle,
            var on: Boolean = false,
            var color: Long = 0x50000000,
            var offset: Vector2d = Vector2d(5.0, 5.0)) {
        fun draw() {
            if (!this.on) return
            Renderer.drawRect(this.color,
                    this.rect.x + this.offset.x,
                    this.rect.y + this.rect.height,
                    this.rect.width,
                    this.offset.y
            )
            Renderer.drawRect(this.color,
                    this.rect.x + this.rect.width,
                    this.rect.y + this.offset.y,
                    this.offset.x,
                    this.rect.height - this.offset.y
            )
        }
    }

    private class Outline(
            val rect: Rectangle,
            var on: Boolean = false,
            var color: Long = 0xff000000,
            var thickness: Double = 5.0) {
        fun draw() {
            if (!this.on) return
            Renderer.drawRect(this.color,
                    this.rect.x - this.thickness,
                    this.rect.y - this.thickness,
                    this.rect.width + this.thickness * 2,
                    this.rect.height + this.thickness * 2
            )
        }
    }
}