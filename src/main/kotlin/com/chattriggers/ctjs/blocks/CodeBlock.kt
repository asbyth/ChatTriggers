package com.chattriggers.ctjs.blocks

import com.chattriggers.ctjs.engine.ModuleManager
import com.chattriggers.ctjs.minecraft.libs.ChatLib
import com.chattriggers.ctjs.minecraft.libs.FileLib
import com.chattriggers.ctjs.minecraft.libs.renderer.Renderer
import com.chattriggers.ctjs.minecraft.objects.gui.GuiHandler
import com.chattriggers.ctjs.minecraft.wrappers.Client
import net.minecraft.block.Block
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.GuiTextField
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import net.minecraft.world.World
import java.io.File

class BlockCodeBlock(materialIn: Material) : Block(materialIn), ITileEntityProvider {
    init {
        unlocalizedName = "code_block"
    }

    override fun onBlockActivated(worldIn: World, pos: BlockPos?, state: IBlockState?, playerIn: EntityPlayer?, side: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if (worldIn.isRemote) return true

        val tileEntity = worldIn.getTileEntity(pos)
        when (tileEntity) {
            is TileEntityCodeBlock -> GuiHandler.openGui(CodeBlockGui(tileEntity))
            else -> ModuleManager.generalConsole.printStackTrace(IllegalArgumentException("TileEntity for code block is not TileEntityCodeBlock"))
        }

        return false
    }

    override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity {
        return TileEntityCodeBlock()
    }
}

class TileEntityCodeBlock: TileEntity() {
    var file = ""

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)

        val entryList = compound.getTag("code")
        when (entryList) {
            is NBTTagCompound -> {
                file = entryList.getString("file")
                ModuleManager.generalConsole.out.println("file: $file")
            }
            else -> ModuleManager.generalConsole.printStackTrace(IllegalArgumentException("NBTBase in code block is not a NBTTagCompound"))
        }
    }

    override fun writeToNBT(compound: NBTTagCompound) {
        super.writeToNBT(compound)

        val entryCompound = NBTTagCompound()
        entryCompound.setString("file", file)

        compound.setTag("code", entryCompound)
        ModuleManager.generalConsole.out.println(compound)
    }
}

class CodeBlockGui(private val tileEntity: TileEntityCodeBlock) : GuiScreen() {
    private val baseFolder = "./config/ChatTriggers/CodeBlocks/"
    private val textField = GuiTextField(
            0, Renderer.getFontRenderer(),
            6 + Renderer.getStringWidth(baseFolder), 4,
            0, 10)

    private var systemTime = Client.getSystemTime()
    private var originalFile: String
    private var isFile: Boolean?
    private var code = ""

    init {
        textField.maxStringLength = 1000
        textField.text = tileEntity.file
        originalFile = tileEntity.file
        if (!originalFile.isFile()) originalFile = ""
        isFile = when {
            textField.text.isFile() -> true
            textField.text.isFolder() -> null
            else -> false
        }

        if (isFile == true) {
            code = FileLib.read(baseFolder + tileEntity.file)!!
        }
    }

    override fun onGuiClosed() {
        super.onGuiClosed()

        textField.text = ChatLib.removeFormatting(textField.text)
        if (textField.text == "") {
            resetFile("No file set!")
        } else if (!textField.text.isFile()) {
            resetFile("${textField.text} is not a file!")
        } else {
            ChatLib.chat("File set to ${textField.text}")
        }
    }

    private fun resetFile(output: String) {
        ChatLib.chat(output)
        tileEntity.file = originalFile
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, button: Int) {
        super.mouseClicked(mouseX, mouseY, button)
        textField.mouseClicked(mouseX, mouseY, 0)
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        super.keyTyped(typedChar, keyCode)
        textField.text = textField.text
        textField.textboxKeyTyped(typedChar, keyCode)

        when {
            textField.text.isFile() -> {
                tileEntity.file = textField.text
                textField.text = textField.text
                isFile = true
            }
            textField.text.isFolder() -> {
                textField.text = textField.text
                isFile = null
            }
            else -> {
                textField.text = textField.text
                isFile = false
            }
        }
    }

    private fun String.isFile(): Boolean {
        val file = File(baseFolder + this)
        return file.exists() && file.isFile
    }

    private fun String.isFolder(): Boolean {
        val file = File(baseFolder + this)
        return file.exists() && file.isDirectory
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.drawScreen(mouseX, mouseY, partialTicks)

        updateTextField()

        GlStateManager.pushMatrix()

        Renderer.drawRect(0xaa000000.toInt(), 0f, 0f, Renderer.screen.getWidth().toFloat(), Renderer.screen.getHeight().toFloat())
        when (isFile) {
            true -> Renderer.drawString(ChatLib.addColor("&a$baseFolder"), 5f, 5f)
            null -> Renderer.drawString(ChatLib.addColor("&e$baseFolder"), 5f, 5f)
            false -> Renderer.drawString(ChatLib.addColor("&c$baseFolder"), 5f, 5f)
        }

        textField.width = Renderer.screen.getWidth() - Renderer.getStringWidth(baseFolder) - 10
        textField.drawTextBox()

        Renderer.drawString(code, 5f, 20f)

        GlStateManager.popMatrix()
    }

    private fun updateTextField() {
        while (systemTime < Client.getSystemTime() + 50) {
            systemTime += 50
            textField.updateCursorCounter()
        }
    }
}