package com.chattriggers.ctjs.minecraft.wrappers

import com.chattriggers.ctjs.utils.kotlin.External
import net.minecraft.client.settings.AmbientOcclusionStatus
import net.minecraft.client.settings.CloudOption
import net.minecraft.client.settings.ParticleStatus
import net.minecraft.entity.player.ChatVisibility
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerModelPart
import net.minecraft.util.SoundCategory
import net.minecraft.world.Difficulty

@External
object Settings {
    @JvmStatic
    fun getSettings() = Client.getMinecraft().gameSettings

    @JvmStatic
    fun getFOV() = getSettings().fov

    @JvmStatic
    fun setFOV(fov: Double) {
        getSettings().fov = fov
    }

    @JvmStatic
    fun getDifficulty() = getSettings().difficulty.id

    @JvmStatic
    fun setDifficulty(difficulty: Int) {
        getSettings().difficulty = Difficulty.byId(difficulty)
    }

    @JvmStatic
    fun setDifficulty(difficulty: String) {
        getSettings().difficulty = Difficulty.byName(difficulty.toUpperCase())
    }

    object skin {
        @JvmStatic
        fun getCape() = getSettings().modelParts.contains(PlayerModelPart.CAPE)

        @JvmStatic
        fun setCape(toggled: Boolean) {
            setModelPart(toggled, PlayerModelPart.CAPE)
        }

        @JvmStatic
        fun getJacket() = getSettings().modelParts.contains(PlayerModelPart.JACKET)

        @JvmStatic
        fun setJacket(toggled: Boolean) {
            setModelPart(toggled, PlayerModelPart.JACKET)
        }

        @JvmStatic
        fun getLeftSleeve() = getSettings().modelParts.contains(PlayerModelPart.LEFT_SLEEVE)

        @JvmStatic
        fun setLeftSleeve(toggled: Boolean) {
            setModelPart(toggled, PlayerModelPart.LEFT_SLEEVE)
        }

        @JvmStatic
        fun getRightSleeve() = getSettings().modelParts.contains(PlayerModelPart.RIGHT_SLEEVE)

        @JvmStatic
        fun setRightSleeve(toggled: Boolean) {
            setModelPart(toggled, PlayerModelPart.RIGHT_SLEEVE)
        }

        @JvmStatic
        fun getLeftPantsLeg() = getSettings().modelParts.contains(PlayerModelPart.LEFT_PANTS_LEG)

        @JvmStatic
        fun setLeftPantsLef(toggled: Boolean) {
            setModelPart(toggled, PlayerModelPart.LEFT_PANTS_LEG)
        }

        @JvmStatic
        fun getRightPantsLeg() = getSettings().modelParts.contains(PlayerModelPart.RIGHT_PANTS_LEG)

        @JvmStatic
        fun setRightPantsLef(toggled: Boolean) {
            setModelPart(toggled, PlayerModelPart.RIGHT_PANTS_LEG)
        }

        @JvmStatic
        fun getHat() = getSettings().modelParts.contains(PlayerModelPart.HAT)

        @JvmStatic
        fun setHat(toggled: Boolean) {
            setModelPart(toggled, PlayerModelPart.HAT)
        }

        private fun setModelPart(toggled: Boolean, modelPart: PlayerModelPart) {
            if (toggled) getSettings().modelParts.add(modelPart)
            else getSettings().modelParts.remove(modelPart)
        }
    }

    object sound {
        @JvmStatic
        fun getMasterVolume() = getSettings().getSoundLevel(SoundCategory.MASTER)

        @JvmStatic
        fun setMasterVolume(level: Float) = getSettings().setSoundLevel(SoundCategory.MASTER, level)

        @JvmStatic
        fun getMusicVolume() = getSettings().getSoundLevel(SoundCategory.MUSIC)

        @JvmStatic
        fun setMusicVolume(level: Float) = getSettings().setSoundLevel(SoundCategory.MUSIC, level)

        @JvmStatic
        fun getNoteblockVolume() = getSettings().getSoundLevel(SoundCategory.RECORDS)

        @JvmStatic
        fun setNoteblockVolume(level: Float) = getSettings().setSoundLevel(SoundCategory.RECORDS, level)

        @JvmStatic
        fun getWeather() = getSettings().getSoundLevel(SoundCategory.WEATHER)

        @JvmStatic
        fun setWeather(level: Float) = getSettings().setSoundLevel(SoundCategory.WEATHER, level)

        @JvmStatic
        fun getBlocks() = getSettings().getSoundLevel(SoundCategory.BLOCKS)

        @JvmStatic
        fun setBlocks(level: Float) = getSettings().setSoundLevel(SoundCategory.BLOCKS, level)

        //#if MC<=10809
        @JvmStatic
        fun getHostileCreatures() = getSettings().getSoundLevel(SoundCategory.HOSTILE)

        @JvmStatic
        fun setHostileCreatures(level: Float) = getSettings().setSoundLevel(SoundCategory.HOSTILE, level)

        @JvmStatic
        fun getFriendlyCreatures() = getSettings().getSoundLevel(SoundCategory.NEUTRAL)

        @JvmStatic
        fun setFriendlyCreatures(level: Float) = getSettings().setSoundLevel(SoundCategory.NEUTRAL, level)

        @JvmStatic
        fun getPlayers() = getSettings().getSoundLevel(SoundCategory.PLAYERS)

        @JvmStatic
        fun setPlayers(level: Float) = getSettings().setSoundLevel(SoundCategory.PLAYERS, level)

        @JvmStatic
        fun getAmbient() = getSettings().getSoundLevel(SoundCategory.AMBIENT)

        @JvmStatic
        fun setAmbient(level: Float) = getSettings().setSoundLevel(SoundCategory.AMBIENT, level)
    }

    object video {
        @JvmStatic
        fun getGraphics() = getSettings().fancyGraphics

        @JvmStatic
        fun setGraphics(fancy: Boolean) {
            getSettings().fancyGraphics = fancy
        }

        @JvmStatic
        fun getRenderDistance() = getSettings().renderDistanceChunks

        @JvmStatic
        fun setRenderDistance(distance: Int) {
            getSettings().renderDistanceChunks = distance
        }

        @JvmStatic
        fun getSmoothLighting() = getSettings().ambientOcclusionStatus.id

        @JvmStatic
        fun setSmoothLighting(level: Int) {
            getSettings().ambientOcclusionStatus = AmbientOcclusionStatus.getValue(level)
        }

        @JvmStatic
        fun getMaxFrameRate() = getSettings().framerateLimit

        @JvmStatic
        fun setMaxFrameRate(frameRate: Int) {
            getSettings().framerateLimit = frameRate
        }

        @JvmStatic
        fun get3dAnaglyph(): Nothing = throw UnsupportedOperationException("1.15 has no 3DAnaglyph setting")

        @JvmStatic
        fun set3dAnaglyph(toggled: Boolean): Nothing = throw UnsupportedOperationException("1.15 has no 3DAnaglyph setting")

        @JvmStatic
        fun getBobbing() = getSettings().viewBobbing

        @JvmStatic
        fun setBobbing(toggled: Boolean) {
            getSettings().viewBobbing = toggled
        }

        @JvmStatic
        fun getGuiScale() = getSettings().guiScale

        @JvmStatic
        fun setGuiScale(scale: Int) {
            getSettings().guiScale = scale
        }

        @JvmStatic
        fun getBrightness() = getSettings().gamma

        @JvmStatic
        fun setBrightness(brightness: Double) {
            getSettings().gamma = brightness
        }

        @JvmStatic
        fun getClouds() = getSettings().cloudOption.id

        @JvmStatic
        fun setClouds(clouds: Int) {
            getSettings().cloudOption = CloudOption.byId(clouds)
        }

        @JvmStatic
        fun getParticles() = getSettings().particles.id

        @JvmStatic
        fun setParticles(particles: Int) {
            getSettings().particles = ParticleStatus.byId(particles)
        }

        @JvmStatic
        fun getFullscreen() = getSettings().fullscreen

        @JvmStatic
        fun setFullscreen(toggled: Boolean) {
            getSettings().fullscreen = toggled
        }

        @JvmStatic
        fun getVsync() = getSettings().vsync

        @JvmStatic
        fun setVsync(toggled: Boolean) {
            getSettings().vsync = toggled
        }

        @JvmStatic
        fun getMipmapLevels() = getSettings().mipmapLevels

        @JvmStatic
        fun setMipmapLevels(mipmapLevels: Int) {
            getSettings().mipmapLevels = mipmapLevels
        }

        @JvmStatic
        fun getAlternateBlocks(): Nothing = throw UnsupportedOperationException("1.15 has no Alternative Blocks settings")

        @JvmStatic
        fun setAlternateBlocks(toggled: Boolean): Nothing = throw UnsupportedOperationException("1.15 has no Alternative Blocks settings")

        @JvmStatic
        fun getVBOs(): Nothing = throw UnsupportedOperationException("1.15 has no VBO settings")

        @JvmStatic
        fun setVBOs(toggled: Boolean): Nothing = throw UnsupportedOperationException("1.15 has no VBO settings")

        @JvmStatic
        fun getEntityShadows() = getSettings().entityShadows

        @JvmStatic
        fun setEntityShadows(toggled: Boolean) {
            getSettings().entityShadows = toggled
        }
    }

    object chat {
        // show chat
        @JvmStatic
        fun getVisibility() = getSettings().chatVisibility

        @JvmStatic
        fun setVisibility(visibility: String) {
            when (visibility.toLowerCase()) {
                "hidden" -> getSettings().chatVisibility = ChatVisibility.HIDDEN
                "commands", "system" -> getSettings().chatVisibility = ChatVisibility.SYSTEM
                else -> getSettings().chatVisibility = ChatVisibility.FULL
            }
        }

        // colors
        @JvmStatic
        fun getColors() = getSettings().chatColor

        @JvmStatic
        fun setColors(toggled: Boolean) {
            getSettings().chatColor = toggled
        }

        // web links
        @JvmStatic
        fun getWebLinks() = getSettings().chatLinks

        @JvmStatic
        fun setWebLinks(toggled: Boolean) {
            getSettings().chatLinks = toggled
        }

        // opacity
        @JvmStatic
        fun getOpacity() = getSettings().chatOpacity

        @JvmStatic
        fun setOpacity(opacity: Double) {
            getSettings().chatOpacity = opacity
        }

        // prompt on links
        @JvmStatic
        fun getPromptOnWebLinks() = getSettings().chatLinksPrompt

        @JvmStatic
        fun setPromptOnWebLinks(toggled: Boolean) {
            getSettings().chatLinksPrompt = toggled
        }

        // GUI scale
        @JvmStatic
        fun getGUIScale() = getSettings().guiScale

        @JvmStatic
        fun setGUIScale(scale: Int) {
            getSettings().guiScale
        }

        // chat scale
        @JvmStatic
        fun getChatScale() = getSettings().chatScale

        @JvmStatic
        fun setChatScale(scale: Int) {
            getSettings().chatScale
        }

        // focused height
        @JvmStatic
        fun getChatFocusedHeight() = getSettings().chatHeightFocused

        @JvmStatic
        fun setChatFocusedHeight(height: Double) {
            getSettings().chatHeightFocused = height
        }

        // unfocused height
        @JvmStatic
        fun getChatUnfocusedHeight() = getSettings().chatHeightUnfocused

        @JvmStatic
        fun setChatUnfocusedHeight(height: Double) {
            getSettings().chatHeightUnfocused = height
        }

        // width
        @JvmStatic
        fun getChatWidth() = getSettings().chatWidth

        @JvmStatic
        fun setChatWidth(width: Double) {
            getSettings().chatWidth = width
        }

        // reduced debug info
    }
}