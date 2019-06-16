# Extra Libs
Java.import 'java.util.ArrayList'
Java.import 'java.util.HashMap'
Java.import 'java.lang.Thread'
Java.import 'org.lwjgl.input.Keyboard'
Java.import 'net.minecraftforge.fml.relauncher.ReflectionHelper'

# Trigger types
Java.import 'com.chattriggers.ctjs.triggers.OnChatTrigger'
Java.import 'com.chattriggers.ctjs.triggers.OnCommandTrigger'
Java.import 'com.chattriggers.ctjs.triggers.OnRegularTrigger'
Java.import 'com.chattriggers.ctjs.triggers.OnRenderTrigger'
Java.import 'com.chattriggers.ctjs.triggers.OnSoundPlayTrigger'
Java.import 'com.chattriggers.ctjs.triggers.OnStepTrigger'
Java.import 'com.chattriggers.ctjs.triggers.OnTrigger'

# Triggers
TriggerRegister = Java.type('com.chattriggers.ctjs.engine.langs.rb.RbRegister').INSTANCE
TriggerResult = OnTrigger.TriggerResult
Priority = OnTrigger.Priority

# Libraries
Java.import 'com.chattriggers.ctjs.minecraft.libs.ChatLib'
Java.import 'com.chattriggers.ctjs.minecraft.libs.EventLib'
Java.import 'com.chattriggers.ctjs.minecraft.libs.Tessellator'
Java.import 'com.chattriggers.ctjs.minecraft.libs.FileLib'
Java.import 'com.chattriggers.ctjs.minecraft.libs.MathLib'

# Renderer
Java.import 'com.chattriggers.ctjs.minecraft.libs.renderer.Renderer'
Java.import 'com.chattriggers.ctjs.minecraft.libs.renderer.Shape'
Java.import 'com.chattriggers.ctjs.minecraft.libs.renderer.Rectangle'
Java.import 'com.chattriggers.ctjs.minecraft.libs.renderer.Text'
Java.import 'com.chattriggers.ctjs.minecraft.libs.renderer.Image'

# Objects
XMLHttpRequest = Java.type('com.chattriggers.ctjs.engine.langs.rb.RbXMLHttpRequest')
Display = Java.type('com.chattriggers.ctjs.engine.langs.rb.RbDisplay')
DisplayLine = Java.type('com.chattriggers.ctjs.engine.langs.rb.RbDisplayLine')
Gui = Java.type('com.chattriggers.ctjs.engine.langs.rb.RbGui')
Java.import 'com.chattriggers.ctjs.minecraft.objects.display.DisplayHandler'
Java.import 'com.chattriggers.ctjs.minecraft.objects.message.Message'
Java.import 'com.chattriggers.ctjs.minecraft.objects.message.TextComponent'
Java.import 'com.chattriggers.ctjs.minecraft.objects.Book'
Java.import 'com.chattriggers.ctjs.minecraft.objects.KeyBind'
Java.import 'com.chattriggers.ctjs.minecraft.objects.Sound'

# Wrappers
Java.import 'com.chattriggers.ctjs.minecraft.wrappers.Client'
Java.import 'com.chattriggers.ctjs.minecraft.wrappers.Settings'
Java.import 'com.chattriggers.ctjs.minecraft.wrappers.Player'
Java.import 'com.chattriggers.ctjs.minecraft.wrappers.World'
Java.import 'com.chattriggers.ctjs.minecraft.wrappers.Server'
Java.import 'com.chattriggers.ctjs.minecraft.wrappers.TabList'
Java.import 'com.chattriggers.ctjs.minecraft.wrappers.Scoreboard'
Java.import 'com.chattriggers.ctjs.minecraft.wrappers.CPS'
Java.import 'com.chattriggers.ctjs.minecraft.wrappers.objects.Entity'
Java.import 'com.chattriggers.ctjs.minecraft.wrappers.objects.block.Block'
Java.import 'com.chattriggers.ctjs.minecraft.wrappers.objects.block.Sign'
Java.import 'com.chattriggers.ctjs.minecraft.wrappers.objects.inventory.Inventory'
Java.import 'com.chattriggers.ctjs.minecraft.wrappers.objects.inventory.Item'
Java.import 'com.chattriggers.ctjs.minecraft.wrappers.objects.inventory.action.ClickAction'
Java.import 'com.chattriggers.ctjs.minecraft.wrappers.objects.inventory.action.DragAction'
Java.import 'com.chattriggers.ctjs.minecraft.wrappers.objects.inventory.action.KeyAction'

# Misc
Console = Java.type('com.chattriggers.ctjs.engine.langs.rb.RbLoader').INSTANCE.getConsole()
Config = Java.type('com.chattriggers.ctjs.utils.config.Config').INSTANCE
ChatTriggers = Java.type('com.chattriggers.ctjs.Reference').INSTANCE


# Helper methods
require 'math'

def cancel(event)
  begin
    EventLib.cancel(event)

  rescue Exception
    unless event.isCancelable()
      return
    end

    event.setCanceled(true)
  end
end

def register(trigger_type, method_name)
  TriggerRegister.register(trigger_type, method_name)
end

def ease_out(start, finish, speed, jump)
  unless jump
    jump = 1
  end

  if Math.floor(Math.abs(finish - start) / jump) > 0
    start + (finish - start) / speed.to_f
  else
    finish
  end
end

set_timeout(500) do
  # do something
end

def set_timeout(delay)
  func = -> do
    Thread.sleep(delay)
    yield
  end

  Thread(func).start()
end
