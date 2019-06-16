package com.chattriggers.ctjs.engine.langs.py

import com.chattriggers.ctjs.engine.ILoader
import com.chattriggers.ctjs.engine.IRegister
import com.chattriggers.ctjs.minecraft.libs.XMLHttpRequest
import com.chattriggers.ctjs.minecraft.objects.display.Display
import com.chattriggers.ctjs.minecraft.objects.display.DisplayLine
import com.chattriggers.ctjs.minecraft.objects.gui.Gui

object PyRegister : IRegister {
    override fun getImplementationLoader(): ILoader = PyLoader
}

class PyGui : Gui() {
    override fun getLoader(): ILoader = PyLoader
}

class PyXMLHttpRequest : XMLHttpRequest() {
    override fun getLoader(): ILoader = PyLoader
}

class PyDisplayLine(text: String) : DisplayLine(text) {
    override fun getLoader(): ILoader = PyLoader
}

class PyDisplay : Display() {
    override fun createDisplayLine(text: String): DisplayLine {
        return PyDisplayLine(text)
    }
}
