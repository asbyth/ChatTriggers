package com.chattriggers.ctjs.commands;

import com.chattriggers.ctjs.triggers.OnTrigger;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class Command extends CommandBase {
    private String name;
    private String usage;
    private OnTrigger trigger;

    public Command(OnTrigger trigger, String name, String usage) {
        this.trigger = trigger;
        this.name = name;
        this.usage = usage;
    }

    @Override
    public String getName() {return getCommandName();}
    public String getCommandName() {
        return name;
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public String getUsage(ICommandSender sender) {return getCommandName();}
    public String getCommandUsage(ICommandSender sender) {
        return usage;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {processCommand(sender, args);}
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        trigger.trigger((Object[]) args);
    }
}
