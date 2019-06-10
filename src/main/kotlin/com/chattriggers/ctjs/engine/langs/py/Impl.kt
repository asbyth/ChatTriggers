package com.chattriggers.ctjs.engine.langs.py

import com.chattriggers.ctjs.engine.ILoader
import com.chattriggers.ctjs.engine.IRegister
import com.chattriggers.ctjs.minecraft.libs.XMLHttpRequest
import com.chattriggers.ctjs.minecraft.objects.display.Display
import com.chattriggers.ctjs.minecraft.objects.display.DisplayHandler
import com.chattriggers.ctjs.minecraft.objects.display.DisplayLine
import com.chattriggers.ctjs.minecraft.objects.gui.Gui
import org.python.core.PyDictionary
import org.python.core.PyObject

object PyRegister : IRegister {
    override fun getImplementationLoader(): ILoader = PyLoader
}

class PyGui : Gui() {
    override fun getLoader(): ILoader = PyLoader
}

class PyXMLHttpRequest : XMLHttpRequest() {
    override fun getLoader(): ILoader = PyLoader
}

class PyDisplayLine : DisplayLine {
    constructor(text: String) : super(text)

    constructor(text: String, config: PyObject) : super(text) {
        this.textColor = config.getOption("text_color", null)?.toInt()
        this.backgroundColor = config.getOption("background_color", null)?.toInt()

        this.setAlign(config.getOption("align", null))
        this.setBackground(config.getOption("background", null))
    }

    private fun PyObject?.getOption(key: String, default: Any?): String? {
        if (this == null || this !is PyDictionary) return default?.toString()
        return this[key].toString()
    }

    override fun getLoader(): ILoader = PyLoader
}

class PyDisplay : Display {
    constructor() : super()

    constructor(config: PyObject?) : super() {
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

    private fun PyObject?.getOption(key: String, default: Any?): String {
        if (this == null || this !is PyDictionary) return default.toString()
        return this[key].toString()
    }

    override fun createDisplayLine(text: String): DisplayLine {
        return PyDisplayLine(text)
    }
}
