package com.chattriggers.ctjs.engine

import com.chattriggers.ctjs.CTJS
import com.chattriggers.ctjs.Reference
import com.chattriggers.ctjs.engine.Loader.Companion.getFoldersInDir
import com.chattriggers.ctjs.engine.Loader.Companion.modulesFolder
import com.chattriggers.ctjs.engine.module.Module
import com.chattriggers.ctjs.engine.module.ModuleMetadata
import com.chattriggers.ctjs.minecraft.libs.ChatLib
import com.chattriggers.ctjs.minecraft.libs.FileLib
import com.chattriggers.ctjs.print
import com.chattriggers.ctjs.triggers.TriggerType
import com.chattriggers.ctjs.utils.config.Config
import com.chattriggers.ctjs.utils.console.Console
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.IOException
import java.net.URL

object ModuleManager {
    var cachedModules = mutableListOf<Module>()

    @JvmField
    var toDownload = mutableListOf<String>()

    /**
     * Finds all modules to be installed, checks for updates if
     * specified, and installs any required modules if necessary.
     */
    fun loadModules(updateCheck: Boolean) {
        loadAssets()

        toDownload.forEach { importModule(it, false) }
        toDownload.clear()

        getFoldersInDir(modulesFolder).map {
            getModule(it, updateCheck)
        }.flatten().distinctBy {
            it.name.toLowerCase()
        }.also { PrimaryLoader.initialize(it) }
    }

    /**
     * Loads all module assets into the CT assets directory
     */
    private fun loadAssets() = getFoldersInDir(modulesFolder).map {
        File(it, "assets")
    }.filter {
        it.exists() && !it.isFile
    }.map {
        it.listFiles()?.toList() ?: listOf()
    }.flatten().forEach {
        FileUtils.copyFileToDirectory(it, CTJS.assetsDir)
    }

    /**
     * Imports a module from the CT website to the user's module directory
     */
    fun importModule(name: String, extra: Boolean = true, isRequired: Boolean = false) = if (extra) {
        run {
            ChatLib.chat("&7Importing $name...")
            val modules = importModuleHelper(name) ?: return@run

            modules.forEach { module ->
                cachedModules.add(module)
                PrimaryLoader.loadExtra(module)
                if (isRequired) module.metadata.isRequired = true
            }

            ChatLib.chat("&aSuccessfully imported $name")
        }
        null
    } else {
        importModuleHelper(name)
    }

    /**
     * Called by [importModule], ensures the module exists on the website
     * and imports it
     */
    private fun importModuleHelper(name: String) = try {
        // Ensure module exists
        FileLib.getUrlContent("https://www.chattriggers.com/downloads/metadata/$name")

        downloadModule(name)

        val moduleFolder = getFoldersInDir(modulesFolder).firstOrNull {
            it.name.toLowerCase() == name.toLowerCase()
        }!!

        getModule(moduleFolder, false)
    } catch (e: IOException) {
        e.print()
        ChatLib.chat("&cCan't find module with name $name")
        null
    }

    /**
     * Deletes a module
     */
    fun deleteModule(name: String): Boolean {
        return if (FileLib.deleteDirectory(File(Config.modulesFolder, name))) {
            Reference.loadCT()
            true
        } else {
            false
        }
    }

    /**
     * Returns a module given its directory. Updates the module if necessary
     */
    private fun getModule(dir: File, updateCheck: Boolean): List<Module> {
        val metadataFile = File(dir, "metadata.json")
        var metadata = ModuleMetadata()
        val modules = mutableListOf<Module>()

        if (metadataFile.exists()) {
            try {
                metadata = ModuleMetadata.fromFile(metadataFile)
                metadata.fileName = dir.name
            } catch (exception: Exception) {
                exception.print()
            }
        }

        try {
            if (!metadata.isDefault && updateCheck) {
                "checking for update in ${metadata.fileName}".print()

                val newMetadata = URL("https://www.chattriggers.com/downloads/metadata/${metadata.fileName}")
                        .openConnection()
                        .let { conn ->
                            conn.setRequestProperty("User-Agent", "Mozilla/5.0")
                            conn.getInputStream().bufferedReader()
                                    .use { it.readText() }
                                    .let { ModuleMetadata.fromJson(it) }
                        }

                val name = metadata.fileName

                if (newMetadata.version != metadata.version && name != null) {
                    downloadModule(name)

                    ChatLib.chat("&6Updated " + metadata.name)
                }
            }

            modules.addAll(getRequiredModules(metadata, updateCheck))
        } catch (e: IOException) {
            e.print()
            "Can't find page for ${dir.name}".print()
        } catch (e: Exception) {
            "Error loading module from $dir".print()
            e.print()
        }

        modules.add(Module(dir.name, metadata, dir))

        return modules
    }

    /**
     * Downloads a module from the website
     */
    private fun downloadModule(name: String) = try {
        val downloadZip = File(modulesFolder, "currDownload.zip")

        val conn = URL("https://www.chattriggers.com/downloads/scripts/$name").openConnection()
        conn.setRequestProperty("User-Agent", "Mozilla/5.0")
        FileUtils.copyInputStreamToFile(conn.getInputStream(), downloadZip)

        FileLib.unzip(downloadZip.absolutePath, modulesFolder.absolutePath)
        downloadZip.delete()
        true
    } catch (e: IOException) {
        e.print()
        false
    }

    private fun getRequiredModules(metadata: ModuleMetadata, updateCheck: Boolean): List<Module> {
        if (metadata.isDefault || metadata.requires == null) return listOf()

        return metadata.requires.map {
            File(modulesFolder, it)
        }.map { moduleFile ->
            if (moduleFile.exists()) {
                return@map getModule(moduleFile, updateCheck).map {
                    it.metadata.isRequired = true
                    it
                }
            } else {
                return@map importModule(moduleFile.name, false, isRequired = true) ?: listOf()
            }
        }.flatten()
    }
}