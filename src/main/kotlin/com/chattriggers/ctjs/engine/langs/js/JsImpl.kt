package com.chattriggers.ctjs.engine.langs.js

import com.chattriggers.ctjs.engine.ILoader
import com.chattriggers.ctjs.engine.IRegister
import com.chattriggers.ctjs.minecraft.libs.XMLHttpRequest
import com.chattriggers.ctjs.minecraft.objects.display.Display
import com.chattriggers.ctjs.minecraft.objects.display.DisplayLine
import com.chattriggers.ctjs.minecraft.objects.gui.Gui

/*
This file holds the "glue" for this language.

Certain classes have triggers inside of them that need to know what loader to use,
and that's where these implementations come in.
 */

object JSRegister : IRegister {
    override fun getImplementationLoader(): ILoader = JsLoader
}

class JSGui : Gui() {
    override fun getLoader(): ILoader = JsLoader
}

class JSXMLHttpRequest : XMLHttpRequest() {
    override fun getLoader(): ILoader = JsLoader
}

class JSDisplayLine(text: String) : DisplayLine(text) {
    override fun getLoader(): ILoader = JsLoader
}

class JSDisplay : Display() {
    override fun createDisplayLine(text: String): DisplayLine {
        return JSDisplayLine(text)
    }
}