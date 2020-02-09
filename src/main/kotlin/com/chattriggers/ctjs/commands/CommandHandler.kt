package com.chattriggers.ctjs.commands

import com.chattriggers.ctjs.Reference
import com.chattriggers.ctjs.engine.module.ModuleManager
import com.chattriggers.ctjs.engine.module.ModulesGui
import com.chattriggers.ctjs.minecraft.libs.ChatLib
import com.chattriggers.ctjs.minecraft.listeners.ChatListener
import com.chattriggers.ctjs.minecraft.objects.gui.GuiHandler
import com.chattriggers.ctjs.minecraft.objects.message.Message
import com.chattriggers.ctjs.print
import com.chattriggers.ctjs.utils.config.GuiConfig
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandExceptionType
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.util.text.StringTextComponent
import net.minecraft.util.text.Style
import net.minecraft.util.text.event.ClickEvent
import net.minecraft.util.text.event.HoverEvent
import java.awt.Desktop
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.io.File
import java.io.IOException
import java.util.concurrent.CompletableFuture

object CommandHandler {
    private const val idFixed = 90123
    private var idFixedOffset = -1
    private var commandList = mutableListOf<UserCommand>()
    val commandDispatcher = CommandDispatcher<CommandSource>()

    init {
        Commands.literal("ct")
            .then(Commands.argument("action", object : ArgumentType<CTCommand> {
                override fun parse(reader: StringReader): CTCommand {
                    // TODO: Ensure all exception messages get printed in red
                    return when (reader.readString()) {
                        "load" -> CTCommand.LOAD
                        "reload" -> CTCommand.RELOAD
                        "unload" -> CTCommand.UNLOAD
                        "files", "file" -> CTCommand.FILES
                        "import" -> if (reader.canRead()) {
                            CTCommand.IMPORT(reader.readString())
                        } else {
                            throw getSyntaxException("/ct import <module name>")
                        }
                        "delete" -> if (reader.canRead()) {
                            CTCommand.DELETE(reader.readString())
                        } else {
                            throw getSyntaxException("/ct delete <module name>")
                        }
                        "modules" -> CTCommand.MODULES
                        "console" -> CTCommand.CONSOLE(if (reader.canRead()) reader.readString() else null)
                        "config", "settings", "setting" -> CTCommand.SETTINGS
                        "sim", "simulate" -> CTCommand.SIMULATE(reader.getActionArgs())
                        "dump" -> CTCommand.DUMP(reader.getActionArgs())
                        "copy" -> CTCommand.COPY(reader.getActionArgs())
                        else -> throw getSyntaxException("Unrecognized CT command")
                    }
                }

                override fun <S> listSuggestions(context: CommandContext<S>, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
                    // TODO: Suggestions for sub-commands?
                    return builder
                        .suggest("load").suggest("reload").suggest("unload").suggest("files")
                        .suggest("import").suggest("delete").suggest("modules").suggest("console")
                        .suggest("settings").suggest("simluate").suggest("dump").suggest("copy")
                        .buildFuture()
                }
            })).executes { context ->
                when (val action = context.getArgument("action", CTCommand::class.java)) {
                    CTCommand.LOAD -> Reference.loadCT()
                    CTCommand.RELOAD -> Reference.reloadCT()
                    CTCommand.UNLOAD -> Reference.unloadCT()
                    CTCommand.FILES -> openFileLocation()
                    is CTCommand.IMPORT -> ModuleManager.importModule(action.moduleName)
                    is CTCommand.DELETE -> ChatLib.chat(
                        (if (ModuleManager.deleteModule(action.moduleName)) "&aDeleted " else "&cFailed to delete ") +
                            action.moduleName
                    )
                    CTCommand.MODULES -> GuiHandler.openGui(ModulesGui)
                    is CTCommand.CONSOLE -> if (action.language == null) {
                        ModuleManager.generalConsole.showConsole()
                    } else {
                        ModuleManager.getConsole(action.language).showConsole()
                    }
                    CTCommand.SETTINGS -> GuiHandler.openGui(GuiConfig())
                    is CTCommand.SIMULATE -> ChatLib.simulateChat(action.args.joinToString(separator = " "))
                    is CTCommand.DUMP -> dump(action.args)
                    is CTCommand.COPY -> copyArgsToClipboard(action.args)
                }
                0
            }.run(commandDispatcher::register)
    }

    private fun openFileLocation() {
        try {
            Desktop.getDesktop().open(File("./config/ChatTriggers"))
        } catch (exception: IOException) {
            exception.print()
            ChatLib.chat("&cCould not open file location")
        }
    }

    private fun dump(args: List<String>) {
        if (args.isEmpty()) {
            dumpChat()
            return
        }

        when (args[0].toLowerCase()) {
            "chat" -> {
                if (args.size == 1) dumpChat()
                else dumpChat(args[1].toInt())
            }
            "actionbar" -> {
                if (args.size == 1) dumpActionBar()
                else dumpActionBar(args[1].toInt())
            }
        }
    }

    private fun dumpChat(lines: Int = 100) = dumpList(ChatListener.chatHistory, lines)
    private fun dumpActionBar(lines: Int = 100) = dumpList(ChatListener.actionBarHistory, lines)
    private fun dumpList(messages: List<String>, lines: Int) {
        clearOldDump()

        var toDump = lines
        if (toDump > messages.size) toDump = messages.size
        Message("&6&m${ChatLib.getChatBreak()}").setChatLineId(this.idFixed).chat()
        var msg: String
        for (i in 0 until toDump) {
            msg = ChatLib.replaceFormatting(messages[messages.size - toDump + i])
            Message(
                StringTextComponent(msg)
                    .setStyle(Style().apply {
                        clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ct copy $msg")
                        hoverEvent = HoverEvent(
                            HoverEvent.Action.SHOW_TEXT,
                            StringTextComponent(ChatLib.addColor("&eClick here to copy this message."))
                        )
                    })
            ).setFormatted(false).setChatLineId(this.idFixed + i + 1).chat()
        }
        Message("&6&m${ChatLib.getChatBreak()}").setChatLineId(this.idFixed + lines + 1).chat()

        this.idFixedOffset = this.idFixed + lines + 1
    }

    private fun clearOldDump() {
        if (this.idFixedOffset == -1) return
        while (this.idFixedOffset >= this.idFixed)
            ChatLib.clearChat(this.idFixedOffset--)
        this.idFixedOffset = -1
    }

    private fun copyArgsToClipboard(args: List<String>) {
        clearOldDump()
        Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(args.joinToString(" ")), null)
    }

    @JvmStatic
    fun getCommandList() = this.commandList

    fun StringReader.getActionArgs() = this.string.split(" ").drop(2)

    fun getSyntaxException(message: String) = CommandSyntaxException(object : CommandExceptionType {}, com.mojang.brigadier.Message {
        message
    })

    sealed class CTCommand {
        object LOAD                              : CTCommand()
        object RELOAD                            : CTCommand()
        object UNLOAD                            : CTCommand()
        object FILES                             : CTCommand()
        class  IMPORT(val moduleName: String)    : CTCommand()
        class  DELETE(val moduleName: String)    : CTCommand()
        object MODULES                           : CTCommand()
        class  CONSOLE(val language: String?)    : CTCommand()
        object SETTINGS                          : CTCommand()
        class  SIMULATE(val args: List<String>)  : CTCommand()
        class  DUMP(val args: List<String>)      : CTCommand()
        class  COPY(val args: List<String>)      : CTCommand()
    }
}