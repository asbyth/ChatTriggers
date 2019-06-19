package com.chattriggers.ctjs.minecraft.objects

import com.chattriggers.ctjs.CTJS
import com.chattriggers.ctjs.minecraft.mixins.MixinSoundHandler
import com.chattriggers.ctjs.minecraft.wrappers.Client
import com.chattriggers.ctjs.minecraft.wrappers.Player
import com.chattriggers.ctjs.minecraft.wrappers.World
import com.chattriggers.ctjs.utils.kotlin.External
import com.chattriggers.ctjs.utils.kotlin.SoundCategory
import net.minecraft.client.audio.SoundManager
import net.minecraftforge.fml.relauncher.ReflectionHelper
import org.graalvm.polyglot.Value
import paulscode.sound.SoundSystem

import java.io.File
import java.net.MalformedURLException

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
class Sound(private val config: Value) {
    private var sndSystem: SoundSystem? = null
    private val source: String = config.getMember("source").asString()
    var isListening = false

    init {
        if (World.isLoaded()) {
            loadSndSystem()

            try {
                bootstrap()
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            }

        } else {
            isListening = true
        }

        CTJS.sounds.add(this)
    }

    fun onWorldLoad() {
        isListening = false

        println("Loading sound system")
        loadSndSystem()
        println("sound system loaded: " + sndSystem!!)

        try {
            println("Bootstrapping")
            bootstrap()
        } catch (exc: MalformedURLException) {
            exc.printStackTrace()
        }

    }

    private fun loadSndSystem() {
        val sndManager = (Client.getMinecraft().soundHandler as MixinSoundHandler).sndManager

        sndSystem = ReflectionHelper.getPrivateValue<SoundSystem, SoundManager>(
            SoundManager::class.java,
            sndManager,
            "sndSystem",
            "field_148620_e"
        )
    }

    @Throws(MalformedURLException::class)
    private fun bootstrap() {
        val source = config.getMember("source").asString()
        val priority = config.getMember("priority").let { if (it.isBoolean) it.asBoolean() else false }
        val loop = config.getMember("loop").let { if (it.isBoolean) it.asBoolean() else false }
        val stream = config.getMember("stream").let { if (it.isBoolean) it.asBoolean() else false }

        val url = File("${CTJS.assetsDir.absolutePath}\\$source").toURI().toURL()
        val x = config.getMember("x").let { if (it.isNumber) it.asDouble() else Player.getX() }
        val y = config.getMember("y").let { if (it.isNumber) it.asDouble() else Player.getX() }
        val z = config.getMember("z").let { if (it.isNumber) it.asDouble() else Player.getX() }
        val attModel = config.getMember("attenuation").let { if (it.isNumber) it.asInt() else 1 }
        val distOrRoll = 16

        if (stream) {
            sndSystem!!.newStreamingSource(
                priority,
                source,
                url,
                source,
                loop,
                x.toFloat(),
                y.toFloat(),
                z.toFloat(),
                attModel,
                distOrRoll.toFloat()
            )
        } else {
            sndSystem!!.newSource(
                priority,
                source,
                url,
                source,
                loop,
                x.toFloat(),
                y.toFloat(),
                z.toFloat(),
                attModel,
                distOrRoll.toFloat()
            )
        }

        if (config.hasMember("volume")) {
            setVolume(config.getMember("volume").asDouble())
        }

        if (config.hasMember("pitch")) {
            setPitch(config.getMember("pitch").asDouble())
        }

        if (config.hasMember("category")) {
            setCategory(config.getMember("volume").asString())
        }
    }

    /**
     * Sets the category of this sound, making it respect the Player's sound volume sliders.
     * Options are: MASTER, MUSIC, RECORDS, WEATHER, BLOCKS, MOBS, ANIMALS, PLAYERS, and AMBIENT
     *
     * @param category the category
     */
    fun setCategory(category: String) = apply {
        val category1 = SoundCategory.valueOf(category.toUpperCase())
        setVolume(Client.getMinecraft().gameSettings.getSoundLevel(category1).toDouble())
    }

    /**
     * Sets this sound's volume.
     * Will override the category if called after [.setCategory], but not if called before.
     *
     * @param volume New volume, float value ( 0.0f - 1.0f ).
     */
    fun setVolume(volume: Double) = apply { sndSystem!!.setVolume(this.source, volume.toFloat()) }

    /**
     * Updates the position of this sound
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     */
    fun setPosition(x: Double, y: Double, z: Double) = apply { sndSystem!!.setPosition(this.source, x.toFloat(), y.toFloat(), z.toFloat()) }

    /**
     * Sets this sound's pitch.
     *
     * @param pitch A float value ( 0.5f - 2.0f ).
     */
    fun setPitch(pitch: Double) = apply { sndSystem!!.setPitch(this.source, pitch.toFloat()) }

    /**
     * Sets the attenuation (fade out over space) of the song.
     * Models are:
     *  NONE(0) - no fade
     *  ROLLOFF(1) - this is the default, meant to be somewhat realistic
     *  LINEAR(2) - fades out linearly, as the name implies
     *
     * @param model the model
     */
    fun setAttenuation(model: Int) = apply { sndSystem!!.setAttenuation(this.source, model) }

    /**
     * Plays/resumes the sound
     */
    fun play() { sndSystem!!.play(this.source) }

    /**
     * Pauses the sound, to be resumed later
     */
    fun pause() { sndSystem!!.pause(this.source) }

    /**
     * Completely stops the song
     */
    fun stop() { sndSystem!!.stop(this.source) }

    /**
     * I really don't know what this does
     */
    fun rewind() { sndSystem!!.rewind(this.source) }
}
