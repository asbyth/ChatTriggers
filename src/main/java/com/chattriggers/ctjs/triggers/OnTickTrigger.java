package com.chattriggers.ctjs.triggers;

import com.chattriggers.ctjs.CTJS;
import com.chattriggers.ctjs.utils.console.Console;

import javax.script.ScriptException;

public class OnTickTrigger extends OnTrigger {
    public OnTickTrigger(String methodName) {
        super(methodName);
    }

    @Override
    public void trigger(Object... args) {
        if (!(args[0] instanceof Integer)) throw new IllegalArgumentException("1st argument is not an integer");

        try {
            CTJS.getInstance().getInvocableEngine().invokeFunction(methodName, args[0]);
        } catch (ScriptException | NoSuchMethodException e) {
            if (((int) args[0]) % 40 == 0) {
                Console.getConsole().printStackTrace(e);
            }
        }
    }
}
