package com.chattriggers.ctjs.engine.langs.py

import com.chattriggers.ctjs.engine.ILoader
import com.chattriggers.ctjs.engine.ILoader.Companion.modulesFolder
import com.chattriggers.ctjs.engine.module.Module
import com.chattriggers.ctjs.minecraft.libs.FileLib
import com.chattriggers.ctjs.triggers.OnTrigger
import com.chattriggers.ctjs.utils.console.Console
import com.chattriggers.ctjs.utils.kotlin.ModuleLoader
import org.python.core.Py
import org.python.core.PyObject
import org.python.util.PythonInterpreter
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.util.*

@ModuleLoader
object PyLoader : ILoader {
    override val toRemove = mutableListOf<OnTrigger>()
    override var triggers = mutableListOf<OnTrigger>()
    override val console by lazy { Console(this) }

    private val cachedModules = mutableListOf<Module>()
    private lateinit var interpreter: PythonInterpreter
    private var pipInstalled = false

    override fun load(modules: List<Module>) {
        cachedModules.clear()

        val props = Properties(System.getProperties())
        PythonInterpreter.initialize(props, null, arrayOf())

        interpreter = PythonInterpreter()

        val jars = modules.map {
            it.folder.listFiles().toList()
        }.flatten().filter {
            it.name.endsWith(".jar")
        }.map {
            it.absolutePath
        }

        interpreter.exec("import sys as _sys")

        jars.forEach {
            interpreter.exec("_sys.path.append(\"$it\")")
        }

        val script = saveResource(
                "/provided_libs.py",
                File(modulesFolder.parentFile,
                        "chattriggers-provided-libs.py"
                ),
                true
        )

        try {
            interpreter.exec(script)
        } catch (e: Exception) {
            console.printStackTrace(e)
        }

        val combinedScript = modules.map {
            it.getFilesWithExtension(".py")
        }.flatten().joinToString(separator = "\n") {
            it.readText()
        }

        try {
            interpreter.exec(combinedScript)
        } catch (e: Exception) {
            console.printStackTrace(e)
        }

        cachedModules.addAll(modules)
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
            interpreter.exec(script)
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
            return interpreter.eval(code)
        } catch (e: Exception) {
            try {
                interpreter.exec(code)
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
                val callable = interpreter.get(method)
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
}
