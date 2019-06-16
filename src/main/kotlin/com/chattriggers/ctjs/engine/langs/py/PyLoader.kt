package com.chattriggers.ctjs.engine.langs.py

import com.chattriggers.ctjs.engine.ILoader
import com.chattriggers.ctjs.engine.ILoader.Companion.modulesFolder
import com.chattriggers.ctjs.engine.PrimaryLoader
import com.chattriggers.ctjs.engine.PrimaryLoader.console
import com.chattriggers.ctjs.engine.module.Module
import com.chattriggers.ctjs.triggers.OnTrigger
import com.chattriggers.ctjs.utils.console.Console
import com.chattriggers.ctjs.utils.kotlin.ModuleLoader
import org.graalvm.polyglot.Source
import org.graalvm.polyglot.Value
import java.io.File

@ModuleLoader
object PyLoader : ILoader {
    override var triggers = mutableListOf<OnTrigger>()
    override val toRemove = mutableListOf<OnTrigger>()
    override val cachedModules = mutableListOf<Module>()

    override fun preload(modules: List<Module>) {
        cachedModules.clear()

        val providedLibsScript = saveResource(
                "/provided_libs.py",
                File(modulesFolder.parentFile,
                        "chattriggers-provided-libs.py"
                ),
                true
        )

        try {
            PrimaryLoader.scriptContext.eval("python", providedLibsScript)
        } catch (e: Exception) {
            console.printStackTrace(e)
        }
    }

    override fun loadFiles(module: Module) = try {
        val scriptFiles = module.getFilesWithExtension(".py")

        scriptFiles.forEach {
            val source = Source.newBuilder("python", it).build()

            PrimaryLoader.scriptContext.eval(source)
        }
    } catch (e: Exception) {
        console.out.println("Error loading module ${module.name}")
        console.printStackTrace(e)
    }

    override fun eval(code: String): Any? {
        return PrimaryLoader.scriptContext.eval("python", code)
    }

    override fun getLanguageName(): List<String> {
        return listOf("py")
    }

    override fun trigger(trigger: OnTrigger, method: Value, vararg args: Any?) {
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
