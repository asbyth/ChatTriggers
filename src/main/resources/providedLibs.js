/*global
   Java
*/

// Extra libs
const ArrayList = Java.type('java.util.ArrayList');
const HashMap = Java.type('java.util.HashMap');
const Thread = Java.type('java.lang.Thread');
const Keyboard = Java.type('org.lwjgl.input.Keyboard');
const ReflectionHelper = Java.type('net.minecraftforge.fml.relauncher.ReflectionHelper');

// Triggers
const TriggerRegister = Java.type('com.chattriggers.ctjs.engine.JsRegister').INSTANCE;
const TriggerResult = Java.type('com.chattriggers.ctjs.triggers.OnTrigger.TriggerResult');
const Priority = Java.type('com.chattriggers.ctjs.triggers.OnTrigger.Priority');

// Trigger types
const OnChatTrigger = Java.type('com.chattriggers.ctjs.triggers.OnChatTrigger');
const OnCommandTrigger = Java.type('com.chattriggers.ctjs.triggers.OnCommandTrigger');
const OnRegularTrigger = Java.type('com.chattriggers.ctjs.triggers.OnRegularTrigger');
const OnRenderTrigger = Java.type('com.chattriggers.ctjs.triggers.OnRenderTrigger');
const OnSoundPlayTrigger = Java.type('com.chattriggers.ctjs.triggers.OnSoundPlayTrigger');
const OnStepTrigger = Java.type('com.chattriggers.ctjs.triggers.OnStepTrigger');
const OnTrigger = Java.type('com.chattriggers.ctjs.triggers.OnTrigger');
const InteractAction = Java.type("net.minecraftforge.event.entity.player.PlayerInteractEvent.Action");

// Libraries
const ChatLib = Java.type('com.chattriggers.ctjs.minecraft.libs.ChatLib');
const EventLib = Java.type('com.chattriggers.ctjs.minecraft.libs.EventLib');
const FileLib = Java.type('com.chattriggers.ctjs.minecraft.libs.FileLib');
const MathLib = Java.type('com.chattriggers.ctjs.minecraft.libs.MathLib');
const Tessellator = Java.type('com.chattriggers.ctjs.minecraft.libs.Tessellator');

// Renderer
const Renderer = Java.type('com.chattriggers.ctjs.minecraft.libs.renderer.Renderer');
const Shape = Java.type('com.chattriggers.ctjs.minecraft.libs.renderer.Shape');
const Rectangle = Java.type('com.chattriggers.ctjs.minecraft.libs.renderer.Rectangle');
const Text = Java.type('com.chattriggers.ctjs.minecraft.libs.renderer.Text');
const Image = Java.type('com.chattriggers.ctjs.minecraft.libs.renderer.Image');

// Objects
const XMLHttpRequest = Java.type('com.chattriggers.ctjs.engine.JsXMLHttpRequest');
const DisplayHandler = Java.type('com.chattriggers.ctjs.minecraft.objects.display.DisplayHandler');
const Display = Java.type('com.chattriggers.ctjs.engine.JsDisplay');
const DisplayLine = Java.type('com.chattriggers.ctjs.engine.JsDisplayLine');
const Gui = Java.type('com.chattriggers.ctjs.engine.JsGui');
const Message = Java.type('com.chattriggers.ctjs.minecraft.objects.message.Message');
const TextComponent = Java.type('com.chattriggers.ctjs.minecraft.objects.message.TextComponent');
const Book = Java.type('com.chattriggers.ctjs.minecraft.objects.Book');
const KeyBind = Java.type('com.chattriggers.ctjs.minecraft.objects.KeyBind');
const Sound = Java.type('com.chattriggers.ctjs.minecraft.objects.Sound');

// Wrappers
const Client = Java.type('com.chattriggers.ctjs.minecraft.wrappers.Client');
const Player = Java.type('com.chattriggers.ctjs.minecraft.wrappers.Player');
const World = Java.type('com.chattriggers.ctjs.minecraft.wrappers.World');
const Server = Java.type('com.chattriggers.ctjs.minecraft.wrappers.Server');
const Inventory = Java.type('com.chattriggers.ctjs.minecraft.wrappers.objects.inventory.Inventory');
const TabList = Java.type('com.chattriggers.ctjs.minecraft.wrappers.TabList');
const Scoreboard = Java.type('com.chattriggers.ctjs.minecraft.wrappers.Scoreboard');
const CPS = Java.type('com.chattriggers.ctjs.minecraft.wrappers.CPS');
const Item = Java.type('com.chattriggers.ctjs.minecraft.wrappers.objects.inventory.Item');
const Block = Java.type('com.chattriggers.ctjs.minecraft.wrappers.objects.block.Block');
const Sign = Java.type('com.chattriggers.ctjs.minecraft.wrappers.objects.block.Sign');
const Entity = Java.type('com.chattriggers.ctjs.minecraft.wrappers.objects.Entity');
const Action = Java.type('com.chattriggers.ctjs.minecraft.wrappers.objects.inventory.action.Action');
const ClickAction = Java.type('com.chattriggers.ctjs.minecraft.wrappers.objects.inventory.action.ClickAction');
const DragAction = Java.type('com.chattriggers.ctjs.minecraft.wrappers.objects.inventory.action.DragAction');
const KeyAction = Java.type('com.chattriggers.ctjs.minecraft.wrappers.objects.inventory.action.KeyAction');

// Misc
const Config = Java.type('com.chattriggers.ctjs.utils.config.Config').INSTANCE;
const ChatTriggers = Java.type('com.chattriggers.ctjs.Reference').INSTANCE;


// Helper methods
const cancel = (event) => {
    try {
        EventLib.cancel(event);
    } catch(err) {
        if (!event.isCancelable()) return;
        event.setCanceled(true);
    }
};

const register = (triggerType, methodName) =>
    TriggerRegister.register(triggerType, methodName);

// animation
const easeOut = (start, finish, speed, jump) => {
    if (!jump) jump = 1;

    if (Math.floor(Math.abs(finish - start) / jump) > 0) {
        return start + (finish - start) / speed;
    } else {
        return finish;
    }
};

Number.prototype.easeOut = (to, speed, jump) => {
    if (!jump) jump = 1;

    if (Math.floor(Math.abs(to - this) / jump) > 0) {
        this = this + (to - this) / speed;
    } else {
        this = to
    }
};

const setTimeout = (func, delay) => {
    new Thread(() => {
        Thread.sleep(delay);
        func();
    }).start();
};
