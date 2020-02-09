package com.chattriggers.ctjs.commands

import com.chattriggers.ctjs.triggers.OnTrigger
import net.minecraft.command.Commands

class UserCommand(
    trigger: OnTrigger,
    name: String/*,
    private val usage: String,
    private val tabCompletionOptions: MutableList<String>*/
) {
    private var triggers = mutableListOf<OnTrigger>()

    init {
        this.triggers.add(trigger)

        Commands.literal(name)
            .then(Commands.argument("args") { reader ->
                mutableListOf<String>().apply {
                    while (reader.canRead()) {
                        add(reader.readString())
                    }
                }.toTypedArray()
            })
            .executes { context ->
                val args = context.getArgument("args", Array<String>::class.java)

                triggers.forEach {
                    it.trigger(*args)
                }

                0
            }.run(CommandHandler.commandDispatcher::register)
    }

    fun getTriggers() = this.triggers
}
