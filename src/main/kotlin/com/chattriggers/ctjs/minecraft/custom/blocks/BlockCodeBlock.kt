package com.chattriggers.ctjs.minecraft.custom.blocks

import com.chattriggers.ctjs.engine.ModuleManager
import com.chattriggers.ctjs.minecraft.custom.guis.GuiCodeBlock
import com.chattriggers.ctjs.minecraft.custom.tileEntities.TileEntityCodeBlock
import com.chattriggers.ctjs.minecraft.objects.gui.GuiHandler
import net.minecraft.block.Block
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World

class BlockCodeBlock(materialIn: Material) : Block(materialIn), ITileEntityProvider {
    init { unlocalizedName = "code_block" }

    override fun canConnectRedstone(world: IBlockAccess, pos: BlockPos, side: EnumFacing): Boolean {
        val tileEntity = world.getTileEntity(pos)
        return when (tileEntity) {
            is TileEntityCodeBlock -> tileEntity.connectsToRedstone
            else -> false
        }
    }

    override fun isBlockSolid(worldIn: IBlockAccess, pos: BlockPos, side: EnumFacing): Boolean {
        val tileEntity = worldIn.getTileEntity(pos)
        return when (tileEntity) {
            is TileEntityCodeBlock -> tileEntity.solid
            else -> false
        }
    }

    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if (worldIn.isRemote) return true

        val tileEntity = worldIn.getTileEntity(pos)
        when (tileEntity) {
            is TileEntityCodeBlock -> GuiHandler.openGui(GuiCodeBlock(tileEntity))
            else -> ModuleManager.generalConsole.printStackTrace(IllegalArgumentException("TileEntity for code block is not TileEntityCodeBlock"))
        }

        return false
    }

    override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity {
        return TileEntityCodeBlock()
    }
}