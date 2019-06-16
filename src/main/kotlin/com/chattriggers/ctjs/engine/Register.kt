package com.chattriggers.ctjs.engine

import com.chattriggers.ctjs.minecraft.wrappers.objects.inventory.Item
import com.chattriggers.ctjs.triggers.*
import org.graalvm.polyglot.Value
import kotlin.reflect.full.memberFunctions

@Suppress("unused")
open class Register {
    lateinit var lang: Lang

    /**
     * Helper method register a trigger. <br/>
     * Called by taking the original name of the method, i.e. `registerChat`,
     * removing the word register, and making the first letter lowercase.
     *
     * @param triggerType the type of trigger
     * @param method the name of the method or the actual method to callback when the event is fired
     * @return the trigger for additional modification
     */
    fun register(triggerType: String, method: Value): OnTrigger {
        val capitalizedName = triggerType.substring(0, 1).toUpperCase() + triggerType.substring(1)

        val func = this::class.memberFunctions.firstOrNull {
            it.name == "register$capitalizedName"
        }

        //println("params for func ${func?.name}: ${func?.parameters?.toString()}")

        return func?.call(this, method) as OnTrigger? ?: throw NoSuchMethodException()
    }

    /**
     * Registers a new trigger that runs before a chat message is received.<br></br>
     *
     *
     * Passes through multiple arguments:<br></br>
     * any number of chat criteria variables<br></br>
     * the chat event<br></br>
     * Available modifications:<br></br>
     * [OnRenderTrigger.triggerIfCanceled] Sets if triggered if event is already cancelled<br></br>
     * [OnChatTrigger.setChatCriteria] Sets the chat criteria<br></br>
     * [OnChatTrigger.setParameter] Sets the chat parameter<br></br>
     * [OnTrigger.setPriority] Sets the priority<br></br>
     *
     * @param method the name of the method to callback when the event is fired
     * @return the trigger for additional modification
     */
    fun registerChat(method: Value): OnChatTrigger {
        return OnChatTrigger(method, TriggerType.CHAT, lang)
    }

    /**
     * Registers a new trigger that runs before an action bar message is received.<br></br>
     *
     *
     * Passes through multiple arguments:<br></br>
     * any number of chat criteria variables<br></br>
     * the chat event<br></br>
     * Available modifications:<br></br>
     * [OnRenderTrigger.triggerIfCanceled] Sets if triggered if event is already cancelled<br></br>
     * [OnChatTrigger.setChatCriteria] Sets the chat criteria<br></br>
     * [OnChatTrigger.setParameter] Sets the chat parameter<br></br>
     * [OnTrigger.setPriority] Sets the priority<br></br>
     *
     * @param method the name of the method to callback when the event is fired
     * @return the trigger for additional modification
     */
    fun registerActionBar(method: Value): OnChatTrigger {
        return OnChatTrigger(method, TriggerType.ACTION_BAR, lang)
    }

    /**
     * Registers a trigger that runs before the world loads.<br></br>
     *
     *
     * Available modifications:<br></br>
     * [OnTrigger.setPriority] Sets the priority
     *
     * @param method the name of the method to callback when the event is fired
     * @return the trigger for additional modification
     */
    fun registerWorldLoad(method: Value): OnRegularTrigger {
        return OnRegularTrigger(method, TriggerType.WORLD_LOAD, lang)
    }

    /**
     * Registers a new trigger that runs before the world unloads.<br></br>
     *
     *
     * Available modifications:<br></br>
     * [OnTrigger.setPriority] Sets the priority
     *
     * @param method the name of the method to callback when the event is fired
     * @return the trigger for additional modification
     */
    fun registerWorldUnload(method: Value): OnRegularTrigger {
        return OnRegularTrigger(method, TriggerType.WORLD_UNLOAD, lang)
    }

    /**
     * Registers a new trigger that runs before a mouse button is being pressed or released.<br></br>
     *
     *
     * Passes through 4 arguments:<br></br>
     * mouse x<br></br>
     * mouse y<br></br>
     * mouse button<br></br>
     * mouse button state<br></br>
     * Available modifications:<br></br>
     * [OnTrigger.setPriority] Sets the priority
     *
     * @param method the name of the method to callback when the event is fired
     * @return the trigger for additional modification
     */
    fun registerClicked(method: Value): OnRegularTrigger {
        return OnRegularTrigger(method, TriggerType.CLICKED, lang)
    }

    /**
     * Registers a new trigger that runs while a mouse button is being held down.<br></br>
     *
     *
     * Passes through 5 arguments:<br></br>
     * mouse delta x<br></br>
     * mouse delta y<br></br>
     * mouse x<br></br>
     * mouse y<br></br>
     * mouse button<br></br>
     * Available modifications:<br></br>
     * [OnTrigger.setPriority] Sets the priority
     *
     * @param method the name of the method to callback when the event is fired
     * @return the trigger for additional modification
     */
    fun registerDragged(method: Value): OnRegularTrigger {
        return OnRegularTrigger(method, TriggerType.DRAGGED, lang)
    }

    /**
     * Registers a new trigger that runs before a sound is played.<br></br>
     *
     *
     * Passes through 6 arguments:<br></br>
     * the sound event<br></br>
     * the sound event's position<br></br>
     * the sound event's name<br></br>
     * the sound event's volume<br></br>
     * the sound event's pitch<br></br>
     * the sound event's category's name<br></br>
     * Available modifications:<br></br>
     * [OnSoundPlayTrigger.setCriteria] Sets the sound name criteria<br></br>
     * [OnTrigger.setPriority] Sets the priority
     *
     * @param method the name of the method to callback when the event is fired
     * @return the trigger for additional modification
     */
    fun registerSoundPlay(method: Value): OnSoundPlayTrigger {
        return OnSoundPlayTrigger(method, lang)
    }

    /**
     * Registers a new trigger that runs before a noteblock is played.<br></br>
     *
     *
     * Passes through 4 arguments:<br></br>
     * the note block play event<br></br>
     * the note block play event's Vector3d position<br></br>
     * the note block play event's note's name<br></br>
     * the note block play event's octave<br></br>
     * Available modifications:<br></br>
     * [OnTrigger.setPriority] Sets the priority
     *
     * @param method the name of the method to callback when the event is fired
     * @return the trigger for additional modification
     */
    fun registerNoteBlockPlay(method: Value): OnRegularTrigger {
        return OnRegularTrigger(method, TriggerType.NOTE_BLOCK_PLAY, lang)
    }

    /**
     * Registers a new trigger that runs before a noteblock is changed.<br></br>
     *
     *
     * Passes through 4 arguments:<br></br>
     * the note block change event<br></br>
     * the note block change event's Vector3d position<br></br>
     * the note block change event's note's name<br></br>
     * the note block change event's octave<br></br>
     * Available modifications:<br></br>
     * [OnTrigger.setPriority] Sets the priority
     *
     * @param method the name of the method to callback when the event is fired
     * @return the trigger for additional modification
     */
    fun registerNoteBlockChange(method: Value): OnRegularTrigger {
        return OnRegularTrigger(method, TriggerType.NOTE_BLOCK_CHANGE, lang)
    }

    /**
     * Registers a new trigger that runs before every game tick.<br></br>
     *
     *
     * Passes through 1 argument:<br></br>
     * ticks elapsed<br></br>
     * Available modifications:<br></br>
     * [OnTrigger.setPriority] Sets the priority
     *
     * @param method the name of the method to callback when the event is fired
     * @return the trigger for additional modification
     */
    fun registerTick(method: Value): OnRegularTrigger {
        return OnRegularTrigger(method, TriggerType.TICK, lang)
    }

    /**
     * Registers a new trigger that runs in predictable intervals. (60 per second by default)<br></br>
     *
     *
     * Passes through 1 argument:<br></br>
     * steps elapsed<br></br>
     * Available modifications:<br></br>
     * [OnStepTrigger.setFps] Sets the fps<br></br>
     * [OnStepTrigger.setDelay] Sets the delay in seconds<br></br>
     * [OnTrigger.setPriority] Sets the priority
     *
     * @param method the name of the method to callback when the event is fired
     * @return the trigger for additional modification
     */
    fun registerStep(method: Value): OnStepTrigger {
        return OnStepTrigger(method, lang)
    }

    /**
     * Registers a new trigger that runs before the world is drawn.<br></br>
     *
     *
     * Available modifications:<br></br>
     * [OnTrigger.setPriority] Sets the priority
     *
     * @param method the name of the method to callback when the event is fired
     * @return the trigger for additional modification
     */
    fun registerRenderWorld(method: Value): OnRegularTrigger {
        return OnRegularTrigger(method, TriggerType.RENDER_WORLD, lang)
    }

    /**
     * Registers a new trigger that runs before the overlay is drawn.<br></br>
     *
     *
     * Available modifications:<br></br>
     * [OnTrigger.setPriority] Sets the priority
     *
     * @param method the name of the method to callback when the event is fired
     * @return the trigger for additional modification
     */
    fun registerRenderOverlay(method: Value): OnRenderTrigger {
        return OnRenderTrigger(method, TriggerType.RENDER_OVERLAY, lang)
    }

    /**
     * Registers a new trigger that runs before the player list is being drawn.<br></br>
     *
     *
     * Passes through 1 argument:<br></br>
     * The render event<br></br>
     * Available modifications:<br></br>
     * [OnRenderTrigger.triggerIfCanceled] Sets if triggered if event is already cancelled<br></br>
     * [OnTrigger.setPriority] Sets the priority
     *
     * @param method the name of the method to callback when the event is fired
     * @return the trigger for additional modification
     */
    fun registerRenderPlayerList(method: Value): OnRenderTrigger {
        return OnRenderTrigger(method, TriggerType.RENDER_PLAYER_LIST, lang)
    }

    /**
     * Registers a new trigger that runs before the crosshair is being drawn.<br></br>
     *
     *
     * Passes through 1 argument:<br></br>
     * The render event<br></br>
     * Available modifications:<br></br>
     * [OnRenderTrigger.triggerIfCanceled] Sets if triggered if event is already cancelled<br></br>
     * [OnTrigger.setPriority] Sets the priority
     *
     * @param method the name of the method to callback when the event is fired
     * @return the trigger for additional modification
     */
    fun registerRenderCrosshair(method: Value): OnRenderTrigger {
        return OnRenderTrigger(method, TriggerType.RENDER_CROSSHAIR, lang)
    }

    /**
     * Registers a trigger that runs before the debug screen is being drawn.<br></br>
     *
     *
     * Passes through 1 argument:<br></br>
     * The render event<br></br>
     * Available modifications:<br></br>
     * [OnRenderTrigger.triggerIfCanceled] Sets if triggered if event is already cancelled<br></br>
     * [OnTrigger.setPriority] Sets the priority
     *
     * @param method the name of the method to callback when the event is fired
     * @return the trigger for additional modification
     */
    fun registerRenderDebug(method: Value): OnRenderTrigger {
        return OnRenderTrigger(method, TriggerType.RENDER_DEBUG, lang)
    }

    /**
     * Registers a new trigger that runs before the boss health bar is being drawn.<br></br>
     *
     *
     * Passes through 1 argument:<br></br>
     * The render event<br></br>
     * Available modifications:<br></br>
     * [OnRenderTrigger.triggerIfCanceled] Sets if triggered if event is already cancelled<br></br>
     * [OnTrigger.setPriority] Sets the priority
     *
     * @param method the name of the method to callback when the event is fired
     * @return the trigger for additional modification
     */
    fun registerRenderBossHealth(method: Value): OnRenderTrigger {
        return OnRenderTrigger(method, TriggerType.RENDER_BOSS_HEALTH, lang)
    }

    /**
     * Registers a new trigger that runs before the player's health is being drawn.<br></br>
     *
     *
     * Passes through 1 argument:<br></br>
     * The render event<br></br>
     * Available modifications:<br></br>
     * [OnRenderTrigger.triggerIfCanceled] Sets if triggered if event is already cancelled<br></br>
     * [OnTrigger.setPriority] Sets the priority
     *
     * @param method the name of the method to callback when the event is fired
     * @return the trigger for additional modification
     */
    fun registerRenderHealth(method: Value): OnRenderTrigger {
        return OnRenderTrigger(method, TriggerType.RENDER_HEALTH, lang)
    }

    /**
     * Registers a new trigger that runs before the player's food is being drawn.<br></br>
     *
     *
     * Passes through 1 argument:<br></br>
     * The render event<br></br>
     * Available modifications:<br></br>
     * [OnRenderTrigger.triggerIfCanceled] Sets if triggered if event is already cancelled<br></br>
     * [OnTrigger.setPriority] Sets the priority
     *
     * @param method the name of the method to callback when the event is fired
     * @return the trigger for additional modification
     */
    fun registerRenderFood(method: Value): OnRenderTrigger {
        return OnRenderTrigger(method, TriggerType.RENDER_FOOD, lang)
    }

    /**
     * Registers a new trigger that runs before the player's mount's health is being drawn.<br></br>
     *
     *
     * Passes through 1 argument:<br></br>
     * The render event<br></br>
     * Available modifications:<br></br>
     * [OnRenderTrigger.triggerIfCanceled] Sets if triggered if event is already cancelled<br></br>
     * [OnTrigger.setPriority] Sets the priority
     *
     * @param method the name of the method to callback when the event is fired
     * @return the trigger for additional modification
     */
    fun registerRenderMountHealth(method: Value): OnRenderTrigger {
        return OnRenderTrigger(method, TriggerType.RENDER_MOUNT_HEALTH, lang)
    }

    /**
     * Registers a new trigger that runs before the player's experience is being drawn.<br></br>
     *
     *
     * Passes through 1 argument:<br></br>
     * The render event<br></br>
     * Available modifications:<br></br>
     * [OnRenderTrigger.triggerIfCanceled] Sets if triggered if event is already cancelled<br></br>
     * [OnTrigger.setPriority] Sets the priority
     *
     * @param method the name of the method to callback when the event is fired
     * @return the trigger for additional modification
     */
    fun registerRenderExperience(method: Value): OnRenderTrigger {
        return OnRenderTrigger(method, TriggerType.RENDER_EXPERIENCE, lang)
    }

    /**
     * Registers a new trigger that runs before the player's hotbar is drawn.<br></br>
     *
     *
     * Passes through 1 argument:<br></br>
     * The render event<br></br>
     * Available modifications:<br></br>
     * [OnRenderTrigger.triggerIfCanceled] Sets if triggered if event is already cancelled<br></br>
     * [OnTrigger.setPriority] Sets the priority
     *
     * @param method the name of the method to callback when the event is fired
     * @return the trigger for additional modification
     */
    fun registerRenderHotbar(method: Value): OnRenderTrigger {
        return OnRenderTrigger(method, TriggerType.RENDER_HOTBAR, lang)
    }

    /**
     * Registers a new trigger that runs before the player's air level is drawn.<br></br>
     *
     *
     * Passes through 1 argument:<br></br>
     * The render event<br></br>
     * Available modifications:<br></br>
     * [OnRenderTrigger.triggerIfCanceled] Sets if triggered if event is already cancelled<br></br>
     * [OnTrigger.setPriority] Sets the priority
     *
     * @param method the name of the method to callback when the event is fired
     * @return the trigger for additional modification
     */
    fun registerRenderAir(method: Value): OnRenderTrigger {
        return OnRenderTrigger(method, TriggerType.RENDER_AIR, lang)
    }

    /**
     * Registers a new trigger that runs before the block highlight box is drawn.<br></br>
     *
     *
     * Passes through 2 arguments:<br></br>
     * The draw block highlight event<br></br>
     * The draw block highlight event's position<br></br>
     * Available modifications:<br></br>
     * [OnTrigger.setPriority] Sets the priority
     *
     * @param method the name of the method to callback when the event is fired
     * @return the trigger for additional modification
     */
    fun registerDrawBlockHighlight(method: Value): OnRegularTrigger {
        return OnRegularTrigger(method, TriggerType.BLOCK_HIGHLIGHT, lang)
    }

    /**
     * Registers a new trigger that runs after the game loads.<br></br>
     * This runs after the initial loading of the game directly after scripts are loaded and after "/ct loadExtra" happens.<br></br>
     *
     *
     * Available modifications:<br></br>
     * [OnTrigger.setPriority] Sets the priority
     *
     * @param method the name of the method to callback when the event is fired
     * @return the trigger for additional modification
     */
    fun registerGameLoad(method: Value): OnRegularTrigger {
        return OnRegularTrigger(method, TriggerType.GAME_LOAD, lang)
    }

    /**
     * Registers a new trigger that runs before the game unloads.<br></br>
     * This runs before shutdown of the JVM and before "/ct loadExtra" happens.<br></br>
     *
     *
     * Available modifications:<br></br>
     * [OnTrigger.setPriority] Sets the priority
     *
     * @param method the name of the method to callback when the event is fired
     * @return the trigger for additional modification
     */
    fun registerGameUnload(method: Value): OnRegularTrigger {
        return OnRegularTrigger(method, TriggerType.GAME_UNLOAD, lang)
    }

    /**
     * Registers a new command that will run the method provided.<br></br>
     *
     *
     * Passes through multiple arguments:<br></br>
     * The arguments supplied to the command by the user<br></br>
     * Available modifications:<br></br>
     * [OnCommandTrigger.setCommandName] Sets the command name<br></br>
     * [OnTrigger.setPriority] Sets the priority
     *
     * @param method the name of the method to callback when the event is fired
     * @return the trigger for additional modification
     */
    fun registerCommand(method: Value): OnCommandTrigger {
        return OnCommandTrigger(method, lang)
    }

    /**
     * Registers a new trigger that runs when a new gui is first opened.<br></br>
     *
     *
     * Passes through 1 argument:<br></br>
     * the gui opened event<br></br>
     * Available modifications:<br></br>
     * [OnTrigger.setPriority] Sets the priority
     *
     * @param method the name of the method to callback when the event is fired
     * @return the trigger for additional modification
     */
    fun registerGuiOpened(method: Value): OnRegularTrigger {
        return OnRegularTrigger(method, TriggerType.GUI_OPENED, lang)
    }

    /**
     * Registers a new trigger that runs when a player joins the world.<br></br>
     * Maximum is one per tick. Any extras will queue and run in later ticks.<br></br>
     * This trigger is asynchronous.<br></br>
     *
     *
     * Passes through 1 argument:<br></br>
     * the [com.chattriggers.ctjs.minecraft.wrappers.objects.PlayerMP] object<br></br>
     * Available modifications:<br></br>
     * [OnTrigger.setPriority] Sets the priority
     *
     * @param method the name of the method to callback when the event is fired
     * @return the trigger for additional modification
     */
    fun registerPlayerJoined(method: Value): OnRegularTrigger {
        return OnRegularTrigger(method, TriggerType.PLAYER_JOIN, lang)
    }

    /**
     * Registers a new trigger that runs when a player leaves the world.<br></br>
     * Maximum is one per tick. Any extras will queue and run in later ticks.<br></br>
     * This trigger is asynchronous.<br></br>
     *
     *
     * Passes through 1 argument:<br></br>
     * the name of the player that left<br></br>
     * Available modifications:<br></br>
     * [OnTrigger.setPriority] Sets the priority
     *
     * @param method the name of the method to callback when the event is fired
     * @return the trigger for additional modification
     */
    fun registerPlayerLeft(method: Value): OnRegularTrigger {
        return OnRegularTrigger(method, TriggerType.PLAYER_LEAVE, lang)
    }

    /**
     * Registers a new trigger that runs before an item is picked up.<br></br>
     *
     *
     * Passes through 3 arguments:<br></br>
     * the [Item] that is picked up<br></br>
     * the [com.chattriggers.ctjs.minecraft.wrappers.objects.PlayerMP] that picked up the item<br></br>
     * the item's position vector<br></br>
     * the item's motion vector<br></br>
     * Available modifications:<br></br>
     * [OnTrigger.setPriority] Sets the priority
     *
     * @param method the name of the method to callback when the event is fired
     * @return the trigger for additional modification
     */
    fun registerPickupItem(method: Value): OnRegularTrigger {
        return OnRegularTrigger(method, TriggerType.PICKUP_ITEM, lang)
    }

    /**
     * Registers a new trigger that runs before an item is dropped.<br></br>
     *
     *
     * Passes through 3 arguments:<br></br>
     * the [Item] that is dropped up<br></br>
     * the [com.chattriggers.ctjs.minecraft.wrappers.objects.PlayerMP] that dropped the item<br></br>
     * the item's position vector<br></br>
     * the item's motion vector<br></br>
     * Available modifications:<br></br>
     * [OnTrigger.setPriority] Sets the priority
     *
     * @param method the name of the method to callback when the event is fired
     * @return the trigger for additional modification
     */
    fun registerDropItem(method: Value): OnRegularTrigger {
        return OnRegularTrigger(method, TriggerType.DROP_ITEM, lang)
    }

    /**
     * Registers a new trigger that runs before a screenshot is taken.<br></br>
     *
     *
     * Passes through 2 arguments:<br></br>
     * the name of the screenshot<br></br>
     * the screenshot event<br></br>
     * Available modifications:<br></br>
     * [OnTrigger.setPriority] Sets the priority
     *
     * @param method the name of the method to callback when the event is fired
     * @return the trigger for additional modification
     */
    fun registerScreenshotTaken(method: Value): OnRegularTrigger {
        return OnRegularTrigger(method, TriggerType.SCREENSHOT_TAKEN, lang)
    }

    /**
     * Registers a new trigger that runs before a message is sent in chat.<br></br>
     *
     *
     * Passes through 2 arguments:<br></br>
     * the message event<br></br>
     * the message<br></br>
     * Available modifications:<br></br>
     * [OnTrigger.setPriority] Sets the priority
     *
     * @param method the name of the method to callback when the event is fired
     * @return the trigger for additional modification
     */
    fun registerMessageSent(method: Value): OnRegularTrigger {
        return OnRegularTrigger(method, TriggerType.MESSAGE_SENT, lang)
    }

    /**
     * Registers a new trigger that runs before a message is sent in chat.<br></br>
     *
     *
     * Passes through 2 arguments:<br></br>
     * the list of lore to modify<br></br>
     * the [Item] that this lore is attached to.<br></br>
     * Available modifications:<br></br>
     * [OnTrigger.setPriority] Sets the priority
     *
     * @param method the name of the method to callback when the event is fired
     * @return the trigger for additional modification
     */
    fun registerItemTooltip(method: Value): OnRegularTrigger {
        return OnRegularTrigger(method, TriggerType.TOOLTIP, lang)
    }
}