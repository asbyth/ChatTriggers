package com.chattriggers.ctjs.minecraft.custom

import com.chattriggers.ctjs.Reference
import com.chattriggers.ctjs.minecraft.custom.blocks.BlockCodeBlock
import com.chattriggers.ctjs.minecraft.custom.tileEntities.TileEntityCodeBlock
import com.chattriggers.ctjs.minecraft.wrappers.Client
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.client.resources.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraftforge.fml.common.registry.GameRegistry

object CustomHandler {
    private val code_block: Block = BlockCodeBlock(Material.wood)

    fun registerBlocks() {
        GameRegistry.registerBlock(code_block, code_block.unlocalizedName.substring(5))
        GameRegistry.registerTileEntity(TileEntityCodeBlock::class.java, "ct.jsCodeBlock")
    }

    fun registerRenders() {
        registerRender(code_block)
    }

    fun registerRender(block: Block) {
        val item = Item.getItemFromBlock(code_block)
        Client.getMinecraft().renderItem.itemModelMesher.register(
                item, 0,
                ModelResourceLocation(
                        Reference.MODID + ":" + item.unlocalizedName.substring(5),
                        "inventory"
                )
        )
    }
}