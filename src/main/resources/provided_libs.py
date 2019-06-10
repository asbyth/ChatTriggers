# Extra Libs
from java.util import ArrayList, HashMap
from java.lang import Thread
from org.lwjgl.input import Keyboard
from net.minecraftforge.fml.relauncher import ReflectionHelper

# Trigger types
from com.chattriggers.ctjs.triggers \
    import OnChatTrigger, OnCommandTrigger, OnRegularTrigger, OnRenderTrigger, \
    OnSoundPlayTrigger, OnStepTrigger, OnTrigger

# Triggers
from com.chattriggers.ctjs.engine.langs.py.PyRegister import INSTANCE as TriggerRegister
TriggerResult = OnTrigger.TriggerResult
Priority = OnTrigger.Priority

# Libraries
from com.chattriggers.ctjs.minecraft.libs \
    import ChatLib, EventLib, Tessellator, FileLib, MathLib

# Renderer
from com.chattriggers.ctjs.minecraft.libs.renderer \
    import Renderer, Shape, Rectangle, Text, Image

# Object
from com.chattriggers.ctjs.engine.langs.py \
    import PyXMLHttpRequest, PyDisplay, PyDisplayLine, PyGui
from com.chattriggers.ctjs.minecraft.objects.display \
    import DisplayHandler
from com.chattriggers.ctjs.minecraft.objects.message \
    import Message, TextComponent
from com.chattriggers.ctjs.minecraft.objects \
    import Book, KeyBind, Sound

# Wrappers
from com.chattriggers.ctjs.minecraft.wrappers \
    import Client, Settings, Player, World, Server, TabList, Scoreboard, CPS
from com.chattriggers.ctjs.minecraft.wrappers.objects \
    import Entity
from com.chattriggers.ctjs.minecraft.wrappers.objects.block \
    import Block, Sign
from com.chattriggers.ctjs.minecraft.wrappers.objects.inventory \
    import Inventory, Item
from com.chattriggers.ctjs.minecraft.wrappers.objects.inventory.action \
    import ClickAction, DragAction, KeyAction

# Misc
from com.chattriggers.ctjs.engine.langs.py.PyLoader import INSTANCE as __Console
Console = __Console.getConsole()
from com.chattriggers.ctjs.utils.config.Config import INSTANCE as Config
from com.chattriggers.ctjs.Reference import INSTANCE as ChatTriggers


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