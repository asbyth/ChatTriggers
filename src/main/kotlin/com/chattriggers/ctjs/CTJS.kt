package com.chattriggers.ctjs

import com.chattriggers.ctjs.engine.langs.js.JSLoader
import com.chattriggers.ctjs.engine.module.ModuleManager
import com.chattriggers.ctjs.loader.UriScheme
import com.chattriggers.ctjs.minecraft.libs.FileLib
import com.chattriggers.ctjs.minecraft.listeners.ChatListener
import com.chattriggers.ctjs.minecraft.listeners.ClientListener
import com.chattriggers.ctjs.minecraft.listeners.WorldListener
import com.chattriggers.ctjs.minecraft.objects.Sound
import com.chattriggers.ctjs.minecraft.objects.gui.GuiHandler
import com.chattriggers.ctjs.minecraft.wrappers.CPS
import com.chattriggers.ctjs.minecraft.wrappers.Client
import com.chattriggers.ctjs.minecraft.wrappers.Player
import com.chattriggers.ctjs.triggers.TriggerType
import com.chattriggers.ctjs.utils.UpdateChecker
import com.chattriggers.ctjs.utils.capes.LayerCape
import com.chattriggers.ctjs.utils.config.Config
import com.google.gson.JsonParser
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.loading.FMLPaths
import org.apache.commons.codec.digest.DigestUtils
import org.apache.logging.log4j.LogManager
import thedarkcolour.kotlinforforge.forge.MOD_CONTEXT
import java.io.File
import java.io.FileReader
import kotlin.concurrent.thread

// The value here should match an entry in the META-INF/mods.toml file
@Mod("ctjs")
object CTJS {
    // Directly reference a log4j logger.
    private val LOGGER = LogManager.getLogger()
    val configLocation: File = FMLPaths.CONFIGDIR.get().toFile()
    val assetsDir = File(configLocation, "ChatTriggers/images/").apply { mkdirs() }
    val sounds = mutableListOf<Sound>()

    init {
        // Register the setup method for modloading
        MOD_CONTEXT.getEventBus().addListener(::setup)
        // Register the doClientStuff method for modloading
        MOD_CONTEXT.getEventBus().addListener(::doClientStuff)

        listOf(ChatListener, WorldListener, CPS, GuiHandler, ClientListener, UpdateChecker).forEach {
            MinecraftForge.EVENT_BUS.register(it)
        }

        listOf(JSLoader).forEach {
            ModuleManager.loaders.add(it)
        }
    }

    private fun setup(event: FMLCommonSetupEvent) {
        UriScheme.installUriScheme()
        UriScheme.createSocketListener()
    }

    private fun doClientStuff(event: FMLClientSetupEvent) {
        thread {
            loadConfig()
        }

        val sha256uuid = DigestUtils.sha256Hex(Player.getUUID())

        try {
            FileLib.getUrlContent("https://www.chattriggers.com/tracker/?uuid=$sha256uuid")
        } catch (e: Exception) {
            e.print()
        }

        Reference.conditionalThread {
            ModuleManager.load(true)
        }

        registerHooks()

        Client.getMinecraft().renderManager.skinMap.values.forEach {
            it.addLayer(LayerCape(it))
        }
    }

    fun loadConfig(): Boolean {
        try {
            val parser = JsonParser()
            val obj = parser.parse(
                FileReader(
                    File(this.configLocation, "ChatTriggers.json")
                )
            ).asJsonObject

            Config.load(obj)

            return true
        } catch (exception: Exception) {
            val place = File(this.configLocation, "ChatTriggers.json")
            place.delete()
            place.createNewFile()
            saveConfig()
        }

        return false
    }

    fun saveConfig() = Config.save(File(this.configLocation, "ChatTriggers.json"))

    private fun registerHooks() {
        Runtime.getRuntime().addShutdownHook(
            Thread { TriggerType.GAME_UNLOAD::triggerAll }
        )
    }
}