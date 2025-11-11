package net.abhinandan.bettergambling.block;

import net.abhinandan.bettergambling.BetterGambling;
import net.abhinandan.bettergambling.block.entity.WheelBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPE = DeferredRegister.create(
            BuiltInRegistries.BLOCK_ENTITY_TYPE, BetterGambling.MOD_ID
    );

    public static final Supplier<BlockEntityType<WheelBlockEntity>> WHEEL_BLOCK_ENTITY = BLOCK_ENTITY_TYPE.register(
            "wheel_block_entity",
            () -> BlockEntityType.Builder.of(
                    WheelBlockEntity::new, ModBlocks.WHEEL_BLOCK.get()
            ).build(null)
    );

    public static void register(@NotNull IEventBus eventBus) {
        BLOCK_ENTITY_TYPE.register(eventBus);
    }
}
