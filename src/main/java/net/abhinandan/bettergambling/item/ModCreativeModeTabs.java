package net.abhinandan.bettergambling.item;

import net.abhinandan.bettergambling.BetterGambling;
import net.abhinandan.bettergambling.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB = DeferredRegister.create(
            Registries.CREATIVE_MODE_TAB, BetterGambling.MOD_ID
    );

    public static final Supplier<CreativeModeTab> GACHA_TAB = CREATIVE_MODE_TAB.register(
            "gacha_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.CELESTIA_COIN.get()))
                    .title(Component.translatable("creativetab.bettergambling.gacha_items"))
                    .displayItems(((itemDisplayParameters, output) -> {
                        output.accept(ModItems.CELESTIA_COIN);
                        output.accept(ModBlocks.WHEEL_BLOCK);
                    }))
                    .build()
    );

    public static void register(@NotNull IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
