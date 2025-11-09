package net.abhinandan.bettergambling.block.custom;

import com.mojang.serialization.MapCodec;
import net.abhinandan.bettergambling.block.ModBlocks;
import net.abhinandan.bettergambling.block.entity.WheelBlockEntity;
import net.abhinandan.bettergambling.util.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WheelBlock extends BaseEntityBlock {
    public static final MapCodec<WheelBlock> CODEC = simpleCodec(WheelBlock::new);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public WheelBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new WheelBlockEntity(blockPos, blockState);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                                        @NotNull Player player, @NotNull BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof WheelBlockEntity wheelBlockEntity && !level.isClientSide()) {
            ((ServerPlayer) player).openMenu(new SimpleMenuProvider(wheelBlockEntity, Component.literal("Wheel")), pos);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        LevelReader level = context.getLevel();

        if (!level.isEmptyBlock(pos.above())) {
            return null;
        }

        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected void onRemove(BlockState state, @NotNull Level level, @NotNull BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.getBlock() != newState.getBlock()) {
            if (level.getBlockEntity(pos) instanceof WheelBlockEntity wheelBlockEntity) {
                wheelBlockEntity.drops();
                level.updateNeighbourForOutputSignal(pos, this);

                BlockPos topBlockPos = pos.above();
                BlockState topBlockState = level.getBlockState(topBlockPos);

                if (topBlockState.is(ModBlocks.WHEEL_TOP_BLOCK.get())) {
                    level.destroyBlock(topBlockPos, false);
                }
            }
        }

        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected void onPlace(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState oldState, boolean movedByPiston) {
        if (!level.isClientSide()) {
            BlockPos topBlockPos = pos.above();
            Block topBlock = ModBlocks.WHEEL_TOP_BLOCK.get();

            if (level.isEmptyBlock(topBlockPos)) {
                BlockState topBlockState = topBlock.defaultBlockState();
                level.setBlock(topBlockPos, topBlockState, Block.UPDATE_ALL);
            }
        }

        super.onPlace(state, level, pos, oldState, movedByPiston);
    }

    @Override
    protected void createBlockStateDefinition(@NotNull StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
