package com.chattriggers.ctjs.minecraft.wrappers.objects.block

import net.minecraft.util.Direction

class BlockFace(val facing: Direction) {
    val axis = Axis(facing.axis)

    fun getName(): String = facing.getName()
    fun getXOffset(): Int = facing.xOffset
    fun getYOffset(): Int = facing.yOffset
    fun getZOffset(): Int = facing.zOffset

    override fun toString() = "BlockFace{name=${getName()}, xOffset=${getXOffset()}, yOffset=${getYOffset()}, zOffset = ${getZOffset()}, axis=$axis}"

    class Axis(val axis: Direction.Axis) {
        fun getName(): String = axis.getName()
        fun isHorizontal(): Boolean = axis.isHorizontal
        fun isVertical(): Boolean = axis.isVertical

        override fun toString() = "Axis{name=${getName()}, horizontal=${isHorizontal()}, vertical=${isVertical()}}"
    }
}