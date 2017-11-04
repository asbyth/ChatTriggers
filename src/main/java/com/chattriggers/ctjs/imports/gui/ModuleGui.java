package com.chattriggers.ctjs.imports.gui;

import com.chattriggers.ctjs.imports.Module;
import com.chattriggers.ctjs.libs.ChatLib;
import com.chattriggers.ctjs.libs.MathLib;
import com.chattriggers.ctjs.libs.RenderLib;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class ModuleGui extends GuiScreen {
    private Module module;

    private int width;
    private int infoHeight;

    private int scrolled;
    private int maxScroll;

    private String name;
    private ArrayList<String> description;

    public ModuleGui(Module module) {
        this.module = module;

        updateScaling();

        scrolled = 0;

        name = ChatLib.addColor(this.module.getMetadata().getName() == null
                ? this.module.getName()
                : this.module.getMetadata().getName());
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        updateScaling();

        drawBackground(0);

        // scrollbar
        int scrollHeight = RenderLib.getRenderHeight() - this.maxScroll;
        if (scrollHeight < 20) scrollHeight = 20;
        if (scrollHeight < RenderLib.getRenderHeight()) {
            int scrollY = (int) MathLib.map(this.scrolled, 0, this.maxScroll, 10, RenderLib.getRenderHeight() - scrollHeight - 10);
            RenderLib.drawRectangle(
                    0xa0000000,
                    RenderLib.getRenderWidth() - 5,
                    scrollY,
                    5,
                    scrollHeight
            );
        }

        // info
        RenderLib.drawRectangle(
                0x80000000,
                10,
                10 - scrolled,
                width,
                infoHeight
        );

        RenderLib.drawStringWithShadow(
                name,
                12,
                12 - scrolled,
                0xffffffff
        );

        for (int i = 0; i < description.size(); i++) {
            RenderLib.drawStringWithShadow(
                    ChatLib.addColor(description.get(i)),
                    12,
                    25 + i * 9 - scrolled,
                    0xffffffff
            );
        }

        // code
        RenderLib.drawRectangle(
                0x80000000,
                10,
                infoHeight + 20 - scrolled,
                width,
                module.getLines().size() * 9
        );

        int i = 0;
        for (String line : module.getLines()) {
            RenderLib.drawStringWithShadow(
                    line.replace("\u0009", "     "),
                    10,
                    i * 9 + infoHeight + 20 - scrolled,
                    0xffffffff
            );
            i++;
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();

        int i = Mouse.getEventDWheel();
        if (i == 0) return;

        if (i > 1) i = 1;
        if (i < -1) i = -1;

        if (isShiftKeyDown()) i *= 10;
        else i *= 20;

        this.scrolled -= i;
        if (this.scrolled > this.maxScroll)
            this.scrolled = this.maxScroll;
        if (this.scrolled < 0)
            this.scrolled = 0;
    }

    private void updateScaling() {
        width = RenderLib.getRenderWidth() - 20;

        String preDescription = module.getMetadata().getDescription() == null
                ? "No description provided"
                : module.getMetadata().getDescription();
        description = RenderLib.lineWrap(new ArrayList<>(Arrays.asList(preDescription.split("\n"))), width - 5, 100);

        infoHeight = description.size()*9 + 20;

        maxScroll = this.module.getLines().size()*9 + infoHeight - RenderLib.getRenderHeight() + 30;
    }
}
