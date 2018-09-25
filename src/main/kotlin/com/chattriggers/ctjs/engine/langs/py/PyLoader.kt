package com.chattriggers.ctjs.engine.langs.py

import com.chattriggers.ctjs.engine.ILoader
import com.chattriggers.ctjs.engine.module.Module
import com.chattriggers.ctjs.triggers.OnTrigger
import com.chattriggers.ctjs.triggers.TriggerType
import com.chattriggers.ctjs.utils.console.Console
import com.chattriggers.ctjs.utils.kotlin.ModuleLoader
import org.python.core.Py
import org.python.core.PyString
import org.python.core.PySystemState
import org.python.util.PythonInterpreter
import java.io.File
import java.net.URLClassLoader

@ModuleLoader
object PyLoader : ILoader {
    override val toRemove = mutableListOf<OnTrigger>()
    override var triggers = mutableListOf<OnTrigger>()

    private val cachedModules = mutableListOf<Module>()
    private val interpreters = mutableListOf<PythonInterpreter>()
    private lateinit var genericInterpreter: PythonInterpreter

    override fun load(modules: List<Module>) {
        interpreters.clear()
        cachedModules.clear()

        val sys = PySystemState()
        sys.path.append(
                PyString(ILoader.modulesFolder.absolutePath)
        )

        val jars = modules.map {
            it.folder.listFiles().toList()
        }.flatten().filter {
            it.name.endsWith(".jar")
        }.map {
            it.toURI().toURL()
        }

        val classLoader = URLClassLoader(jars.toTypedArray())

        jars.forEach {
            PySystemState.add_classdir()
        }

        PySystemState.add_package()
        //TODO: JAR LOADING!

        modules.forEach {
            val mainFileName = if (it.metadata.mainFile.endsWith(".py")) it.metadata.mainFile
                                else "${it.metadata.mainFile}.py"

            val mainFile = File(mainFileName)

            if (!mainFile.exists()) return@forEach

            val interp = PythonInterpreter(Py.newStringMap(), sys)
            interp.execfile(mainFile.absolutePath)
            interpreters.add(interp)
        }

        genericInterpreter = PythonInterpreter(Py.newStringMap(), sys)

        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun loadExtra(module: Module) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun exec(type: TriggerType, vararg args: Any?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun eval(code: String): Any? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addTrigger(trigger: OnTrigger) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun clearTriggers() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getLanguageName(): List<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun trigger(trigger: OnTrigger, method: Any, vararg args: Any?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun removeTrigger(trigger: OnTrigger) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getModules(): List<Module> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getConsole(): Console {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}