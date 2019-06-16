package com.chattriggers.ctjs.triggers

import com.chattriggers.ctjs.engine.Lang
import com.chattriggers.ctjs.engine.Loader
import com.chattriggers.ctjs.utils.kotlin.External
import org.graalvm.polyglot.Value

@External
class OnSoundPlayTrigger(method: Value, lang: Lang) : OnTrigger(method, TriggerType.SOUND_PLAY, lang) {
    private var soundNameCriteria = ""

    /**
     * Sets the sound name criteria.<br></br>
     * Short hand for [OnSoundPlayTrigger.setCriteria].
     *
     * @param soundNameCriteria the sound name
     * @return the trigger for method chaining
     */
    fun setCriteria(soundNameCriteria: String) = apply { this.soundNameCriteria = soundNameCriteria }

    override fun trigger(vararg args: Any?) {
        if (args[2] is String
            && soundNameCriteria != ""
            && !(args[2] as String).equals(soundNameCriteria, ignoreCase = true))
            return

        callMethod(*args)
    }
}
