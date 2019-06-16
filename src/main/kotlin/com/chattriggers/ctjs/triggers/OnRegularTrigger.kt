package com.chattriggers.ctjs.triggers

import com.chattriggers.ctjs.engine.Lang
import com.chattriggers.ctjs.engine.Loader
import com.chattriggers.ctjs.utils.kotlin.External
import org.graalvm.polyglot.Value

@External
class OnRegularTrigger(method: Value, triggerType: TriggerType, lang: Lang) : OnTrigger(method, triggerType, lang) {
    override fun trigger(vararg args: Any?) {
        callMethod(*args)
    }
}
