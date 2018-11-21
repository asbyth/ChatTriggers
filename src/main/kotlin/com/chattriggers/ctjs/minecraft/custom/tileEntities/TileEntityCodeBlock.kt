package com.chattriggers.ctjs.minecraft.custom.tileEntities

import com.chattriggers.ctjs.engine.ModuleManager
import com.chattriggers.ctjs.triggers.TriggerType
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity

class TileEntityCodeBlock: TileEntity() {
    var file = ""
    var triggerType = TriggerType.TICK
    var connectsToRedstone = false
    var solid = true

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)

        val entryList = compound.getTag("code")
        when (entryList) {
            is NBTTagCompound -> {
                try {
                    file = entryList.getString("file")
                    triggerType = TriggerType.valueOf(entryList.getString("triggerType"))
                    connectsToRedstone = entryList.getBoolean("connectsToRedstone")
                    solid = entryList.getBoolean("solid")
                } catch (exception: IllegalArgumentException) {
                    writeToNBT(this.serializeNBT())
                }
            }
            else -> ModuleManager.generalConsole.printStackTrace(IllegalArgumentException("NBTBase in code block is not a NBTTagCompound"))
        }
    }

    override fun writeToNBT(compound: NBTTagCompound) {
        super.writeToNBT(compound)

        val entryCompound = NBTTagCompound()
        entryCompound.setString("file", file)
        entryCompound.setString("triggerType", triggerType.toString())
        entryCompound.setBoolean("connectsToRedstone", connectsToRedstone)
        entryCompound.setBoolean("solid", solid)
        compound.setTag("code", entryCompound)
    }
}