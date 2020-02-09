package com.chattriggers.ctjs.minecraft.wrappers.objects.inventory

import com.chattriggers.ctjs.minecraft.wrappers.Player
import com.chattriggers.ctjs.minecraft.wrappers.World
import com.chattriggers.ctjs.minecraft.wrappers.objects.Entity
import com.chattriggers.ctjs.minecraft.wrappers.objects.block.Block
import com.chattriggers.ctjs.utils.kotlin.External
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.item.ItemEntity
import net.minecraft.item.ItemStack
import net.minecraftforge.registries.ForgeRegistries
import net.minecraft.item.Item as MCItem


@External
class Item {
    val item: MCItem
    var itemStack: ItemStack
    private val emptyBlockName = Block("air").block.registryName

    constructor(itemStack: ItemStack?) {
        if (itemStack == null || itemStack == ItemStack.EMPTY) {
            this.item = ForgeRegistries.ITEMS.first { emptyBlockName == it.registryName }
            this.itemStack = ItemStack(item)
        } else {
            this.item = itemStack.item
            this.itemStack = itemStack
        }
    }

    constructor(itemName: String) {
        item = ForgeRegistries.ITEMS.values.first { it.name.unformattedComponentText == itemName }
        itemStack = ItemStack(item)
    }

    constructor(itemID: Int) {
        item = MCItem.getItemById(itemID)
        itemStack = ItemStack(item)
    }

    constructor(block: Block) {
        item = MCItem.getItemFromBlock(block.block)
        itemStack = ItemStack(item)
    }

    constructor(entityItem: ItemEntity) {
        itemStack = entityItem.item
        item = itemStack.item
    }

    /**
     * Created an Item object from an Entity.
     * Has to be wrapping an EntityItem.
     *
     * @param entity the Entity
     */
    constructor(entity: Entity) {
        if (entity.entity is ItemEntity) {
            itemStack = entity.entity.item;
            item = itemStack.item;
        } else {
            throw IllegalArgumentException("Entity is not of type EntityItem")
        }
    }

    fun getRawNBT() = itemStack.serializeNBT().toString()

    fun getID(): Int = MCItem.getIdFromItem(item)

    fun setStackSize(stackSize: Int) = apply {
        itemStack = ItemStack(item, stackSize)
    }

    fun getStackSize(): Int = itemStack.count

    // TODO
    // /**
    //  * Gets the item's unlocalized name.<br>
    //  * Example: <code>tile.wood</code>
    //  *
    //  * @return the item's unlocalized name
    //  */
    // fun getUnlocalizedName(): String = item.unlocalizedName

    /**
     * Gets the item's registry name.<br>
     * Example: <code>minecraft:planks</code>
     *
     * @return the item's registry name
     */
    fun getRegistryName(): String = item.registryName.toString()

    /**
     * Gets the item's stack display name.<br>
     * Example: <code>Oak Wood Planks</code>
     *
     * @return the item's stack display name
     */
    fun getName(): String = if (getID() == 0) "air" else itemStack.displayName.unformattedComponentText

    fun getEnchantments(): Map<String, Int> {
        return EnchantmentHelper.getEnchantments(itemStack).mapKeys {
                it.key.name.replace("enchantment.", "")
        }
    }

    fun isEnchantable(): Boolean = itemStack.isEnchantable

    fun isEnchanted(): Boolean = itemStack.isEnchanted

    fun getItemNBT(): String = itemStack.serializeNBT().toString()

    // TODO
    // fun getMetadata(): Int = itemStack.metadata

    // TODO
    // fun canPlaceOn(block: Block): Boolean = itemStack.canPlaceOn(World.getWorld()?.tags, block.block)

    fun canHarvest(block: Block): Boolean {
        return this.itemStack.canHarvestBlock(
                World.getWorld()!!.getBlockState(block.blockPos)
        )
    }

    // TODO
    // fun canDestroy(block: Block): Boolean = itemStack.canDestroy(block.block)

    /**
     * Gets the items durability, i.e. the number of uses left
     *
     * @return the items durability
     */
    fun getDurability(): Int = getMaxDamage() - getDamage()

    fun getDamage(): Int = itemStack.damage

    fun setDamage(damage: Int) = apply {
        itemStack.damage = damage
    }

    fun getMaxDamage(): Int = itemStack.maxDamage

    fun isDamagable(): Boolean = itemStack.isDamageable

    fun getLore(): List<String> {
        return itemStack.getTooltip(Player.getPlayer(), ITooltipFlag.TooltipFlags.ADVANCED).map {
            it.formattedText
        }
    }

    // TODO
    // /**
    //  * Renders the item icon to the client's overlay.
    //  *
    //  * @param x the x location
    //  * @param y the y location
    //  * @param scale the scale
    //  */
    // @JvmOverloads
    // fun draw(x: Float = 0f, y: Float = 0f, scale: Float = 1f) {
    //     val itemRenderer = Client.getMinecraft().renderManager
    //
    //     GL11.glScalef(scale, scale, 1f)
    //     GL11.glTranslatef(x / scale, y / scale, 0f)
    //     GL11.glColor4f(1f, 1f, 1f, 1f)
    //
    //     RenderHelper.enableStandardItemLighting()
    //
    //     itemRenderer.zLevel = 200f
    //     itemRenderer.renderItemIntoGUI(itemStack, 0, 0)
    //
    //     Renderer.finishDraw()
    // }

    /**
     * Checks whether another Item is the same as this one.
     * It compares id, stack size, and durability.
     *
     * @param other the object to compare to
     * @return whether the objects are equal
     */
    override fun equals(other: Any?): Boolean {
        return other is Item &&
                getID() == other.getID() &&
                getStackSize() == other.getStackSize() &&
                getDamage() == other.getDamage()
    }

    override fun hashCode(): Int {
        var result = item.hashCode()
        result = 31 * result + itemStack.hashCode()
        return result
    }

    override fun toString(): String = itemStack.toString()
}