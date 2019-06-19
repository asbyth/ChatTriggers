package com.chattriggers.ctjs.minecraft.wrappers.objects.inventory.action

import com.chattriggers.ctjs.minecraft.wrappers.Client
import com.chattriggers.ctjs.minecraft.wrappers.Player
import com.chattriggers.ctjs.minecraft.wrappers.objects.inventory.Inventory
import com.chattriggers.ctjs.utils.kotlin.External

//#if MC>10809
//$$ import com.chattriggers.ctjs.utils.kotlin.MCClickType
//#endif

@External
abstract class Action(var slot: Long, var windowId: Long) {
    fun setSlot(slot: Long) = apply {
        this.slot = slot
    }

    fun setWindowId(windowId: Long) = apply {
        this.windowId = windowId
    }

    abstract fun complete()

    //#if MC<=10809
    protected fun doClick(button: Long, mode: Long) {
    //#else
    //$$ protected fun doClick(button: Long, mode: MCClickType) {
    //#endif
        Client.getMinecraft().playerController.windowClick(
            windowId.toInt(),
            slot.toInt(),
            button.toInt(),
            mode.toInt(),
            Player.getPlayer()
        )
    }

    companion object {
        /**
         * Creates a new action.
         * The Inventory must be a container, see {@link Inventory#isContainer()}.
         * The slot can be -999 for outside of the gui
         *
         * @param inventory the inventory to complete the action on
         * @param slot the slot to complete the action on
         * @param typeString the type of action to do (CLICK, DRAG, KEY)
         * @return the new action
         */
        @JvmStatic
        fun of(inventory: Inventory, slot: Long, typeString: String) =
            when (Type.valueOf(typeString.toUpperCase())) {
                Type.CLICK -> ClickAction(slot, inventory.getWindowId())
                Type.DRAG -> DragAction(slot, inventory.getWindowId())
                Type.KEY -> KeyAction(slot, inventory.getWindowId())
                Type.DROP -> DropAction(slot, inventory.getWindowId())
            }
    }

    enum class Type {
        CLICK, DRAG, KEY, DROP
    }
}