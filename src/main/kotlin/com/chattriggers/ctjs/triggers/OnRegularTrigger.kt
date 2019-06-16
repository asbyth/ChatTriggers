package com.chattriggers.ctjs.triggers

import com.chattriggers.ctjs.engine.ILoader
import com.chattriggers.ctjs.utils.kotlin.External
import org.graalvm.polyglot.Value

@External
class OnRegularTrigger(method: Value, triggerType: TriggerType, loader: ILoader) : OnTrigger(method, triggerType, loader) {
    override fun trigger(vararg args: Any?) {
        callMethod(*args)
    }
}
