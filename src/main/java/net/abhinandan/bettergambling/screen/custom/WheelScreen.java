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
            "textures/gui/wheel/wheel.png"
    );
    private static final ResourceLocation BORDER_TEXTURE = ResourceLocation.fromNamespaceAndPath(
            BetterGambling.MOD_ID,
            "textures/gui/wheel/wheel_border.png"
    );
    private static final Component[] rarity = {
            Component.literal("COMMON"),
            Component.literal("UNCOMMON"),
            Component.literal("RARE"),
            Component.literal("EPIC"),
            Component.literal("OMEGA")
    };
    private static final int[] colors = {
            0xFF808080, 0xFF328BEF, 0xFFDFD222, 0xFFB736F4, 0xFF16D318
    };

    public WheelScreen(@NotNull WheelMenu menu, @NotNull Inventory playerInventory, @NotNull Component title) {
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

        guiGraphics.blit(BORDER_TEXTURE, x + 24, y + 22, 0, 0, 117, 104, 117, 104);

        renderText(guiGraphics);
    }

    private void renderText(@NotNull GuiGraphics guiGraphics) {
        int index = menu.getDisplayText();
        if (index != -1) {
            guiGraphics.pose().pushPose();

            int screenWidth = this.width;
            int screenHeight = this.height;

            int colorTop = 0x00000000;
            int colorMiddle = 0xFF000000;
            int colorBottom = 0x00000000;
            guiGraphics.fillGradient(0, 0, screenWidth, screenHeight / 2, colorTop, colorMiddle);
            guiGraphics.fillGradient(0, screenHeight / 2, screenWidth, screenHeight, colorMiddle, colorBottom);

            guiGraphics.pose().translate(screenWidth / 2f, screenHeight / 2f, 0);
            float scale = 4f;
            guiGraphics.pose().scale(scale, scale, 1f);

            Component text = rarity[index];
            int textWidth = this.font.width(text);
            int textHeight = this.font.lineHeight;

            guiGraphics.drawString(this.font, text, -textWidth / 2, -textHeight / 2, colors[index], false);

            guiGraphics.pose().popPose();
        }
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0xFF3F3F40, false);
    }

    private void renderWheel(@NotNull GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.pose().pushPose();

        guiGraphics.pose().translate(x + 50 + 26, y + 50 + 24, 0);
        guiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(menu.getRotationAngle()));
        guiGraphics.pose().translate(-50, -50, 0);
        guiGraphics.blit(WHEEL_TEXTURE, 0, 0, 0, 0, 100, 100, 100, 100);

        guiGraphics.pose().popPose();
    }
}
