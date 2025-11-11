package net.abhinandan.bettergambling.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WheelTopBlock extends Block {
    public WheelTopBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState newState, boolean movedByPiston) {
        if (state.getBlock() != newState.getBlock()) {
            BlockPos bottomBlockPos = pos.below();
            level.destroyBlock(bottomBlockPos, true);
        }

        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level,
                                                        @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            BlockPos bottomBlockPos = pos.below();
            BlockState bottomBlockState = level.getBlockState(bottomBlockPos);
            Block bottomBlock = bottomBlockState.getBlock();

            if (bottomBlock instanceof WheelBlock wheelBlock) {
                return wheelBlock.useWithoutItem(bottomBlockState, level, bottomBlockPos, player, hitResult);
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    protected @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public @Nullable PushReaction getPistonPushReaction(@NotNull BlockState state) {
        return PushReaction.DESTROY;
    }
}
