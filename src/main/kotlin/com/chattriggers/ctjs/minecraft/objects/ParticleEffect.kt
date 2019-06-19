package com.chattriggers.ctjs.minecraft.objects

import com.chattriggers.ctjs.minecraft.wrappers.World
import com.chattriggers.ctjs.utils.kotlin.External
import com.chattriggers.ctjs.utils.kotlin.MCParticle

@External
class ParticleEffect constructor(x: Double, y: Double, z: Double, xSpeed: Double = 0.0, ySpeed: Double = 0.0, zSpeed: Double = 0.0) : MCParticle(World.getWorld(), x, y, z, xSpeed, ySpeed, zSpeed) {
    fun scale(scale: Double) = apply { super.multipleParticleScaleBy(scale.toFloat()) }

    fun multiplyVelocity(multiplier: Double) = apply { multiplyVelocity(multiplier.toFloat()) }

    override fun multiplyVelocity(multiplier: Float) = apply { super.multiplyVelocity(multiplier) }

    fun setColor(r: Double, g: Double, b: Double, a: Double? = null) = apply {
        super.setRBGColorF(r.toFloat(), g.toFloat(), b.toFloat())
        if (a != null) setAlpha(a)
    }

    fun setColor(color: Long) = apply {
        setColor(
            (color shr 16 and 255).toDouble() / 255.0f,
            (color shr 8 and 255).toDouble() / 255.0f,
            (color and 255).toDouble() / 255.0f,
            (color shr 24 and 255).toDouble() / 255.0f
        )
    }

    fun setAlpha(a: Double) = apply { super.setAlphaF(a.toFloat()) }

    fun remove() = apply {
        //#if MC<=10809
        super.setDead()
        //#else
        //$$ super.setExpired();
        //#endif
    }
}
