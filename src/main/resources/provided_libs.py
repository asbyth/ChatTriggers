import java

# Function to help with language-dependent classes
Lang = java.type("com.chattriggers.ctjs.engine.Lang").PY

def extend_with_lang(clazz):
    def ret_func(*args):
        inst = clazz(*args)
        inst.lang = Lang
        return inst
    return ret_func

# Extra Libs
ArrayList = java.type('java.util.ArrayList')
HashMap = java.type('java.util.HashMap')
Thread = java.type('java.lang.Thread')
Keyboard = java.type('org.lwjgl.input.Keyboard')
ReflectionHelper = java.type('net.minecraftforge.fml.relauncher.ReflectionHelper')

# Trigger types
OnChatTrigger = java.type('com.chattriggers.ctjs.triggers.OnChatTrigger')
OnCommandTrigger = java.type('com.chattriggers.ctjs.triggers.OnCommandTrigger')
OnRegularTrigger = java.type('com.chattriggers.ctjs.triggers.OnRegularTrigger')
OnRenderTrigger = java.type('com.chattriggers.ctjs.triggers.OnRenderTrigger')
OnSoundPlayTrigger = java.type('com.chattriggers.ctjs.triggers.OnSoundPlayTrigger')
OnStepTrigger = java.type('com.chattriggers.ctjs.triggers.OnStepTrigger')
OnTrigger = java.type('com.chattriggers.ctjs.triggers.OnTrigger')

# Triggers
TriggerRegister = extend_with_lang(java.type('com.chattriggers.ctjs.engine.Register'))
TriggerResult = OnTrigger.TriggerResult
Priority = OnTrigger.Priority

# Libraries
ChatLib = java.type('com.chattriggers.ctjs.minecraft.libs.ChatLib')
EventLib = java.type('com.chattriggers.ctjs.minecraft.libs.EventLib')
Tessellator = java.type('com.chattriggers.ctjs.minecraft.libs.Tessellator')
FileLib = java.type('com.chattriggers.ctjs.minecraft.libs.FileLib')
MathLib = java.type('com.chattriggers.ctjs.minecraft.libs.MathLib')

# Renderer
Renderer = java.type('com.chattriggers.ctjs.minecraft.libs.renderer.Renderer')
Shape = java.type('com.chattriggers.ctjs.minecraft.libs.renderer.Shape')
Rectangle = java.type('com.chattriggers.ctjs.minecraft.libs.renderer.Rectangle')
Text = java.type('com.chattriggers.ctjs.minecraft.libs.renderer.Text')
Image = java.type('com.chattriggers.ctjs.minecraft.libs.renderer.Image')

# Object
XMLHttpRequest = extend_with_lang(java.type('com.chattriggers.ctjs.minecraft.libs.XMLHttpRequest'))
DisplayHandler = java.type('com.chattriggers.ctjs.minecraft.objects.display.DisplayHandler')
Display = extend_with_lang(java.type('com.chattriggers.ctjs.minecraft.objects.display.Display'))
DisplayLine = extend_with_lang(java.type('com.chattriggers.ctjs.minecraft.objects.display.DisplayLine'))
Gui = extend_with_lang(java.type('com.chattriggers.ctjs.minecraft.objects.gui.Gui'))
Message = java.type('com.chattriggers.ctjs.minecraft.objects.message.Message')
TextComponent = java.type('com.chattriggers.ctjs.minecraft.objects.message.TextComponent')
Book = java.type('com.chattriggers.ctjs.minecraft.objects.Book')
KeyBind = java.type('com.chattriggers.ctjs.minecraft.objects.KeyBind')
Sound = java.type('com.chattriggers.ctjs.minecraft.objects.Sound')

# Wrappers
Client = java.type('com.chattriggers.ctjs.minecraft.wrappers.Client')
Settings = java.type('com.chattriggers.ctjs.minecraft.wrappers.Settings')
Player = java.type('com.chattriggers.ctjs.minecraft.wrappers.Player')
World = java.type('com.chattriggers.ctjs.minecraft.wrappers.World')
Server = java.type('com.chattriggers.ctjs.minecraft.wrappers.Server')
TabList = java.type('com.chattriggers.ctjs.minecraft.wrappers.TabList')
Scoreboard = java.type('com.chattriggers.ctjs.minecraft.wrappers.Scoreboard')
CPS = java.type('com.chattriggers.ctjs.minecraft.wrappers.CPS')
Entity = java.type('com.chattriggers.ctjs.minecraft.wrappers.objects.Entity')
Block = java.type('com.chattriggers.ctjs.minecraft.wrappers.objects.block.Block')
Sign = java.type('com.chattriggers.ctjs.minecraft.wrappers.objects.block.Sign')
Inventory = java.type('com.chattriggers.ctjs.minecraft.wrappers.objects.inventory.Inventory')
Item = java.type('com.chattriggers.ctjs.minecraft.wrappers.objects.inventory.Item')
ClickAction = java.type('com.chattriggers.ctjs.minecraft.wrappers.objects.inventory.action.ClickAction')
DragAction = java.type('com.chattriggers.ctjs.minecraft.wrappers.objects.inventory.action.DragAction')
KeyAction = java.type('com.chattriggers.ctjs.minecraft.wrappers.objects.inventory.action.KeyAction')

# Misc
Console = java.type('com.chattriggers.ctjs.engine.PrimaryLoader').INSTANCE.getConsole()
Config = java.type('com.chattriggers.ctjs.utils.config.Config').INSTANCE
ChatTriggers = java.type('com.chattriggers.ctjs.Reference').INSTANCE

# Helper methods
import math


def cancel(event):
    try:
        EventLib.cancel(event)
    except:
        if not event.isCancelable():
            return
        event.setCanceled(True)


def register(trigger_type, method_name):
    return TriggerRegister.register(trigger_type, method_name)


def easeOut(start, finish, speed, jump):
    if not jump:
        jump = 1

    if math.floor(math.abs(finish - start) / jump) > 0:
        return start + (finish - start) / speed
    else:
        return finish


def setTimeout(func, delay):
    def tFunc():
        Thread.sleep(delay)
        func()

    Thread(tFunc).start()


def log(*args):
    for arg in args:
        Console.out.printf('%s', str(arg))
    Console.out.printf('\n')