package com.chattriggers.ctjs.triggers

import com.chattriggers.ctjs.engine.Lang
import com.chattriggers.ctjs.engine.Loader
import com.chattriggers.ctjs.engine.ModuleManager
import com.chattriggers.ctjs.utils.kotlin.External
import jdk.nashorn.internal.objects.Global
import org.graalvm.polyglot.Value

@External
abstract class OnTrigger protected constructor(var method: Value, var type: TriggerType, protected var lang: Lang) {
    var priority: Priority
        private set
    private var global: Global?

    init {
        priority = Priority.NORMAL
        global = null

        register()
    }

    /**
     * Sets a triggers priority using [Priority].
     * Highest runs first.
     * @param priority the priority of the trigger
     * @return the trigger for method chaining
     */
    fun setPriority(priority: Priority): OnTrigger {
        this.priority = priority
        return this
    }

    /**
     * Registers a trigger based on its type.
     * This is done automatically with TriggerRegister.
     * @return the trigger for method chaining
     */
    fun register() = apply {
        ModuleManager.getLoader(lang).addTrigger(this)
    }

    /**
     * Unregisters a trigger.
     * @return the trigger for method chaining
     */
    fun unregister() = apply {
        ModuleManager.getLoader(lang).removeTrigger(this)
    }

    protected fun callMethod(vararg args: Any?) {
        ModuleManager.getLoader(lang).trigger(this, this.method, *args)
    }

    abstract fun trigger(vararg args: Any?)

    enum class TriggerResult {
        CANCEL
    }

    enum class Priority {
        //LOWEST IS RAN LAST
        HIGHEST, HIGH, NORMAL, LOW, LOWEST
    }
}