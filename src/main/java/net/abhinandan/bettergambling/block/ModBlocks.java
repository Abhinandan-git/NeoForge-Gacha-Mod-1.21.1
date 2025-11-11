package net.abhinandan.bettergambling.block;

import net.abhinandan.bettergambling.BetterGambling;
import net.abhinandan.bettergambling.block.custom.WheelBlock;
import net.abhinandan.bettergambling.block.custom.WheelTopBlock;
import net.abhinandan.bettergambling.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(BetterGambling.MOD_ID);

    public static final DeferredBlock<Block> WHEEL_BLOCK = registerBlock(
            "wheel_block",
            () -> new WheelBlock(
                    BlockBehaviour.Properties.of().strength(1f).noOcclusion()
            )
    );
    public static final DeferredBlock<Block> WHEEL_TOP_BLOCK = registerBlock(
            "wheel_top_block",
            () -> new WheelTopBlock(
                    BlockBehaviour.Properties.of().strength(1f).noOcclusion()
            )
    );

    private static <T extends Block> @NotNull DeferredBlock<T> registerBlock(@NotNull String name, @NotNull Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(@NotNull String name, @NotNull DeferredBlock<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(@NotNull IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
