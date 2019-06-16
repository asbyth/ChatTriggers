package com.chattriggers.ctjs.engine

import com.chattriggers.ctjs.engine.langs.py.PyLoader
import com.chattriggers.ctjs.engine.module.Module
import com.chattriggers.ctjs.triggers.OnTrigger
import com.chattriggers.ctjs.triggers.TriggerType
import com.chattriggers.ctjs.utils.config.Config
import com.chattriggers.ctjs.utils.console.Console
import org.apache.commons.io.FileUtils
import java.io.File

interface ILoader {
    var triggers: MutableList<OnTrigger>
    val toRemove: MutableList<OnTrigger>
    val console: Console
    val cachedModules: MutableList<Module>

    /**
     * Should configure the loader's script engine, as well as any initial
     * processing or loading that needs to operate on all modules, such as
     * jar loading.
     *
     * Note that this function is given every user module, not just the ones
     * that match this loader's language.
     */
    fun preload(modules: List<Module>)

    /**
     * Loads a module into the loader. This function is called with modules
     * of only this loader's language, in the correct order that they need
     * to be loaded in.
     *
     * This function is meant to be called after a /ct load, as opposed
     * to [loadExtra], which is meant to be called when importing modules.
     */
    fun load(module: Module) {
        loadFiles(module)

        cachedModules.add(module)
    }

    /**
     * Loads a module into the loader. This differs from [load] in that
     * it is meant to be called only when importing modules as it differs
     * semantically in that it should ignore the load if the user already
     * has the module imported.
     */
    fun loadExtra(module: Module) {
        if (cachedModules.any {
                it.name == module.name
            }) return

        cachedModules.add(module)

        loadFiles(module)
    }

    fun loadFiles(module: Module)

    /**
     * Tells the loader that it should activate all triggers
     * of a certain type with the specified arguments.
     */
    fun exec(type: TriggerType, vararg args: Any?) {
        val newTriggers = triggers.toMutableList()
        newTriggers.removeAll(toRemove)
        toRemove.clear()

        newTriggers.filter {
            it.type == type
        }.forEach {
            it.trigger(*args)
        }

        triggers = newTriggers
    }

    /**
     * Gets the result from evaluating a certain line of code in this loader
     */
    fun eval(code: String): Any?

    /**
     * Adds a trigger to this loader to be activated during the game
     */
    fun addTrigger(trigger: OnTrigger) {
        triggers.add(trigger)

        triggers.sortBy {
            it.priority.ordinal
        }
    }

    /**
     * Removes all triggers
     */
    fun clearTriggers() {
        triggers.forEach { it.unregister() }
        triggers.clear()
    }

    /**
     * Returns the names of this specific loader's implemented languages
     */
    fun getLanguageName(): List<String>

    /**
     * Actually calls the method for this trigger in this loader
     */
    fun trigger(trigger: OnTrigger, method: Any, vararg args: Any?)

    /**
     * Removes a trigger from the current pool
     */
    fun removeTrigger(trigger: OnTrigger) {
        toRemove.add(trigger)
    }

    /**
     * Gets a list of all currently loaded modules
     */
    fun getModules(): List<Module>

    /**
     * Save a resource to the OS's filesystem from inside the jar
     * @param resourceName name of the file inside the jar
     * @param outputFile file to save to
     * @param replace whether or not to replace the file being saved to
     */
    fun saveResource(resourceName: String?, outputFile: File, replace: Boolean): String {
        if (resourceName == null || resourceName == "") {
            throw IllegalArgumentException("ResourcePath cannot be null or empty")
        }

        val parsedResourceName = resourceName.replace('\\', '/')
        val resource = this.javaClass.getResourceAsStream(parsedResourceName)
                ?: throw IllegalArgumentException("The embedded resource '$parsedResourceName' cannot be found.")

        val res = resource.bufferedReader().readText()
        FileUtils.write(outputFile, res)
        return res
    }

    companion object {
        internal val modulesFolder = File(Config.modulesFolder)

        internal fun getFoldersInDir(dir: File): List<File> {
            if (!dir.isDirectory) return emptyList()

            return dir.listFiles().filter {
                it.isDirectory
            }
        }
    }
}
