package com.chattriggers.ctjs.minecraft.listeners

import com.chattriggers.ctjs.minecraft.wrappers.Client
import com.chattriggers.ctjs.minecraft.wrappers.Scoreboard
import com.chattriggers.ctjs.minecraft.wrappers.World
import com.chattriggers.ctjs.minecraft.wrappers.objects.PlayerMP
import com.chattriggers.ctjs.minecraft.wrappers.objects.inventory.Item
import com.chattriggers.ctjs.triggers.TriggerType
import net.minecraft.client.MouseHelper
import net.minecraft.client.renderer.Vector3d
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.math.Vec3d
import net.minecraftforge.client.event.*
import net.minecraftforge.event.TickEvent
import net.minecraftforge.event.entity.player.EntityItemPickupEvent
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import org.lwjgl.opengl.GL11

object ClientListener {
    private var ticksPassed: Int = 0
    private val mouseState: MutableMap<Int, Boolean>
    private val draggedState: MutableMap<Int, State>

    class State(val x: Float, val y: Float)

    init {
        this.ticksPassed = 0

        this.mouseState = mutableMapOf()
        draggedState = mutableMapOf()

        for (i in 0..4)
            this.mouseState[i] = false
    }

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (World.getWorld() == null) return

        TriggerType.TICK.triggerAll(this.ticksPassed)
        this.ticksPassed++

        Scoreboard.resetCache()
    }

    // TODO
    // private fun handleMouseInput() {
    //     for (button in 0..4) {
    //         handleDragged(button)
    //
    //         // normal clicked
    //         if (Mouse.isButtonDown(button) == this.mouseState[button]) continue
    //
    //         TriggerType.CLICKED.triggerAll(
    //             Client.getMouseX(),
    //             Client.getMouseY(),
    //             button,
    //             Mouse.isButtonDown(button)
    //         )
    //
    //         this.mouseState[button] = Mouse.isButtonDown(button)
    //
    //         // add new dragged
    //         if (Mouse.isButtonDown(button))
    //             this.draggedState[button] = State(Client.getMouseX(), Client.getMouseY())
    //         else if (this.draggedState.containsKey(button))
    //             this.draggedState.remove(button)
    //     }
    // }

    private fun handleDragged(button: Int) {
        if (button !in draggedState)
            return

        TriggerType.DRAGGED.triggerAll(
            Client.getMouseX() - (this.draggedState[button]?.x ?: 0f),
            Client.getMouseY() - (this.draggedState[button]?.y ?: 0f),
            Client.getMouseX(),
            Client.getMouseY(),
            button
        )

        // update dragged
        this.draggedState[button] = State(Client.getMouseX(), Client.getMouseY())
    }

    @SubscribeEvent
    fun onRenderGameOverlay(event: RenderGameOverlayEvent) {
        GL11.glPushMatrix()
        handleOverlayTriggers(event)
        GL11.glPopMatrix()

        if (event.type != RenderGameOverlayEvent.ElementType.TEXT)
            return

        TriggerType.STEP.triggerAll()

        // TODO
        // handleMouseInput()
    }

    private fun handleOverlayTriggers(event: RenderGameOverlayEvent) {
        val element = event.type

        when (element) {
            RenderGameOverlayEvent.ElementType.PLAYER_LIST -> TriggerType.RENDER_PLAYER_LIST.triggerAll(event)
            RenderGameOverlayEvent.ElementType.CROSSHAIRS -> TriggerType.RENDER_CROSSHAIR.triggerAll(event)
            RenderGameOverlayEvent.ElementType.DEBUG -> TriggerType.RENDER_DEBUG.triggerAll(event)
            RenderGameOverlayEvent.ElementType.BOSSHEALTH -> TriggerType.RENDER_BOSS_HEALTH.triggerAll(event)
            RenderGameOverlayEvent.ElementType.HEALTH -> TriggerType.RENDER_HEALTH.triggerAll(event)
            RenderGameOverlayEvent.ElementType.ARMOR -> TriggerType.RENDER_ARMOR.triggerAll(event)
            RenderGameOverlayEvent.ElementType.FOOD -> TriggerType.RENDER_FOOD.triggerAll(event)
            RenderGameOverlayEvent.ElementType.HEALTHMOUNT -> TriggerType.RENDER_MOUNT_HEALTH.triggerAll(event)
            RenderGameOverlayEvent.ElementType.EXPERIENCE -> TriggerType.RENDER_EXPERIENCE.triggerAll(event)
            RenderGameOverlayEvent.ElementType.HOTBAR -> TriggerType.RENDER_HOTBAR.triggerAll(event)
            RenderGameOverlayEvent.ElementType.AIR -> TriggerType.RENDER_AIR.triggerAll(event)
            RenderGameOverlayEvent.ElementType.TEXT -> TriggerType.RENDER_OVERLAY.triggerAll(event)
        }
    }

    @SubscribeEvent
    fun onGuiOpened(event: GuiOpenEvent) {
        TriggerType.GUI_OPENED.triggerAll(event)
    }

    @SubscribeEvent
    fun onBlockHighlight(event: DrawHighlightEvent) {
        if (event.target == null) return

        val position = Vec3d(
            event.target.hitVec.x,
            event.target.hitVec.y,
            event.target.hitVec.z
        )

        TriggerType.BLOCK_HIGHLIGHT.triggerAll(position, event)
    }

    @SubscribeEvent
    fun onPickupItem(event: EntityItemPickupEvent) {
        val player = event.player
        val item = event.item

        val position = Vec3d(
            item.posX,
            item.posY,
            item.posZ
        )
        val motion = Vec3d(
            item.motion.x,
            item.motion.y,
            item.motion.z
        )

        TriggerType.PICKUP_ITEM.triggerAll(
            Item(item.item),
            PlayerMP(player),
            position,
            motion,
            event
        )
    }

    fun onDropItem(player: PlayerEntity, item: ItemStack?): Boolean {
        val event = CancellableEvent()

        TriggerType.DROP_ITEM.triggerAll(
            Item(item),
            PlayerMP(player),
            event
        )

        return event.isCancelled()
    }

    @SubscribeEvent
    fun onGuiRender(e: GuiScreenEvent.BackgroundDrawnEvent) {
        GL11.glPushMatrix()

        TriggerType.GUI_RENDER.triggerAll(
            Client.mc.mouseHelper.mouseX,
            Client.mc.mouseHelper.mouseY,
            e.gui
        )

        GL11.glPopMatrix()
    }

    @SubscribeEvent
    fun onLeftClick(e: PlayerInteractEvent) {
        val action = when (e) {
            is PlayerInteractEvent.EntityInteract, is PlayerInteractEvent.EntityInteractSpecific ->
                PlayerInteractAction.RIGHT_CLICK_ENTITY
            is PlayerInteractEvent.RightClickBlock -> PlayerInteractAction.RIGHT_CLICK_BLOCK
            is PlayerInteractEvent.RightClickItem -> PlayerInteractAction.RIGHT_CLICK_ITEM
            is PlayerInteractEvent.RightClickEmpty -> PlayerInteractAction.RIGHT_CLICK_EMPTY
            is PlayerInteractEvent.LeftClickBlock -> PlayerInteractAction.LEFT_CLICK_BLOCK
            is PlayerInteractEvent.LeftClickEmpty -> PlayerInteractAction.LEFT_CLICK_EMPTY
            else -> PlayerInteractAction.UNKNOWN
        }

        TriggerType.PLAYER_INTERACT.triggerAll(
                action,
                World.getBlockAt(e.pos.x, e.pos.y, e.pos.z),
                e
        )
    }

    enum class PlayerInteractAction {
        RIGHT_CLICK_BLOCK,
        RIGHT_CLICK_EMPTY,
        LEFT_CLICK_BLOCK,
        RIGHT_CLICK_ENTITY,
        RIGHT_CLICK_ITEM,
        LEFT_CLICK_EMPTY,
        UNKNOWN
    }
}