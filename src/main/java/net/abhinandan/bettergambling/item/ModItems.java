package net.abhinandan.bettergambling.item;

import net.abhinandan.bettergambling.BetterGambling;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(BetterGambling.MOD_ID);

    public static final DeferredItem<Item> CELESTIA_COIN = ITEMS.register(
            "celestia_coin",
            () -> new Item(new Item.Properties().fireResistant())
    );

    public static void register(@NotNull IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
