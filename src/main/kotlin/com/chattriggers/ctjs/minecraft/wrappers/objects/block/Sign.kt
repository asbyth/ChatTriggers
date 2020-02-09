package com.chattriggers.ctjs.minecraft.wrappers.objects.block

import com.chattriggers.ctjs.minecraft.objects.message.Message
import com.chattriggers.ctjs.utils.kotlin.External
import net.minecraft.block.AbstractSignBlock

/**
 * Creates a new Sign object wrapper.<br>
 * Returned with {@link Player#lookingAt()} when looking at a sign.<br>
 * Extends {@link Block}.
 *
 * @param block the {@link Block} to convert to a Sign
 */
@External
class Sign(val sign: AbstractSignBlock) : Block(sign) {
    fun getLines(): List<Message> = sign.nameTextComponent.map { Message(it) }

    fun getFormattedLines(): List<String> = sign.nameTextComponent.map { it.formattedText }

    fun getUnformattedLines(): List<String> = sign.nameTextComponent.map { it.unformattedComponentText }

    override fun toString(): String =
        "Sign{lines=${getLines()}, name=${block.registryName}, x=${getX()}, y=${getY()}, z=${getZ()}}"
}