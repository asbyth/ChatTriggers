package com.chattriggers.ctjs.listeners;

import com.chattriggers.ctjs.CTJS;
import com.chattriggers.ctjs.libs.ChatLib;
import com.chattriggers.ctjs.triggers.TriggerType;
import lombok.Getter;
import com.chattriggers.ctjs.utils.console.Console;
import net.minecraft.util.text.ChatType;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Getter
public class ChatListener {
    @SubscribeEvent
    public void onReceiveChat(ClientChatReceivedEvent event) {
        if (event.getType().equals(ChatType.CHAT) || event.getType().equals(ChatType.SYSTEM)) {
            //Normal Chat Message
            TriggerType.CHAT.triggerAll(event.getMessage().getUnformattedText(), event);

            if (CTJS.getInstance().getConfig().getPrintChatToConsole()) {
                Console.getConsole().out.println("[CHAT] " + ChatLib.replaceFormatting(event.getMessage().getFormattedText()));
            }
        }
    }
}