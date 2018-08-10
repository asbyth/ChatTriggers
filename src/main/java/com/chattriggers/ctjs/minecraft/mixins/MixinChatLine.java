package com.chattriggers.ctjs.minecraft.mixins;

import net.minecraft.client.gui.ChatLine;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChatLine.class)
public interface MixinChatLine {
    @Accessor
    void setLineString(ITextComponent chatComponent);
    @Accessor
    void setChatLineID(int id);
}
