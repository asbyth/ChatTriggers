package com.chattriggers.ctjs.blocks

import net.minecraft.block.Block
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

class BlockCodeBlock(materialIn: Material) : Block(materialIn), ITileEntityProvider {
    init {
        this.unlocalizedName = "code_block"
    }

    override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity {
        return TileEntityCodeBlock()
    }
}

class TileEntityCodeBlock: TileEntity() {

}