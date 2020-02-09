package com.chattriggers.ctjs.minecraft.listeners

import com.chattriggers.ctjs.CTJS
import com.chattriggers.ctjs.minecraft.libs.Tessellator
import com.chattriggers.ctjs.minecraft.wrappers.World
import com.chattriggers.ctjs.minecraft.wrappers.objects.PlayerMP
import com.chattriggers.ctjs.triggers.TriggerType
import net.minecraft.util.math.Vec3d
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.client.event.sound.PlaySoundEvent
import net.minecraftforge.event.TickEvent
import net.minecraftforge.event.world.BlockEvent
import net.minecraftforge.event.world.NoteBlockEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

object WorldListener {
    private var shouldTriggerWorldLoad: Boolean = false
    private var playerList: MutableList<String> = mutableListOf()

    @SubscribeEvent
    fun onWorldLoad(event: WorldEvent.Load) {
        this.playerList.clear()
        this.shouldTriggerWorldLoad = true
    }

    @SubscribeEvent
    fun onRenderGameOverlay(event: RenderGameOverlayEvent) {
        // world loadExtra trigger
        if (!shouldTriggerWorldLoad) return

        TriggerType.WORLD_LOAD.triggerAll()
        shouldTriggerWorldLoad = false

        CTJS.sounds
            .stream()
            .filter { it.isListening }
            .forEach { it.isListening = false }

        CTJS.sounds.clear()
    }

    @SubscribeEvent
    fun onRenderWorld(event: RenderWorldLastEvent) {
        Tessellator.partialTicks = event.partialTicks
        TriggerType.RENDER_WORLD.triggerAll(event.partialTicks)
    }

    @SubscribeEvent
    fun onWorldUnload(event: WorldEvent.Unload) {
        TriggerType.WORLD_UNLOAD.triggerAll()
    }

    @SubscribeEvent
    fun onSoundPlay(event: PlaySoundEvent) {
        val position = Vec3d(
            event.sound.x.toDouble(),
            event.sound.y.toDouble(),
            event.sound.z.toDouble()
        )

        val vol = try {
            event.sound.volume
        } catch (ignored: Exception) {
            0
        }
        val pitch = try {
            event.sound.volume
        } catch (ignored: Exception) {
            1
        }

        TriggerType.SOUND_PLAY.triggerAll(
            position,
            event.name,
            vol,
            pitch,
            event.sound.category ?: event.sound.category.name,
            event
        )
    }

    @SubscribeEvent
    fun noteBlockEventPlay(event: NoteBlockEvent.Play) {
        val position = Vec3d(
            event.pos.x.toDouble(),
            event.pos.y.toDouble(),
            event.pos.z.toDouble()
        )

        TriggerType.NOTE_BLOCK_PLAY.triggerAll(
            position,
            event.note.name,
            event.octave,
            event
        )
    }

    @SubscribeEvent
    fun noteBlockEventChange(event: NoteBlockEvent.Change) {
        val position = Vec3d(
            event.pos.x.toDouble(),
            event.pos.y.toDouble(),
            event.pos.z.toDouble()
        )

        TriggerType.NOTE_BLOCK_CHANGE.triggerAll(
            position,
            event.note.name,
            event.octave,
            event
        )
    }

    @SubscribeEvent
    fun updatePlayerList(event: TickEvent.ClientTickEvent) {
        World.getAllPlayers().filter {
            !playerList.contains(it.getName())
        }.forEach {
            playerList.add(it.getName())
            TriggerType.PLAYER_JOIN.triggerAll(it)
            return@forEach
        }

        val ite = playerList.listIterator()

        while (ite.hasNext()) {
            val it = ite.next()

            try {
                World.getPlayerByName(it)
            } catch (exception: Exception) {
                this.playerList.remove(it)
                TriggerType.PLAYER_LEAVE.triggerAll(it)
                break
            }
        }
    }

    @SubscribeEvent
    fun blockBreak(event: BlockEvent.BreakEvent) {
        TriggerType.BLOCK_BREAK.triggerAll(
            World.getBlockAt(event.pos.x, event.pos.y, event.pos.z),
            PlayerMP(event.player),
            event
        )
    }
}