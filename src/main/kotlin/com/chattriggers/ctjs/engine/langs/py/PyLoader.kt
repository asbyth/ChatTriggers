package com.chattriggers.ctjs.engine.langs.py

import com.chattriggers.ctjs.engine.IBridge
import com.chattriggers.ctjs.engine.ILoader
import com.chattriggers.ctjs.engine.ILoader.Companion.modulesFolder
import com.chattriggers.ctjs.engine.module.Module
import com.chattriggers.ctjs.triggers.OnTrigger
import com.chattriggers.ctjs.utils.console.Console
import com.chattriggers.ctjs.utils.kotlin.ModuleLoader
import org.python.core.Py
import org.python.core.PyObject
import org.python.util.PythonInterpreter
import java.io.File

@ModuleLoader
object PyLoader : ILoader {
    override val toRemove = mutableListOf<OnTrigger>()
    override var triggers = mutableListOf<OnTrigger>()
    override val console by lazy { Console(this) }

    private val cachedModules = mutableListOf<Module>()
    private lateinit var scriptEngine: PythonInterpreter

    override fun preload(modules: List<Module>) {
        cachedModules.clear()

        scriptEngine = PythonInterpreter()
        scriptEngine.exec("import sys as _sys")

        val script = saveResource(
                "/provided_libs.py",
                File(modulesFolder.parentFile,
                        "chattriggers-provided-libs.py"
                ),
                true
        )

        try {
            scriptEngine.exec(script)
        } catch (e: Exception) {
            console.printStackTrace(e)
        }
    }

    override fun load(module: Module) {
        module.getFilesWithExtension(".jar").map {
            it.absolutePath
        }.forEach {
            scriptEngine.exec("_sys.path.append(\"$it\")")
        }

        val combinedScript = module.getFilesWithExtension(".py").joinToString(separator = "\n") {
            it.readText()
        }

        try {
            scriptEngine.exec(combinedScript)
        } catch (e: Exception) {
            console.out.println("Error loading module ${module.name}")
            console.printStackTrace(e)
        }

        cachedModules.add(module)
    }

    override fun loadExtra(module: Module) {
        if (cachedModules.any {
            it.name == module.name
        }) return

        cachedModules.add(module)

        val script = module.getFilesWithExtension(".py").joinToString(separator = "\n") {
            it.readText()
        }

        try {
            scriptEngine.exec(script)
        } catch (e: Exception) {
            console.out.println("Error loading module ${module.name}")
            console.printStackTrace(e)
        }
    }

    override fun eval(code: String): Any? {
        // TODO: Make this better?
        // Jython eval is different than Nashorn eval. It can
        // only handle expressions (like "1 + 2"), so no assignment
        // and no importing. exec can handle everything, but returns
        // nothing, so if exec is needed, the input is reprinted
        // to console. Ex: "a = 5" in the python console prints
        // "a = 5", whereas in the js console, "var a = 5" would
        // print "5"
        return try {
            return scriptEngine.eval(code)
        } catch (e: Exception) {
            try {
                scriptEngine.exec(code)
                code
            } catch (ee: Exception) {
                console.printStackTrace(ee)
                null
            }
        }
    }

    override fun getLanguageName(): List<String> {
        return listOf("py")
    }

    override fun trigger(trigger: OnTrigger, method: Any, vararg args: Any?) {
        try {
            if (method is String) {
                val callable = scriptEngine.get(method)
                callable.__call__(Py.javas2pys(*args))
            } else if (method is PyObject) {
                method.__call__(Py.javas2pys(*args))
            }
        } catch (e: Exception) {
            console.printStackTrace(e)
            removeTrigger(trigger)
        }
    }

    override fun getModules(): List<Module> {
        return cachedModules
    }

    object PyBridge : IBridge {
        override fun get(name: String): Any? {
            return scriptEngine.get(name)
        }
    }
}
