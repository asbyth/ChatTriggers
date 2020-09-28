package com.chattriggers.ctjs

import com.chattriggers.ctjs.commands.Command
import com.chattriggers.ctjs.commands.CommandHandler
import com.chattriggers.ctjs.engine.module.ModuleManager
import com.chattriggers.ctjs.minecraft.libs.ChatLib
import com.chattriggers.ctjs.minecraft.libs.renderer.Renderer
import com.chattriggers.ctjs.minecraft.objects.display.DisplayHandler
import com.chattriggers.ctjs.minecraft.objects.gui.GuiHandler
import com.chattriggers.ctjs.minecraft.objects.message.Message
import com.chattriggers.ctjs.minecraft.wrappers.World
import com.chattriggers.ctjs.mixin.CommandHandlerAccessor
import com.chattriggers.ctjs.triggers.TriggerType
import com.chattriggers.ctjs.utils.config.Config
import com.chattriggers.ctjs.utils.kotlin.External
import com.chattriggers.ctjs.utils.kotlin.times
import com.google.common.reflect.ClassPath
import me.ntrrgc.tsGenerator.TypeScriptGenerator
import net.minecraftforge.client.ClientCommandHandler
import java.io.File
import java.lang.Math.round
import kotlin.concurrent.thread
import kotlin.math.roundToInt
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

@External
object Reference {
    const val MODID = "ct.js"
    const val MODNAME = "ChatTriggers"
    const val MODVERSION = "1.3.0"

    var isLoaded = true

    @Deprecated("Does not provide any additional functionality", ReplaceWith("loadCT"))
    @JvmStatic
    fun reloadCT() = loadCT()

    @JvmStatic
    fun unloadCT(asCommand: Boolean = true) {
        TriggerType.WORLD_UNLOAD.triggerAll()
        TriggerType.GAME_UNLOAD.triggerAll()

        isLoaded = false

        DisplayHandler.clearDisplays()
        GuiHandler.clearGuis()
        CommandHandler.getCommandList().clear()
        ModuleManager.teardown()

        if (asCommand) {
            ChatLib.chat("&7Unloaded all of ChatTriggers")
            isLoaded = false
        }
    }

    @JvmStatic
    fun loadCT() {
        if (!isLoaded) return

        unloadCT(false)

        ChatLib.chat("&cReloading ct.js scripts...")
        (ClientCommandHandler.instance as CommandHandlerAccessor).also { instance ->
            instance.getCommandSet().removeIf { it is Command }
            instance.getCommandMap().entries.removeIf { it.value is Command }
        }

        CTJS.loadConfig()

        printLoadCompletionStatus(0f)

        conditionalThread {
            ModuleManager.setup()
            ModuleManager.entryPass(completionListener = ::printLoadCompletionStatus)

            ChatLib.chat("&aDone reloading scripts!")

            isLoaded = true

            TriggerType.GAME_LOAD.triggerAll()
            if (World.isLoaded())
                TriggerType.WORLD_LOAD.triggerAll()
        }
    }

    private fun printLoadCompletionStatus(percentComplete: Float) {
        val completionInteger = (percentComplete * 100).roundToInt()
        val prefix = "$completionInteger% ["
        val postfix = "]"

        val charWidth = Renderer.getStringWidth("=")
        val availableWidth = ChatLib.getChatWidth() - Renderer.getStringWidth(prefix + postfix)
        val correctLength = availableWidth / charWidth
        val completedLength = (percentComplete * correctLength).roundToInt()
        val fullWidth = "=" * completedLength
        val spaceWidth = Renderer.getStringWidth(" ")
        val spaceLeft = (availableWidth - completedLength * charWidth) / spaceWidth
        val padding = " " * spaceLeft

        val correctLine = "&c$prefix$fullWidth$padding$postfix"

        Message(correctLine).setChatLineId(28445).chat()
    }

    @JvmStatic
    fun conditionalThread(block: () -> Unit) {
        if (Config.threadedLoading) {
            thread { block() }
        } else {
            block()
        }
    }

    internal fun generateBindings() {
        val classpath = ClassPath.from(this::class.java.classLoader)
        val externalClasses = classpath.getTopLevelClassesRecursive("com.chattriggers.ctjs").map {
            it.load()
        }.filter { it.isAnnotationPresent(External::class.java) }.map { it.kotlin }

        val generator = TypeScriptGenerator(rootClasses = externalClasses)
        File(CTJS.assetsDir.parent, "typings.d.ts").apply { createNewFile() }.writeText(generator.definitionsText)
    }
}

fun Exception.print() {
    try {
        ModuleManager.generalConsole.printStackTrace(this)
    } catch (exception: Exception) {
        this.printStackTrace()
    }
}

fun String.print() {
    try {
        ModuleManager.generalConsole.out.println(this)
    } catch (exception: Exception) {
        println(this)
    }
}
