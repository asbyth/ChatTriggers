package com.chattriggers.ctjs.objects;

import com.chattriggers.ctjs.libs.ChatLib;
import lombok.Getter;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;

import java.util.ArrayList;
import java.util.Collections;

public class Message {
    @Getter
    private ITextComponent chatMessage;
    private ArrayList<Object> messageParts;
    @Getter
    private int chatLineId;

    public Message(Object... messages) {
        messageParts = new ArrayList<>();

        Collections.addAll(messageParts, messages);
        parseMessages(messages);

        this.chatLineId = -1;
    }

    /**
     * Sets the chat line ID. Useful for deleting messages by ID.
     * @param id the ID of the message
     * @return the message for method chaining
     */
    public Message setChatLineId(int id) {
        this.chatLineId = id;

        return this;
    }

    /**
     * Sets a part of the message (defined by the splits made in the constructor)
     * @param part the index of the part to change
     * @param message the new message to replace with
     * @return the message for method chaining
     */
    public Message setMessagePart(int part, Object message) {
        messageParts.set(part, message);
        parseMessages(messageParts);

        return this;
    }

    /**
     * Get an exact copy of the message
     * @return the copy of the message
     */
    public Message copy() {
        if (chatLineId != -1) {
            return new Message(chatLineId, messageParts);
        }

        return new Message(messageParts);
    }

    private void parseMessages(Object... messages) {
        chatMessage = new TextComponentString("");

        for (Object message : messages) {
            if (message instanceof String) {
                TextComponentString cct = new TextComponentString(ChatLib.addColor((String) message));
                cct.setStyle(new Style().setParentStyle(null));

                chatMessage.appendSibling(cct);
            } else if (message instanceof ITextComponent) {
                chatMessage.appendSibling((ITextComponent) message);
            }
        }
    }
}
