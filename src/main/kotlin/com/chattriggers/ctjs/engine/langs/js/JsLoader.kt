package com.chattriggers.ctjs.engine.langs.js

import com.chattriggers.ctjs.engine.ILoader
import com.chattriggers.ctjs.engine.ILoader.Companion.modulesFolder
import com.chattriggers.ctjs.engine.PrimaryLoader
import com.chattriggers.ctjs.engine.langs.py.PyLoader
import com.chattriggers.ctjs.engine.module.Module
import com.chattriggers.ctjs.triggers.OnTrigger
import com.chattriggers.ctjs.utils.console.Console
import com.chattriggers.ctjs.utils.kotlin.ModuleLoader
import org.graalvm.polyglot.Context
import org.graalvm.polyglot.Source
import java.io.File

@ModuleLoader
object JsLoader : ILoader {
    override var triggers = mutableListOf<OnTrigger>()
    override val toRemove = mutableListOf<OnTrigger>()
    override val console by lazy { Console(this) }
    override val cachedModules = mutableListOf<Module>()

    override fun preload(modules: List<Module>) {
        cachedModules.clear()

        val providedLibsScript = saveResource(
                "/providedLibs.js",
                File(modulesFolder.parentFile,
                        "chattriggers-provided-libs.js"
                ),
                true
        )

        try {
            PrimaryLoader.scriptContext.eval("js", providedLibsScript)
        } catch (e: Exception) {
            console.printStackTrace(e)
        }
    }

    override fun load(module: Module) {
        loadFiles(module)

        cachedModules.add(module)
    }

    override fun loadExtra(module: Module) {
        if (cachedModules.any {
            it.name == module.name
        }) return

        cachedModules.add(module)

        loadFiles(module)
    }

    override fun loadFiles(module: Module) = try {
        val scriptFiles = module.getFilesWithExtension(".js")

        scriptFiles.forEach {
            val source = Source.newBuilder("js", it).build()

            PrimaryLoader.scriptContext.eval(source)
        }
    } catch (e: Exception) {
        PyLoader.console.out.println("Error loading module ${module.name}")
        PyLoader.console.printStackTrace(e)
    }

    override fun eval(code: String): Any? {
        return PrimaryLoader.scriptContext.eval("js", code)
    }

    override fun getLanguageName(): List<String> {
        return listOf("js")
    }

    override fun trigger(trigger: OnTrigger, method: Any, vararg args: Any?) {
        try {
            TODO()
        } catch (e: Exception) {
            console.printStackTrace(e)
            removeTrigger(trigger)
        }
    }

    override fun getModules(): List<Module> {
        return cachedModules
    }
}