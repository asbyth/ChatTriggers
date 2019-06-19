package com.chattriggers.ctjs.minecraft.libs.renderer

import com.chattriggers.ctjs.utils.kotlin.External
import com.chattriggers.ctjs.utils.kotlin.MCTessellator
import com.chattriggers.ctjs.utils.kotlin.getRenderer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import javax.vecmath.Vector2d

@External
class Shape(private var color: Long) {
    private var vertexes = mutableListOf<Vector2d>()
    private var drawMode = 9L

    fun copy(): Shape = clone()
    fun clone(): Shape {
        val clone = Shape(this.color)
        clone.vertexes.addAll(this.vertexes)
        clone.setDrawMode(this.drawMode)
        return clone
    }

    fun getColor(): Long = this.color
    fun setColor(color: Long) = apply { this.color = color }

    fun getDrawMode(): Long = this.drawMode
    /**
     * Sets the GL draw mode of the shape. Possible draw modes are:<br>
     * 0 = points<br>
     * 1 = lines<br>
     * 2 = line loop<br>
     * 3 = line strip<br>
     * 5 = triangles<br>
     * 5 = triangle strip<br>
     * 6 = triangle fan<br>
     * 7 = quads<br>
     * 8 = quad strip<br>
     * 9 = polygon
     */
    fun setDrawMode(drawMode: Long) = apply { this.drawMode = drawMode }

    fun getVertexes(): List<Vector2d> = this.vertexes
    fun addVertex(x: Double, y: Double) = apply { this.vertexes.add(Vector2d(x, y)) }
    fun insertVertex(index: Int, x: Double, y: Double) = apply { this.vertexes.add(index, Vector2d(x, y)) }
    fun removeVertex(index: Int) = apply { this.vertexes.removeAt(index) }

    /**
     * Sets the shape as a line pointing from [x1, y1] to [x2, y2] with a thickness
     */
    fun setLine(x1: Double, y1: Double, x2: Double, y2: Double, thickness: Double) = apply {
        this.vertexes.clear()

        val theta = -Math.atan2(y2 - y1, x2 - x1)
        val i = Math.sin(theta) * (thickness / 2)
        val j = Math.cos(theta) * (thickness / 2)

        this.vertexes.add(Vector2d(x1 + i, y1 + j))
        this.vertexes.add(Vector2d(x2 + i, y2 + j))
        this.vertexes.add(Vector2d(x2 - i, y2 - j))
        this.vertexes.add(Vector2d(x1 - i, y1 - j))

        this.drawMode = 9
    }

    /**
     * Sets the shape as a circle with a center at [x, y]
     * with radius and number of steps around the circle
     */
    fun setCircle(x: Double, y: Double, radius: Double, steps: Int) = apply {
        this.vertexes.clear()

        val theta = 2 * Math.PI / steps
        val cos = Math.cos(theta)
        val sin = Math.sin(theta)

        var xHolder: Double
        var circleX = 1.0
        var circleY = 0.0

        for (i in 0 .. steps) {
            this.vertexes.add(Vector2d(x, y))
            this.vertexes.add(Vector2d(circleX * radius + x, circleY * radius + y))
            xHolder = circleX
            circleX = cos * circleX - sin * circleY
            circleY = sin * xHolder + cos * circleY
            this.vertexes.add(Vector2d(circleX * radius + x, circleY * radius + y))
        }

        this.drawMode = 5
    }

    fun draw() = apply {
        val a = (this.color shr 24 and 255).toFloat() / 255.0f
        val r = (this.color shr 16 and 255).toFloat() / 255.0f
        val g = (this.color shr 8 and 255).toFloat() / 255.0f
        val b = (this.color and 255).toFloat() / 255.0f

        val tessellator = MCTessellator.getInstance()
        val worldRenderer = tessellator.getRenderer()

        GlStateManager.enableBlend()
        GlStateManager.disableTexture2D()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        if (Renderer.colorized == null)
            GlStateManager.color(r, g, b, a)

        worldRenderer.begin(this.drawMode.toInt(), DefaultVertexFormats.POSITION)

        for (vertex in this.vertexes)
            worldRenderer.pos(vertex.x, vertex.y, 0.0).endVertex()

        tessellator.draw()
        GlStateManager.color(1f, 1f, 1f, 1f)
        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()

        Renderer.finishDraw()

        return this
    }
}