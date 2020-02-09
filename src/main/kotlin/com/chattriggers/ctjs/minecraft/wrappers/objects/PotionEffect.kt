package com.chattriggers.ctjs.minecraft.wrappers.objects

import com.chattriggers.ctjs.utils.kotlin.External
import net.minecraft.client.resources.I18n
import net.minecraft.potion.Effect
import net.minecraft.potion.EffectInstance
import net.minecraft.potion.Potion

@External
class PotionEffect(private val effect: EffectInstance) {
    /**
     * Returns the translation key of the potion.
     * Ex: "potion.poison"
     */
    fun getName(): String = this.effect.effectName

    /**
     * Returns the localized name of the potion that
     * is displayed in the player's inventory.
     * Ex: "Poison"
     */
    fun getLocalizedName(): String = I18n.format(getName(), "%s")

    fun getAmplifier(): Int = this.effect.amplifier

    fun getDuration(): Int = this.effect.duration

    fun getID(): Int = Effect.getId(this.effect.potion)

    fun isAmbient(): Boolean = this.effect.isAmbient

    fun isDurationMax(): Boolean = this.effect.isPotionDurationMax

    fun showsParticles(): Boolean {
        return this.effect.doesShowParticles();
    }

    override fun toString(): String = this.effect.toString()
}