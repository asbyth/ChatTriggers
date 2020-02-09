package com.chattriggers.ctjs.minecraft.wrappers.objects

import com.chattriggers.ctjs.utils.kotlin.External
import com.chattriggers.ctjs.utils.kotlin.MCParticle

@External
class Particle(val underlyingEntity: MCParticle) {
    fun getX() = underlyingEntity.posX

    fun getY() = underlyingEntity.posY

    fun getZ() = underlyingEntity.posZ

    fun setX(x: Double) = apply {
        underlyingEntity.setPosition(x, getY(), getZ())
    }

    fun setY(y: Double) = apply {
        underlyingEntity.setPosition(getX(), y, getZ())
    }

    fun setZ(z: Double) = apply {
        underlyingEntity.setPosition(getX(), getY(), z)
    }

    fun scale(scale: Float) = apply {
        this.underlyingEntity.multipleParticleScaleBy(scale)
    }

    fun multiplyVelocity(multiplier: Float) = apply {
        this.underlyingEntity.multiplyVelocity(multiplier)
    }

    fun setColor(r: Float, g: Float, b: Float) = apply {
        this.underlyingEntity.setColor(r, g, b)
    }

    fun setColor(r: Float, g: Float, b: Float, a: Float) = apply {
        setColor(r, g, b)
        setAlpha(a)
    }

    fun setColor(color: Long) = apply {
        val red = (color shr 16 and 255).toFloat() / 255.0f
        val blue = (color shr 8 and 255).toFloat() / 255.0f
        val green = (color and 255).toFloat() / 255.0f
        val alpha = (color shr 24 and 255).toFloat() / 255.0f

        setColor(red, green, blue, alpha)
    }

    fun setAlpha(a: Float) = apply {
        this.underlyingEntity.setAlphaF(a)
    }

    /**
     * Sets the amount of ticks this particle will live for
     *
     * @param maxAge the particles max age (in ticks)
     */
    fun setMaxAge(maxAge: Int) = apply {
        this.underlyingEntity.maxAge = maxAge
    }

    fun remove() = apply {
        this.underlyingEntity.setExpired()
    }

    override fun toString(): String {
        return underlyingEntity.toString()
    }
}