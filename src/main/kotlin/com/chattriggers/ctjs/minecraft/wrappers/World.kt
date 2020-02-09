package com.chattriggers.ctjs.minecraft.wrappers

import com.chattriggers.ctjs.minecraft.wrappers.objects.Chunk
import com.chattriggers.ctjs.minecraft.wrappers.objects.Entity
import com.chattriggers.ctjs.minecraft.wrappers.objects.Particle
import com.chattriggers.ctjs.minecraft.wrappers.objects.PlayerMP
import com.chattriggers.ctjs.minecraft.wrappers.objects.block.Block
import com.chattriggers.ctjs.utils.kotlin.External
import com.chattriggers.ctjs.utils.kotlin.MCParticle
import com.mojang.blaze3d.vertex.IVertexBuilder
import net.minecraft.client.particle.IParticleRenderType
import net.minecraft.client.particle.ParticleManager
import net.minecraft.client.renderer.ActiveRenderInfo
import net.minecraft.client.world.ClientWorld
import net.minecraft.particles.BasicParticleType
import net.minecraft.particles.ParticleType
import net.minecraft.particles.ParticleTypes
import net.minecraft.util.ResourceLocation
import net.minecraft.util.SoundCategory
import net.minecraft.util.SoundEvent
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry

@External
object World {
    /**
     * Gets Minecraft's WorldClient object
     *
     * @return The Minecraft WorldClient object
     */
    @JvmStatic
    fun getWorld(): ClientWorld? = Client.getMinecraft().world

    @JvmStatic
    fun isLoaded(): Boolean = getWorld() != null

    /**
     * Play a sound at the player location.
     *
     * @param name   the name of the sound
     * @param volume the volume of the sound
     * @param pitch  the pitch of the sound
     */
    @JvmStatic
    fun playSound(name: String, volume: Float, pitch: Float) {
        val sound = SoundEvent(ResourceLocation("minecraft", name))
        Player.getPlayer()?.playSound(sound, volume, pitch)
    }

    /**
     * Play a record at location x, y, and z.<br></br>
     * Use "null" as name in the same location to stop record.
     *
     * @param name  the name of the sound/record
     * @param x     the x location
     * @param y     the y location
     * @param z     the z location
     */
    @JvmStatic
    @JvmOverloads
    fun playRecord(name: String, x: Double, y: Double, z: Double, volume: Float = 1F, pitch: Float = 1F, distanceDelay: Boolean = false) {
        val sound = SoundEvent(ResourceLocation("minecraft", name))
        getWorld()?.playSound(x, y, z, sound, SoundCategory.RECORDS, volume, pitch, distanceDelay)
    }

    @JvmStatic
    fun isRaining(): Boolean = getWorld()?.worldInfo?.isRaining ?: false

    @JvmStatic
    fun getRainingStrength(): Float = getWorld()?.rainingStrength ?: -1f

    @JvmStatic
    fun getTime(): Long = getWorld()?.gameTime ?: -1L

    @JvmStatic
    fun getDifficulty(): String = getWorld()?.difficulty.toString()

    @JvmStatic
    fun getMoonPhase(): Int = getWorld()?.moonPhase ?: -1

    @JvmStatic
    fun getSeed(): Long = getWorld()?.seed ?: -1L

    @JvmStatic
    fun getType(): String? {
        return getWorld()?.worldType?.name
    }

    /**
     * Gets the [Block] at a location in the world.
     *
     * @param x the x position
     * @param y the y position
     * @param z the z position
     * @return the [Block] at the location
     */
    @JvmStatic
    fun getBlockAt(x: Int, y: Int, z: Int): Block {
        val blockPos = BlockPos(x, y, z)
        val blockState = getWorld()?.getBlockState(blockPos) ?:
            throw IllegalStateException("Attempt to call World.getBlockAt(...) without a loaded world")

        return Block(blockState.block).setBlockPos(blockPos)
    }

    /**
     * Gets all of the players in the world, and returns their wrapped versions.
     *
     * @return the players
     */
    @JvmStatic
    fun getAllPlayers(): List<PlayerMP> = getWorld()?.players?.map { PlayerMP(it) } ?: listOf()

    /**
     * Gets a player by their username, must be in the currently loaded world!
     *
     * @param name the username
     * @return the player with said username
     */
    @JvmStatic
    fun getPlayerByName(name: String): PlayerMP? {
        return getWorld()?.players?.filter {
            it.name.unformattedComponentText == name
        }?.let(::PlayerMP)
    }

    @JvmStatic
    fun hasPlayer(name: String): Boolean = getWorld()?.players?.any { it.name.unformattedComponentText == name } ?: false

    @JvmStatic
    fun getChunkAt(x: Int, z: Int): Chunk {
        return Chunk(
            getWorld()?.getChunk(
                x, z
            ) ?: throw IllegalStateException("Attempt to call World.getChunk(...) without a loaded world")
        )
    }

    @JvmStatic
    fun getAllEntities(): List<Entity> = getWorld()?.allEntities?.map { Entity(it) } ?: listOf()

    /**
     * Gets every entity loaded in the world of a certain class
     *
     * @param clazz the class to filter for (Use `Java.type().class` to get this)
     * @return the entity list
     */
    @JvmStatic
    fun getAllEntitiesOfType(clazz: Class<*>): List<Entity> = getAllEntities().filter { it.entity.javaClass == clazz }

    /**
     * World border object to get border parameters
     */
    object border {
        /**
         * Gets the border center x location.
         *
         * @return the border center x location
         */
        @JvmStatic
        fun getCenterX(): Double = getWorld()?.worldBorder?.centerX ?:
            throw IllegalStateException("Attempt to call World.border.getCenterX() without a loaded world")

        /**
         * Gets the border center z location.
         *
         * @return the border center z location
         */
        @JvmStatic
        fun getCenterZ(): Double = getWorld()?.worldBorder?.centerZ ?:
            throw IllegalStateException("Attempt to call World.border.getCenterY() without a loaded world")

        /**
         * Gets the border size.
         *
         * @return the border size
         */
        @JvmStatic
        fun getSize(): Int = getWorld()?.worldBorder?.size ?:
            throw IllegalStateException("Attempt to call World.border.getCenterZ() without a loaded world")

        /**
         * Gets the border target size.
         *
         * @return the border target size
         */
        @JvmStatic
        fun getTargetSize(): Double = getWorld()?.worldBorder?.targetSize ?:
            throw IllegalStateException("Attempt to call World.border.getTargetSize() without a loaded world")

        /**
         * Gets the border time until the target size is met.
         *
         * @return the border time until target
         */
        @JvmStatic
        fun getTimeUntilTarget(): Long = getWorld()?.worldBorder?.timeUntilTarget ?:
            throw IllegalStateException("Attempt to call World.border.getTimeUntilTarget() without a loaded world")
    }

    /**
     * World spawn object for getting spawn location.
     */
    object spawn {
        /**
         * Gets the spawn x location.
         *
         * @return the spawn x location.
         */
        @JvmStatic
        fun getX(): Int = getWorld()?.spawnPoint?.x ?:
            throw IllegalStateException("Attempt to call World.spawn.getX() without a loaded world")

        /**
         * Gets the spawn y location.
         *
         * @return the spawn y location.
         */
        @JvmStatic
        fun getY(): Int = getWorld()?.spawnPoint?.y  ?:
            throw IllegalStateException("Attempt to call World.spawn.getY() without a loaded world")

        /**
         * Gets the spawn z location.
         *
         * @return the spawn z location.
         */
        @JvmStatic
        fun getZ(): Int = getWorld()?.spawnPoint?.z  ?:
            throw IllegalStateException("Attempt to call World.spawn.getZ() without a loaded world")
    }

    object particle {
        /**
         * Gets an array of all the different particle names you can pass
         * to [.spawnParticle]
         *
         * @return the array of name strings
         */
        @JvmStatic
        fun getParticleNames(): List<String> = Registry.PARTICLE_TYPE.mapNotNull { it.registryName?.namespace }

        /**
         * Spawns a particle into the world with the given attributes,
         * which can be configured further with the returned [Particle]
         *
         * @param particle the name of the particle to spawn, see [.getParticleNames]
         * @param x the x coordinate to spawn the particle at
         * @param y the y coordinate to spawn the particle at
         * @param z the z coordinate to spawn the particle at
         * @param xSpeed the motion the particle should have in the x direction
         * @param ySpeed the motion the particle should have in the y direction
         * @param zSpeed the motion the particle should have in the z direction
         * @return the newly spawned particle for further configuration
         */
        @JvmStatic
        fun spawnParticle(
            particle: String,
            x: Double,
            y: Double,
            z: Double,
            xSpeed: Double,
            ySpeed: Double,
            zSpeed: Double
        ): Particle? {
            val particleType = Registry.PARTICLE_TYPE
                .firstOrNull { it.registryName?.namespace == particle } as? BasicParticleType ?:
                throw IllegalArgumentException("Unknown particle name $particle")

            val fx = Client.mc.particles.addParticle(particleType, x, y, z, xSpeed, ySpeed, zSpeed) ?:
                throw IllegalArgumentException("Unknown error occurred while attempting to spawn " +
                    "particle of type $particle at ($x, $y, $z) with speed ($xSpeed, $ySpeed, $zSpeed)")

            return Particle(fx)
        }

        @JvmStatic
        fun spawnParticle(particle: MCParticle) {
            Client.mc.particles.addEffect(particle)
        }
    }
}
