package com.chattriggers.ctjs.minecraft.wrappers.objects

import com.chattriggers.ctjs.minecraft.libs.Tessellator
import com.chattriggers.ctjs.minecraft.wrappers.objects.block.BlockFace
import com.chattriggers.ctjs.utils.kotlin.External
import com.chattriggers.ctjs.utils.kotlin.MCEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.util.math.MathHelper
import java.util.*

@External
open class Entity(val entity: MCEntity) {
    fun getX() = entity.posX
    fun getY() = entity.posY
    fun getZ() = entity.posZ

    fun getLastX() = entity.lastTickPosX
    fun getLastY() = entity.lastTickPosY
    fun getLastZ() = entity.lastTickPosZ

    fun getRenderX() = getLastX() + (getX() - getLastX()) * Tessellator.partialTicks
    fun getRenderY() = getLastY() + (getY() - getLastY()) * Tessellator.partialTicks
    fun getRenderZ() = getLastZ() + (getZ() - getLastZ()) * Tessellator.partialTicks

    var face: BlockFace? = null

    /**
     * Gets the pitch, the horizontal direction the entity is facing towards.
     * This has a range of -180 to 180.
     *
     * @return the entity's pitch
     */
    fun getPitch(): Double {
        return MathHelper.wrapDegrees(this.entity.rotationPitch).toDouble()
    }

    /**
     * Gets the yaw, the vertical direction the entity is facing towards.
     * This has a range of -180 to 180.
     *
     * @return the entity's yaw
     */
    fun getYaw(): Double {
        return MathHelper.wrapDegrees(this.entity.rotationYaw).toDouble()
    }

    /**
     * Gets the entity's x motion.
     * This is the amount the entity will move in the x direction next tick.
     *
     * @return the player's x motion
     */
    fun getMotionX(): Double = this.entity.motion.x

    /**
     * Gets the entity's y motion.
     * This is the amount the entity will move in the y direction next tick.
     *
     * @return the player's y motion
     */
    fun getMotionY(): Double = this.entity.motion.y

    /**
     * Gets the entity's z motion.
     * This is the amount the entity will move in the z direction next tick.
     *
     * @return the player's z motion
     */
    fun getMotionZ(): Double = this.entity.motion.z

    /**
     * Gets the entity's health, -1 if not a living entity
     *
     * @return the entity's health
     */
    fun getHP(): Float {
        return if (this.entity is LivingEntity)
            this.entity.health
        else -1f
    }

    fun getRiding(): Entity? {
        val riding = this.entity.ridingEntity

        return if (riding == null)
            null
        else
            Entity(riding)
    }

    fun getRider(): Entity? {
        return if (getRiders().isEmpty())
            null
        else
            getRiders()[0]
    }

    fun getRiders(): List<Entity> {
        return this.entity.passengers.map {
            Entity(it)
        }
    }

    /**
     * Checks whether or not the entity is dead.
     * This is a fairly loose term, dead for a particle could mean it has faded,
     * while dead for an entity means it has no health.
     *
     * @return whether or not an entity is dead
     */
    fun isDead(): Boolean = !this.entity.isAlive

    /**
     * Gets the entire width of the entity's hitbox
     *
     * @return the entity's width
     */
    fun getWidth(): Float = this.entity.width

    /**
     * Gets the entire height of the entity's hitbox
     *
     * @return the entity's height
     */
    fun getHeight(): Float = this.entity.height

    /**
     * Gets the height of the eyes on the entity,
     * can be added to its Y coordinate to get the actual Y location of the eyes.
     * This value defaults to 85% of an entity's height, however is different for some entities.
     *
     * @return the height of the entity's eyes
     */
    fun getEyeHeight(): Float = this.entity.eyeHeight

    /**
     * Gets the name of the entity, could be "Villager",
     * or, if the entity has a custom name, it returns that.
     *
     * @return the (custom) name of the entity
     */
    open fun getName(): String = this.entity.name.unformattedComponentText

    /**
     * Gets the Java class name of the entity, for example "EntityVillager"
     *
     * @return the entity's class name
     */
    fun getClassName(): String = this.entity.javaClass.simpleName

    /**
     * Gets the Java UUID object of this entity.
     * Use of [UUID.toString] in conjunction is recommended.
     *
     * @return the entity's uuid
     */
    fun getUUID(): UUID = this.entity.uniqueID

    override fun toString(): String {
        return ("Entity{"
                + getName()
                + ",x:" + getX()
                + ",y:" + getY()
                + ",z:" + getZ()
                + "}")
    }
}