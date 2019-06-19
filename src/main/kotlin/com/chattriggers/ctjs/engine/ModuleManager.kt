package com.chattriggers.ctjs.engine

import com.chattriggers.ctjs.Reference
import com.chattriggers.ctjs.engine.module.Module
import com.chattriggers.ctjs.minecraft.libs.FileLib
import com.chattriggers.ctjs.triggers.TriggerType
import com.chattriggers.ctjs.utils.config.Config
import java.io.File
import kotlin.concurrent.thread

object ModuleManager {
    val loaders = Lang.values().map { Loader(it) }
    var cachedModules = listOf<Module>()

    fun getLoader(lang: Lang): Loader {
        return loaders.first { it.getLanguageName() == lang.langName }
    }

    fun importModule(moduleName: String) {
        PrimaryLoader.importModule(moduleName, true)
    }

    fun deleteModule(name: String): Boolean {
        if (FileLib.deleteDirectory(File(Config.modulesFolder, name))) {
            cachedModules.filter {
                return@filter it.name == name
            }
            Reference.loadCT()
            return true
        }
        return false
    }

    fun loadModules(updateCheck: Boolean, asCommand: Boolean = false) {
        val modules = PrimaryLoader.fetchModules(updateCheck)
        cachedModules = modules
        PrimaryLoader.initialize(modules)

        try {
            PrimaryLoader.scriptContext.eval("js", "x")
        } catch (e: Exception) {
            // Do nothing. We want this to error for some godforsaken reason.
            // Don't question the madness that we have been driven to in order
            // to fix this error, we simply had to sell our souls to the devil.
        }

        if (Config.clearConsoleOnLoad)
            PrimaryLoader.console.clearConsole()

        loaders.forEach { it.preload() }
        loaders.forEach { it.load(modules) }
    }

    fun loadModule(module: Module) {
        val list = mutableListOf<Module>()
        list.addAll(cachedModules)
        list.add(module)
        cachedModules = list

        loaders.forEach {
            it.loadExtra(module)
        }
    }

    fun unloadTriggers() {
        loaders.forEach {
            it.clearTriggers()
        }
    }

    fun trigger(type: TriggerType, vararg arguments: Any?) {
        loaders.forEach {
            it.exec(type, *arguments)
        }
    }
}