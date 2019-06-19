package com.chattriggers.ctjs.minecraft.wrappers.objects.inventory.action

import com.chattriggers.ctjs.utils.kotlin.External

//#if MC>10809
//$$ import com.chattriggers.ctjs.utils.kotlin.MCClickType
//#endif

@External
class KeyAction(slot: Long, windowId: Long) : Action(slot, windowId) {
    private var key: Long = -1L

    fun getKey(): Long = key

    /**
     * Which key to act as if has been clicked (REQUIRED).
     * Options currently are 0-8, representing the hotbar keys
     *
     * @param key which key to "click"
     */
    fun setKey(key: Long) = apply {
        this.key = key
    }

    override fun complete() {
        //#if MC<=10809
        doClick(key, 2)
        //#else
        //$$ doClick(key, MCClickType.SWAP)
        //#endif
    }
}