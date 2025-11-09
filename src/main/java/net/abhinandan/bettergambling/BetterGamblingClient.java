package net.abhinandan.bettergambling;

import net.abhinandan.bettergambling.screen.ModMenu;
import net.abhinandan.bettergambling.screen.custom.WheelScreen;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.jetbrains.annotations.NotNull;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = BetterGambling.MOD_ID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = BetterGambling.MOD_ID, value = Dist.CLIENT)
public class BetterGamblingClient {
    public BetterGamblingClient(@NotNull ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
        BetterGambling.LOGGER.info("HELLO FROM CLIENT SETUP");
        BetterGambling.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }

    @SubscribeEvent
    public static void registerScreens(@NotNull RegisterMenuScreensEvent event) {
        event.register(ModMenu.WHEEL_MENU.get(), WheelScreen::new);
    }
}
