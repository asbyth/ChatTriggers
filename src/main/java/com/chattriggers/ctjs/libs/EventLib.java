package com.chattriggers.ctjs.libs;

import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;

public class EventLib {
    public static int getButton(MouseEvent event) {
        return event.getButton();
    }

    public static Boolean getButtonState(MouseEvent event) {
        return event.isButtonstate();
    }

    public static RenderGameOverlayEvent.ElementType getType(RenderGameOverlayEvent event) {
        return event.getType();
    }

    public static byte getType(ClientChatReceivedEvent event) {
        return event.getType().getId();
    }

    public static ITextComponent getMessage(ClientChatReceivedEvent event) {
        return event.getMessage();
    }

    public static String getName(PlaySoundEvent event) {
        return event.getName();
    }

    public static String getModId(ConfigChangedEvent.OnConfigChangedEvent event) {
        return event.getModID();
    }
}
