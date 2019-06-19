package com.chattriggers.ctjs.minecraft.wrappers.objects

import com.chattriggers.ctjs.minecraft.mixins.MixinEntityFX
import com.chattriggers.ctjs.utils.kotlin.External
import com.chattriggers.ctjs.utils.kotlin.MCParticle

@External
class Particle(val underlyingEntity: MCParticle) {
    fun scale(scale: Double) {
        this.underlyingEntity.multipleParticleScaleBy(scale.toFloat())
    }

    fun multiplyVelocity(multiplier: Double) {
        this.underlyingEntity.multiplyVelocity(multiplier.toFloat())
    }

    fun setColor(r: Double, g: Double, b: Double) {
        this.underlyingEntity.setRBGColorF(r.toFloat(), g.toFloat(), b.toFloat())
    }

    fun setColor(r: Double, g: Double, b: Double, a: Double) {
        setColor(r, g, b)
        setAlpha(a)
    }

    fun setColor(color: Long) {
        val red = (color shr 16 and 255).toDouble() / 255.0
        val blue = (color shr 8 and 255).toDouble() / 255.0
        val green = (color and 255).toDouble() / 255.0
        val alpha = (color shr 24 and 255).toDouble() / 255.0

        setColor(red, green, blue, alpha)
    }

    fun setAlpha(a: Double) {
        this.underlyingEntity.setAlphaF(a.toFloat())
    }

    /**
     * Sets the amount of ticks this particle will live for
     *
     * @param maxAge the particles max age (in ticks)
     */
    fun setMaxAge(maxAge: Int) {
        (this.underlyingEntity as MixinEntityFX).particleMaxAge = maxAge
    }

    fun remove() {
        //#if MC<=10809
        this.underlyingEntity.setDead()
        //#else
        //$$ this.underlyingEntity.setExpired();
        //#endif
    }
}