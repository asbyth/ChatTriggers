package com.chattriggers.ctjs.engine

import com.chattriggers.ctjs.engine.module.Module
import com.chattriggers.ctjs.triggers.TriggerType
import com.chattriggers.ctjs.utils.config.Config
import com.chattriggers.ctjs.utils.console.Console

object PrimaryLoader {
    private val loaders = Lang.values().map { Loader(it) }

    fun getLoader(lang: Lang): Loader {
        return loaders.first { it.getLanguageName() == lang.langName }
    }

    fun initialize(modules: List<Module>) {
        loaders.forEach { it.initialize() }

        val jars = modules.map {
            it.getFilesWithExtension(".jar")
        }.flatten().map {
            it.absolutePath
        }

        // TODO: This won't add jars for all classpaths now that
        // contexts are split up into their own languages
        getLoader(Lang.JS).synchronized {
            jars.forEach {
                eval("js", "Java.addToClasspath(\"$it\")")
            }
        }

        getLoader(Lang.JS).synchronized {
            try {
                eval("js", "x")
            } catch (e: Exception) {
                // Do nothing. We want this to error for some godforsaken reason.
                // Don't question the madness that we have been driven to in order
                // to fix this error, we simply had to sell our souls to the devil.
            }
        }

        if (Config.clearConsoleOnLoad)
            Console.clearConsole()

        loaders.forEach { it.preload() }
        loaders.forEach { it.load(modules) }
    }

    fun loadExtra(module: Module) {
        loaders.forEach { it.loadExtra(module) }
    }

    fun unloadTriggers() = loaders.forEach {
        it.clearTriggers()
    }

    fun trigger(type: TriggerType, vararg arguments: Any?) = loaders.forEach {
        it.fireTrigger(type, *arguments)
    }
}