package com.chattriggers.ctjs.engine

import com.chattriggers.ctjs.engine.langs.js.JSLoader
import com.chattriggers.ctjs.engine.langs.py.PyLoader

object BridgeManager {
    private var bridges = mapOf(
            "js" to JSLoader.JSBridge,
            "py" to PyLoader.PyBridge
    )

    fun lang(lang: String): IBridge {
        return bridges[lang] ?: throw IllegalArgumentException(
                "Unknown language: $lang\n" +
                        "Valid languages: ${bridges.keys.joinToString()}"
        )
    }
}
