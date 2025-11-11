package net.abhinandan.bettergambling.screen;

import net.abhinandan.bettergambling.BetterGambling;
import net.abhinandan.bettergambling.screen.custom.WheelMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

public class ModMenu {
    public static final DeferredRegister<MenuType<?>> MENU = DeferredRegister.create(Registries.MENU, BetterGambling.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<WheelMenu>> WHEEL_MENU = registerMenu(WheelMenu::new);

    private static <T extends AbstractContainerMenu> @NotNull DeferredHolder<MenuType<?>, MenuType<T>> registerMenu(@NotNull IContainerFactory<T> factory) {
        return MENU.register("wheel_menu", () -> IMenuTypeExtension.create(factory));
    }

    public static void register(@NotNull IEventBus eventBus) {
        MENU.register(eventBus);
    }
}
