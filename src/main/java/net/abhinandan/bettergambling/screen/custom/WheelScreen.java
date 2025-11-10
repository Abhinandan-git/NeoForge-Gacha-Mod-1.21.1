package net.abhinandan.bettergambling.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import net.abhinandan.bettergambling.BetterGambling;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class WheelScreen extends AbstractContainerScreen<WheelMenu> {
    private static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(
            BetterGambling.MOD_ID,
            "textures/gui/wheel/wheel_bg_gui.png"
    );
    private static final ResourceLocation WHEEL_TEXTURE = ResourceLocation.fromNamespaceAndPath(
            BetterGambling.MOD_ID,
            "textures/gui/wheel/wheel_gui.png"
    );
    private static final ResourceLocation BORDER_TEXTURE = ResourceLocation.fromNamespaceAndPath(
            BetterGambling.MOD_ID,
            "textures/gui/wheel/wheel_border.png"
    );

    public WheelScreen(WheelMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);
        RenderSystem.setShaderTexture(0, WHEEL_TEXTURE);
        RenderSystem.setShaderTexture(0, BORDER_TEXTURE);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(GUI_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        renderWheel(guiGraphics, x, y);

        guiGraphics.blit(BORDER_TEXTURE, x + 24, y + 18, 0, 0, 117, 104, 117, 104);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, -12566464, false);
    }

    private void renderWheel(@NotNull GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x + 52 + 24, y + 52 + 18, 0);
        guiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(menu.getRotationAngle()));
        guiGraphics.pose().translate(-50, -50, 0);
        guiGraphics.blit(WHEEL_TEXTURE, 0, 0, 0, 0, 100, 100, 100, 100);
        guiGraphics.pose().popPose();
    }
}
