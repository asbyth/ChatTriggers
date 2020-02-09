package com.chattriggers.ctjs.minecraft.wrappers.objects

import com.chattriggers.ctjs.minecraft.libs.renderer.Renderer
import com.chattriggers.ctjs.minecraft.objects.message.TextComponent
import com.chattriggers.ctjs.minecraft.wrappers.Client
import com.chattriggers.ctjs.minecraft.wrappers.objects.inventory.Item
import com.chattriggers.ctjs.utils.kotlin.External
import net.minecraft.client.network.play.NetworkPlayerInfo
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.EquipmentSlotType
import net.minecraft.scoreboard.ScorePlayerTeam
import net.minecraft.util.text.StringTextComponent

@External
class PlayerMP(val player: PlayerEntity) : Entity(player) {
    fun isSpectator() = this.player.isSpectator

    fun getActivePotionEffects(): List<PotionEffect> {
        return player.activePotionEffects.map {
            PotionEffect(it)
        }
    }

    fun getPing() = getPlayerInfo()?.responseTime ?: -1

    // TODO
    // /**
    //  * Gets the item currently in the player's specified inventory slot.
    //  * 0 for main hand, 1-4 for armor
    //  * (2 for offhand in 1.12.2, and everything else shifted over).
    //  *
    //  * @param slot the slot to access
    //  * @return the item in said slot
    //  */
    // fun getItemInSlot(slot: Int): Item {
    //     return Item(player.getItemStackFromSlot(
    //             EquipmentSlotType.values()[slot]
    //         )
    //     )
    // }

    /**
     * Gets the display name for this player,
     * i.e. the name shown in tab list and in the player's nametag.
     * @return the display name
     */
    fun getDisplayName(): TextComponent? {
        return TextComponent(getPlayerName(getPlayerInfo()) ?: return null)
    }

    fun setTabDisplayName(textComponent: TextComponent) {
        getPlayerInfo()?.displayName = textComponent.chatComponentText
    }

    // TODO
    // /**
    //  * Sets the name for this player shown above their head,
    //  * in their name tag
    //  *
    //  * @param textComponent the new name to display
    //  */
    // fun setNametagName(textComponent: TextComponent) {
    //     displayNameField.set(player, textComponent.chatComponentText.formattedText)
    // }

    @JvmOverloads
    fun draw(x: Int, y: Int, rotate: Boolean = false) = apply {
        Renderer.drawPlayer(player, x, y, rotate)
    }

    private fun getPlayerName(networkPlayerInfoIn: NetworkPlayerInfo?): String? {
        return networkPlayerInfoIn?.displayName?.formattedText
            ?: ScorePlayerTeam.formatMemberName(
                networkPlayerInfoIn?.playerTeam,
                StringTextComponent(networkPlayerInfoIn?.gameProfile?.name ?: return null)
            ).unformattedComponentText ?: null
    }

    private fun getPlayerInfo(): NetworkPlayerInfo? = Client.getConnection()?.getPlayerInfo(this.player.uniqueID)

    override fun toString(): String {
        return "PlayerMP{name:" + getName() +
                ",ping:" + getPing() +
                ",entity:" + super.toString() +
                "}"
    }

    override fun getName(): String = this.player.name.unformattedComponentText
}
