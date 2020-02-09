package com.chattriggers.ctjs.minecraft.libs.renderer

import com.chattriggers.ctjs.CTJS
import com.chattriggers.ctjs.utils.kotlin.External
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.client.renderer.texture.NativeImage
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.eventbus.api.SubscribeEvent
import java.awt.image.BufferedImage
import java.io.File
import java.net.URL
import javax.imageio.ImageIO

@External
class Image(val image: NativeImage) {
    private lateinit var texture: DynamicTexture
    private val textureWidth = image.width
    private val textureHeight = image.height
    private var textureUploaded = false

    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    @JvmOverloads
    constructor(name: String, url: String? = null) : this(getNativeImage(name, url))

    fun getTextureWidth(): Int = this.textureWidth

    fun getTextureHeight(): Int = this.textureHeight

    fun getTexture(): DynamicTexture {
        if (!textureUploaded) {
            // We're trying to access the texture before initialization. Presumably, the game overlay render event
            // hasn't fired yet so we haven't loaded the texture. Let's hope this is a rendering context!
            try {
                texture = DynamicTexture(image)
                textureUploaded = true
                image.untrack()

                MinecraftForge.EVENT_BUS.unregister(this)
            } catch (e: Exception) {
                // Unlucky. This probably wasn't a rendering context.
                println("Trying to bake texture in a non-rendering context.")

                throw e
            }
        }

        return this.texture
    }

    @SubscribeEvent
    fun onRender(event: RenderGameOverlayEvent) {
        if (!textureUploaded) {
            texture = DynamicTexture(image)
            textureUploaded = true
            image.untrack()

            MinecraftForge.EVENT_BUS.unregister(this)
        }
    }

    @JvmOverloads
    fun draw(
        x: Double, y: Double,
        width: Double = this.textureWidth.toDouble(),
        height: Double = this.textureHeight.toDouble()
    ) = apply {
        if (!textureUploaded) return@apply

        Renderer.drawImage(this, x, y, width, height)
    }

    companion object {
        private fun getNativeImage(name: String, url: String? = null): NativeImage {
            val resourceFile = File(CTJS.assetsDir, name)

            if (resourceFile.exists()) {
                return NativeImage.read(resourceFile.inputStream())
            }

            val image = URL(url).openStream()
            resourceFile.writeBytes(image.readBytes())
            return NativeImage.read(image)
        }
    }
}