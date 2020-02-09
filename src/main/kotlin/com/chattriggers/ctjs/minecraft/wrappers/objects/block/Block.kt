package com.chattriggers.ctjs.minecraft.wrappers.objects.block

import com.chattriggers.ctjs.minecraft.wrappers.Player
import com.chattriggers.ctjs.minecraft.wrappers.World
import com.chattriggers.ctjs.minecraft.wrappers.objects.inventory.Item
import com.chattriggers.ctjs.utils.kotlin.External
import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry
import net.minecraftforge.registries.ForgeRegistries
import net.minecraft.block.Block as MCBlock

@External
open class Block {
    var block: MCBlock
    var blockPos: BlockPos = BlockPos(0, 0, 0)
    var face: BlockFace? = null

    constructor(block: MCBlock) {
        this.block = block
    }

    /**
     * Copies the block and blockPos of the provided block into
     * this object.
     */
    constructor(block: Block) {
        this.block = block.block
        this.blockPos = block.blockPos
    }

    constructor(blockName: String) {
        block = ForgeRegistries.BLOCKS.values.first { it.nameTextComponent.unformattedComponentText == blockName }
    }

    // TODO
    // constructor(blockID: Int) {
    //     block = MCBlock.getBlockById(blockID)
    // }

    constructor(item: Item) {
        block = MCBlock.getBlockFromItem(item.item)
    }

    /**
     * Sets the block position in the world.<br>
     * This is automatically set by {@link Player#lookingAt()}.<br>
     * This method is not meant for public use.
     *
     * @param blockPos the block position
     * @return the Block object
     */
    fun setBlockPos(blockPos: BlockPos) = apply {
        this.blockPos = blockPos
    }

    /**
     * Sets the face that is being looked at by the player.<br>
     * This is automatically set by {@link Player#lookingAt()}.<br>
     * This method is not meant for public use.
     *
     * @param face this face that is being looked at
     * @return the Block object
     */
    fun setFace(face: BlockFace) = apply {
        this.face = face
    }

    // TODO
    // fun getID(): Int = MCBlock.getIdFromBlock(block)

    /**
     * Gets the block's registry name.<br>
     * Example: <code>minecraft:planks</code>
     *
     * @return the block's registry name
     */
    fun getRegistryName() =
        "${ForgeRegistries.BLOCKS.getKey(block)?.namespace}:${ForgeRegistries.BLOCKS.getKey(block)?.path}"

    // TODO
    // /**
    //  * Gets the block's unlocalized name.<br>
    //  * Example: <code>tile.wood</code>
    //  *
    //  * @return the block's unlocalized name
    //  */
    // fun getUnlocalizedName(): String =

    /**
     * Gets the block's localized name.<br>
     * Example: <code>Wooden Planks</code>
     *
     * @return the block's localized name
     */
    fun getName(): String = block.nameTextComponent.unformattedComponentText

    fun getLightValue(): Int {
        return this.block.getLightValue(
                World.getWorld()!!.getBlockState(blockPos),
                World.getWorld(),
                blockPos
        );
    }

    fun getState(): BlockState = World.getWorld()!!.getBlockState(blockPos)

    fun getDefaultState(): BlockState = block.defaultState

    fun getX(): Int = blockPos.x
    fun getY(): Int = blockPos.y
    fun getZ(): Int = blockPos.z

    // TODO
    // fun getMetadata(): Int = block.getMetaFromState(getState())
    // fun getDefaultMetadata(): Int = block.getMetaFromState(getDefaultState())

    fun canProvidePower(): Boolean {
        return World.getWorld()!!.getBlockState(blockPos).canProvidePower()
    }

    fun isPowered(): Boolean = World.getWorld()!!.isBlockPowered(blockPos)

    fun getRedstoneStrength(): Int = World.getWorld()!!.getStrongPower(blockPos)

    /**
     * Checks whether the block can be mined with the tool in the player's hand
     *
     * @return whether the block can be mined
     */
    fun canBeHarvested(): Boolean = block.canHarvestBlock(
        World.getWorld()!!.getBlockState(blockPos),
        World.getWorld()!!,
        blockPos,
        Player.getPlayer()
    )

    fun canBeHarvestedWith(item: Item): Boolean = item.canHarvest(this)

    fun getHarvestLevel(): Int = block.getHarvestLevel(getDefaultState())

    // TODO: transparent vs translucent?
    fun isTranslucent(): Boolean {
        return World.getWorld()!!.getBlockState(blockPos).isTransparent
    }

    override fun toString(): String = "Block{name=${block.registryName}, x=${getX()}, y=${getY()}, z=${getZ()}}"
}