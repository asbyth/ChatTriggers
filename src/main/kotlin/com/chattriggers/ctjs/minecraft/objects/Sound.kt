package com.chattriggers.ctjs.minecraft.objects

import com.chattriggers.ctjs.CTJS
import com.chattriggers.ctjs.minecraft.wrappers.Client
import com.chattriggers.ctjs.minecraft.wrappers.Player
import com.chattriggers.ctjs.minecraft.wrappers.World
import com.chattriggers.ctjs.utils.kotlin.External
import com.chattriggers.ctjs.utils.kotlin.MCSound
import net.minecraft.client.audio.*
import net.minecraft.client.audio.Sound
import net.minecraft.util.ResourceLocation
import net.minecraft.util.SoundCategory
import org.mozilla.javascript.NativeObject

/**
 * <p>
 *     Instances a new Sound with certain properties. These properties
 *     should be passed through as a normal JavaScript object.
 * </p>
 *
 * <p>
 *     REQUIRED:<br>
 *     &emsp;source (String) - filename, relative to ChatTriggers assets directory
 * </p>
 *
 * <p>
 *     OPTIONAL:<br>
 *     &emsp;priority (boolean) - whether or not this sound should be prioritized, defaults to false<br>
 *     &emsp;loop (boolean) - whether or not to loop this sound over and over, defaults to false<br>
 *     &emsp;stream (boolean) - whether or not to stream this sound rather than preload it (should be true for large files), defaults to false
 * </p>
 *
 * <p>
 *     CONFIGURABLE (can be set in config object, or changed later, but MAKE SURE THE WORLD HAS LOADED)<br>
 *     &emsp;category (String) - which category this sound should be a part of, see {@link #setCategory(String)}.<br>
 *     &emsp;volume (float) - volume of the sound, see {@link #setVolume(float)}<br>
 *     &emsp;pitch (float) - pitch of the sound, see {@link #setPitch(float)}<br>
 *     &emsp;x, y, z (float) - location of the sound, see {@link #setPosition(float, float, float)}. Defaults to the players position.<br>
 *     &emsp;attenuation (int) - fade out model of the sound, see {@link #setAttenuation(int)}<br>
 * </p>
 *
 * @param config the JavaScript config object
 */
@External
class Sound(private val config: NativeObject) {
    private val sndManager = Client.getMinecraft().soundHandler.sndManager
    private val sound: ISound
    var isListening = false

    private val source: String = config["source"] as? String ?: throw IllegalArgumentException("Sound source is null.")
    private var volume: Float = 1f
    private var pitch: Float = 1f
    private var category: SoundCategory = SoundCategory.MASTER
    private var x = 0f
    private var y = 0f
    private var z = 0f
    private var stream = false
    private var weight = 0
    private var attDist = 0
    private var attType = ISound.AttenuationType.NONE

    init {
        if (!World.isLoaded()) {
            isListening = true
        }

        CTJS.sounds.add(this)

        stream = config.getOrDefault("stream", false) as Boolean
        x = (config.getOrDefault("x", Player.getX()) as Double).toFloat()
        y = (config.getOrDefault("y", Player.getY()) as Double).toFloat()
        z = (config.getOrDefault("z", Player.getZ()) as Double).toFloat()
        volume = (config.getOrDefault("volume", 1.0) as Double).toFloat()
        pitch = (config.getOrDefault("pitch", 1.0) as Double).toFloat()
        // TODO
        // category = SoundCategory.valueOf(config.getOrDefault("category", "MASTER") as String)
        weight = config.getOrDefault("weight", 1) as Int
        attDist = config.getOrDefault("attenuationDistance", 0) as Int

        sound = object : ISound {
            override fun getSound() = MCSound(
                this@Sound.source,
                this@Sound.volume,
                this@Sound.pitch,
                weight,
                Sound.Type.FILE,
                stream,
                true,
                attDist
            )

            override fun getPitch() = this@Sound.pitch

            override fun createAccessor(handler: SoundHandler): SoundEventAccessor? {
                return handler.getAccessor(ResourceLocation(source))
            }

            override fun getX() = this@Sound.x

            override fun getY() = this@Sound.y

            override fun getZ() = this@Sound.z

            override fun getCategory() = this@Sound.category

            override fun getVolume() = this@Sound.volume

            override fun getRepeatDelay() = 0

            override fun getSoundLocation() = ResourceLocation(source)

            override fun getAttenuationType() = ISound.AttenuationType.LINEAR

            override fun isGlobal() = false

            override fun canRepeat() = true
        }
    }

    // TODO
    // /**
    //  * Sets the category of this sound, making it respect the Player's sound volume sliders.
    //  * Options are: master, music, record, weather, block, hostile, neutral, player, and ambient
    //  *
    //  * @param category the category
    //  */
    // fun setCategory(category: String) = apply {
    //     category = SoundCategory::class.java.declaredFields.find { it.name == category.toUpperCase() }
    //     setVolume(Client.getMinecraft().gameSettings.getSoundLevel(category1))
    // }

    /**
     * Sets this sound's volume.
     * Will override the category if called after [.setCategory], but not if called before.
     *
     * @param volume New volume, float value ( 0.0f - 1.0f ).
     */
    fun setVolume(volume: Float) = apply {
        this.volume = volume
    }

    /**
     * Updates the position of this sound
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     */
    fun setPosition(x: Float, y: Float, z: Float) = apply {
        this.x = x
        this.y = y
        this.z = z
    }

    /**
     * Sets this sound's pitch.
     *
     * @param pitch A float value ( 0.5f - 2.0f ).
     */
    fun setPitch(pitch: Float) = apply {
        this.pitch = pitch
    }

    /**
     * Sets the attenuation (fade out over space) of the song.
     * Models are:
     *  NONE(0) - no fade
     *  ROLLOFF(1) - this is the default, meant to be somewhat realistic
     *  LINEAR(2) - fades out linearly, as the name implies
     *
     * @param model the model
     */
    fun setAttenuation(model: Int) = apply {
        this.attType = if (model == 0)
            ISound.AttenuationType.NONE
        else
            ISound.AttenuationType.LINEAR
    }

    /**
     * Plays/resumes the sound
     */
    fun play() {
        sndManager.play(sound)
    }

    /**
     * Pauses the sound, to be resumed later
     */
    fun pause() {
        // TODO: Why does this method not take an arg???
        sndManager.pause()
    }

    /**
     * Completely stops the song
     */
    fun stop() {
        sndManager.stop(sound)
    }

    /**
     * I really don't know what this does
     */
    fun rewind() {
        // TODO
    }
}
