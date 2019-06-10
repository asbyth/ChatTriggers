package com.chattriggers.ctjs.engine.langs.js

import com.chattriggers.ctjs.engine.ILoader
import com.chattriggers.ctjs.engine.IRegister
import com.chattriggers.ctjs.minecraft.libs.XMLHttpRequest
import com.chattriggers.ctjs.minecraft.objects.display.Display
import com.chattriggers.ctjs.minecraft.objects.display.DisplayHandler
import com.chattriggers.ctjs.minecraft.objects.display.DisplayLine
import com.chattriggers.ctjs.minecraft.objects.gui.Gui
import jdk.nashorn.api.scripting.ScriptObjectMirror

/*
This file holds the "glue" for this language.

Certain classes have triggers inside of them that need to know what loader to use,
and that's where these implementations come in.
 */

object JSRegister : IRegister {
    override fun getImplementationLoader(): ILoader = JSLoader
}

class JSGui : Gui() {
    override fun getLoader(): ILoader = JSLoader
}

class JSXMLHttpRequest : XMLHttpRequest() {
    override fun getLoader(): ILoader = JSLoader
}

class JSDisplayLine : DisplayLine {
    constructor(text: String) : super(text)

    constructor(text: String, config: ScriptObjectMirror) : super(text) {
        this.textColor = config.getOption("textColor", null)?.toInt()
        this.backgroundColor = config.getOption("backgroundColor", null)?.toInt()

        this.setAlign(config.getOption("align", null))
        this.setBackground(config.getOption("background", null))
    }

    private fun ScriptObjectMirror?.getOption(key: String, default: Any?): String? {
        if (this == null) return default?.toString()
        return this.getOrDefault(key, default).toString()
    }

    override fun getLoader(): ILoader = JSLoader
}

class JSDisplay : Display {
    constructor() : super()

    constructor(config: ScriptObjectMirror?) : super() {
        this.shouldRender = config.getOption("shouldRender", true).toBoolean()
        this.renderX = config.getOption("renderX", 0).toFloat()
        this.renderY = config.getOption("renderY", 0).toFloat()

        this.backgroundColor = config.getOption("backgroundColor", 0x50000000).toInt()
        this.textColor = config.getOption("textColor", 0xffffffff.toInt()).toInt()

        this.setBackground(config.getOption("background", DisplayHandler.Background.NONE))
        this.setAlign(config.getOption("align", DisplayHandler.Align.LEFT))
        this.setOrder(config.getOption("order", DisplayHandler.Order.DOWN))

        this.minWidth = config.getOption("minWidth", 0f).toFloat()

        DisplayHandler.registerDisplay(this)
    }

    private fun ScriptObjectMirror?.getOption(key: String, default: Any): String {
        if (this == null) return default.toString()
        return this.getOrDefault(key, default).toString()
    }

    override fun createDisplayLine(text: String): DisplayLine {
        return JSDisplayLine(text)
    }
}