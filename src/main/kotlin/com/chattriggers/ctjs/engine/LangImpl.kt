package com.chattriggers.ctjs.engine

import com.chattriggers.ctjs.minecraft.libs.XMLHttpRequest
import com.chattriggers.ctjs.minecraft.objects.display.Display
import com.chattriggers.ctjs.minecraft.objects.display.DisplayLine
import com.chattriggers.ctjs.minecraft.objects.gui.Gui
import org.graalvm.polyglot.Value

// Language dependent class implementations

// Register
object JsRegister : Register(Lang.JS)
object PyRegister : Register(Lang.PY)
object RbRegister : Register(Lang.RB)
object RRegister  : Register(Lang.R)

// XMLHttpRequest
class JsXMLHttpRequest : XMLHttpRequest(Lang.JS)
class PyXMLHttpRequest : XMLHttpRequest(Lang.PY)
class RbXMLHttpRequest : XMLHttpRequest(Lang.RB)
class RXMLHttpRequest  : XMLHttpRequest(Lang.R)

// Display
class JsDisplay : Display {
    constructor() : super(Lang.JS)
    constructor(config: Value) : super(Lang.JS, config)
}

class PyDisplay : Display {
    constructor() : super(Lang.PY)
    constructor(config: Value) : super(Lang.PY, config)
}

class RbDisplay : Display {
    constructor() : super(Lang.RB)
    constructor(config: Value) : super(Lang.RB, config)
}

class RDisplay : Display {
    constructor() : super(Lang.R)
    constructor(config: Value) : super(Lang.R, config)
}

// DisplayLine
class JsDisplayLine : DisplayLine {
    constructor(text: String) : super(Lang.JS, text)
    constructor(text: String, config: Value) : super(Lang.JS, text, config)
}

class PyDisplayLine : DisplayLine {
    constructor(text: String) : super(Lang.PY, text)
    constructor(text: String, config: Value) : super(Lang.PY, text, config)
}

class RbDisplayLine : DisplayLine {
    constructor(text: String) : super(Lang.RB, text)
    constructor(text: String, config: Value) : super(Lang.RB, text, config)
}

class RDisplayLine : DisplayLine {
    constructor(text: String) : super(Lang.R, text)
    constructor(text: String, config: Value) : super(Lang.R, text, config)
}

// Gui
class JsGui : Gui(Lang.JS)
class PyGui : Gui(Lang.PY)
class RbGui : Gui(Lang.RB)
class RGui  : Gui(Lang.R)