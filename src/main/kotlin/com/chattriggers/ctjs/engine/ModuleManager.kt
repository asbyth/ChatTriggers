package com.chattriggers.ctjs.engine

import com.chattriggers.ctjs.Reference
import com.chattriggers.ctjs.Reference.timeout
import com.chattriggers.ctjs.engine.module.Module
import com.chattriggers.ctjs.minecraft.libs.FileLib
import com.chattriggers.ctjs.triggers.TriggerType
import com.chattriggers.ctjs.utils.config.Config
import com.chattriggers.ctjs.utils.console.Console
import java.io.File
import java.lang.IllegalStateException

object ModuleManager {
    val loaders = mutableListOf<ILoader>()
    val generalConsole = Console(null)
    var cachedModules = listOf<Module>()

    fun importModule(moduleName: String) {
        PrimaryLoader.importModule(moduleName, true)
    }

    fun deleteModule(name: String): Boolean {
        if (FileLib.deleteDirectory(File(Config.modulesFolder, name))) {
            cachedModules.filter {
                return@filter it.name == name
            }
            timeout { Reference.load() }
            return true
        }
        return false
    }

    fun load(updateCheck: Boolean) {
        val modules = PrimaryLoader.fetchModules(updateCheck)
        cachedModules = modules
        PrimaryLoader.load(modules)

        loaders.forEach {
            it.preload(modules)
        }

        modules.forEach { module ->
            val loader = loaders.firstOrNull {
                it.getLanguageName().contains(module.metadata.language)
            } ?: throw IllegalStateException("No loader found for language '${module.metadata.language}' " +
                    "in module '${module.name}'")

            loader.load(module)
        }
    }

    fun load(module: Module) {
        val list = mutableListOf<Module>()
        list.addAll(cachedModules)
        list.add(module)
        cachedModules = list

        loaders.forEach {
            it.loadExtra(module)
        }
    }

    fun unload() {
        loaders.forEach {
            it.clearTriggers()
        }
    }

    fun trigger(type: TriggerType, vararg arguments: Any?) {
        loaders.forEach {
            it.exec(type, *arguments)
        }
    }

    fun getConsole(language: String): Console {
        return loaders.firstOrNull {
            it.getLanguageName().contains(language)
        }?.console ?: generalConsole
    }
}