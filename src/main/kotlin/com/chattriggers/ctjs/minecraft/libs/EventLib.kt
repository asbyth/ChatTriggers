package com.chattriggers.ctjs.minecraft.libs

import com.chattriggers.ctjs.minecraft.listeners.CancellableEvent
import com.chattriggers.ctjs.utils.kotlin.External
import net.minecraft.util.text.ITextComponent
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.client.event.GuiScreenEvent
import net.minecraftforge.client.event.sound.PlaySoundEvent
import net.minecraftforge.fml.config.ModConfig

//TODO: figure out what is not needed anymore after the kotlin conversion and remove
@External
object EventLib {
    @JvmStatic
    fun getButtonState(event: GuiScreenEvent): Boolean {
        return event is GuiScreenEvent.MouseClickedEvent
    }

    @JvmStatic
    fun getType(event: ClientChatReceivedEvent): Int {
        return event.type.id.toInt()
    }

    @JvmStatic
    fun getMessage(event: ClientChatReceivedEvent): ITextComponent {
        return event.message
    }

    @JvmStatic
    fun getName(event: PlaySoundEvent): String {
        return event.name
    }

    @JvmStatic
    fun getModId(event: ModConfig.ModConfigEvent): String {
        return event.config.modId
    }

    /**
     * Cancel an event. Automatically used with `cancel(event)`.
     *
     * @param event the event to cancel
     * @throws IllegalArgumentException if event can be cancelled "normally"
     */
    @JvmStatic
    @Throws(IllegalArgumentException::class)
    fun cancel(event: Any) {
        when (event) {
            is PlaySoundEvent -> event.resultSound = null
            is CancellableEvent -> event.setCanceled(true)
            else -> throw IllegalArgumentException()
        }
    }
}
