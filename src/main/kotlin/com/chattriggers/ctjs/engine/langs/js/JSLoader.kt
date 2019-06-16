package com.chattriggers.ctjs.engine.langs.js

import com.chattriggers.ctjs.engine.IBridge
import com.chattriggers.ctjs.engine.ILoader
import com.chattriggers.ctjs.engine.ILoader.Companion.modulesFolder
import com.chattriggers.ctjs.engine.module.Module
import com.chattriggers.ctjs.triggers.OnTrigger
import com.chattriggers.ctjs.utils.console.Console
import com.chattriggers.ctjs.utils.kotlin.ModuleLoader
import org.graalvm.polyglot.Context
import java.io.File

@ModuleLoader
object JSLoader : ILoader {
    override var triggers = mutableListOf<OnTrigger>()
    override val toRemove = mutableListOf<OnTrigger>()
    override val console by lazy { Console(this) }

    private val cachedModules = mutableListOf<Module>()
    private var scriptEngine = Context.newBuilder().allowHostAccess(true).build()

    override fun preload(modules: List<Module>) {
        cachedModules.clear()

        val jars = modules.filter {
            it.metadata.language == "js"
        }.map {
            it.getFilesWithExtension(".jar")
        }.flatten().map {
            it.absolutePath
        }

        scriptEngine = Context.newBuilder().allowHostAccess(true).build()

        jars.forEach {
            scriptEngine.eval("js", "Java.addToClasspath('$it')")
        }

        val script = saveResource(
                "/providedLibs.js",
                File(modulesFolder.parentFile,
                        "chattriggers-provided-libs.js"
                ),
                true
        )

        try {
            scriptEngine.eval("js", script)
        } catch (e: Exception) {
            console.printStackTrace(e)
        }
    }

    override fun load(module: Module) {
        val combinedScript = module.getFilesWithExtension(".js")
                .joinToString(separator = "\n") {
                    it.readText()
                }

        try {
            scriptEngine.eval("js", combinedScript)
        } catch (e: Exception) {
            console.printStackTrace(e)
        }

        cachedModules.add(module)
    }

    override fun loadExtra(module: Module) {
        if (cachedModules.any {
            it.name == module.name
        }) return

        cachedModules.add(module)

        val script = module.getFilesWithExtension(".js").joinToString(separator = "\n") {
            it.readText()
        }

        try {
            scriptEngine.eval("js", script)
        } catch (e: Exception) {
            console.out.println("Error loading module ${module.name}")
            console.printStackTrace(e)
        }
    }

    override fun eval(code: String): Any? {
        return scriptEngine.eval("js", code)
    }

    override fun getLanguageName(): List<String> {
        return listOf("js")
    }

    override fun trigger(trigger: OnTrigger, method: Any, vararg args: Any?) {
        try {
            if (method is String) {
                callNamedMethod(method, *args)
            } else {
                callActualMethod(method, *args)
            }
        } catch (e: Exception) {
            console.printStackTrace(e)
            removeTrigger(trigger)
        }
    }

    override fun getModules(): List<Module> {
        return cachedModules
    }

    private fun callActualMethod(method: Any, vararg args: Any?) {
        TODO()
    }

    private fun callNamedMethod(method: String, vararg args: Any?) {
        TODO()
    }
}