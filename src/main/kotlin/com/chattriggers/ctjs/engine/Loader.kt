package com.chattriggers.ctjs.engine

import com.chattriggers.ctjs.engine.module.Module
import com.chattriggers.ctjs.triggers.OnTrigger
import com.chattriggers.ctjs.triggers.TriggerType
import com.chattriggers.ctjs.utils.config.Config
import com.chattriggers.ctjs.utils.console.Console
import org.apache.commons.io.FileUtils
import org.graalvm.polyglot.Context
import org.graalvm.polyglot.Source
import org.graalvm.polyglot.Value
import java.io.File

class Loader(private val language: Lang) {
    var triggers: MutableList<OnTrigger> = mutableListOf()
    private val toRemove: MutableList<OnTrigger> = mutableListOf()
    private val cachedModules: MutableList<Module> = mutableListOf()
    private var scriptContext: Context = instanceScriptContext()

    fun synchronized(block: Context.() -> Any?) = synchronized(scriptContext) {
        block(scriptContext)
    }

    fun initialize() {
        synchronized {
            try {
                close()
            } catch (e: Exception) {
                e.printStackTrace()
                println("Error closing context, probably Ruby")
            }

            scriptContext = instanceScriptContext()
            null
        }
    }

    /**
     * Should configure the loader's script engine, as well as any initial
     * processing or loading that needs to operate on all modules, such as
     * jar loading.
     */
    fun preload() {
        cachedModules.clear()

        val providedLibsScript = saveResource(
                "/${language.providedLibsName}",
                File(modulesFolder.parentFile,
                        "chattriggers-provided-libs.${language.extension}"
                )
        )

        synchronized {
            try {
                println("Evaling the following in ${language.graalName}:")
                println(providedLibsScript)
                eval(language.graalName, providedLibsScript)
            } catch (e: Exception) {
                Console.out.println("Error loading provided libs for language ${language.langName}")
                Console.printStackTrace(e)
            }
        }
    }

    /**
     * Loads a module into the loader. This function is called with modules
     * of only this loader's language, in the correct order that they need
     * to be loaded in.
     *
     * This function is meant to be called after a /ct load, as opposed
     * to [loadExtra], which is meant to be called when importing modules.
     */
    fun load(modules: List<Module>) {
        modules.forEach { loadFiles(it) }

        cachedModules.addAll(modules)
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

    private fun loadFiles(module: Module) = synchronized {
        try {
            val scriptFiles = module.getFilesWithExtension(".${language.extension}")

            scriptFiles.forEach {
                val source = Source.newBuilder(language.graalName, it).build()

                eval(source)
            }
        } catch (e: Exception) {
            Console.out.println("Error loading module ${module.name}")
            Console.printStackTrace(e)
        }
    }

    /**
     * Tells the loader that it should activate all triggers
     * of a certain type with the specified arguments.
     */
    fun fireTrigger(type: TriggerType, vararg args: Any?) {
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
    fun getLanguageName(): String = language.langName

    /**
     * Actually calls the method for this trigger in this loader
     */
    fun trigger(trigger: OnTrigger, method: Value, vararg args: Any?) {
        try {
            method.executeVoid(*args)
        } catch (e: Exception) {
            Console.printStackTrace(e)
            removeTrigger(trigger)
        }
    }

    /**
     * Removes a trigger from the current pool
     */
    fun removeTrigger(trigger: OnTrigger) {
        toRemove.add(trigger)
    }

    /**
     * Save a resource to the OS's filesystem from inside the jar
     * @param resourceName name of the file inside the jar
     * @param outputFile file to save to
     * @param replace whether or not to replace the file being saved to
     */
    private fun saveResource(resourceName: String?, outputFile: File): String {
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

    private fun instanceScriptContext() = Context
            .newBuilder(language.graalName)
            .allowAllAccess(true)
            .out(Console.out)
            .build()

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
