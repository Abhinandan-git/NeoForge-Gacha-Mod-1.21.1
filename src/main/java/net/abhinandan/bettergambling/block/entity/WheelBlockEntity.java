package net.abhinandan.bettergambling.block.entity;

import net.abhinandan.bettergambling.block.ModBlockEntities;
import net.abhinandan.bettergambling.item.ModItems;
import net.abhinandan.bettergambling.screen.custom.WheelMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class WheelBlockEntity extends BlockEntity implements MenuProvider {
    public final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        protected int getStackLimit(int slot, @NotNull ItemStack stack) {
            return 1;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            assert level != null;
            if (!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    private final ContainerData data;
    private int rotationAngle = new Random().nextInt() % 360;
    private int isSpinning = 0;
    private float spinSpeed = 1f;

    public WheelBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.WHEEL_BLOCK_ENTITY.get(), pos, blockState);

        data = new ContainerData() {
            @Override
            public int get(int i) {
                return switch (i) {
                    case 0 -> WheelBlockEntity.this.rotationAngle;
                    case 1 -> WheelBlockEntity.this.isSpinning;
                    default -> 0;
                };
            }

            @Override
            public void set(int i, int value) {
                switch (i) {
                    case 0 -> WheelBlockEntity.this.rotationAngle = value;
                    case 1 -> WheelBlockEntity.this.isSpinning = value;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    public void clearContents() {
        inventory.setStackInSlot(0, ItemStack.EMPTY);
    }

    public void drops() {
        SimpleContainer inv = new SimpleContainer(inventory.getSlots());

        for (int i = 0; i < inventory.getSlots(); i++) {
            inv.setItem(i, inventory.getStackInSlot(i));
        }

        assert this.level != null;
        Containers.dropContents(this.level, this.worldPosition, inv);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", inventory.serializeNBT(registries));
        tag.putInt("wheel.rotation_angle", rotationAngle);
        tag.putInt("wheel.is_spinning", isSpinning);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        inventory.deserializeNBT(registries, tag.getCompound("inventory"));
        rotationAngle = tag.getInt("wheel.rotation_angle");
        isSpinning = tag.getInt("wheel.is_spinning");
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.literal("Spin the Wheel");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, @NotNull Inventory inventory, @NotNull Player player) {
        return new WheelMenu(i, inventory, this, this.data);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        return saveWithoutMetadata(registries);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;

        // 0 = idle, 1 = spinning, 2 = stopped
        if (isSpinning == 0) {
            if (isInputCorrect()) {
                consumeCoin();
                isSpinning = 1;
                spinSpeed = 30f + level.random.nextFloat() * 10f;
            }
            rotationAngle = (rotationAngle + 1) % 360;
        } else if (isSpinning == 1) {
            rotationAngle += (int) spinSpeed;
            spinSpeed *= 0.95f;
            if (spinSpeed < 0.1f) {
                isSpinning = 2;
            }
        } else if (isSpinning == 2) {
            isSpinning = 0;
        }

        setChanged(level, pos, state);
        level.sendBlockUpdated(pos, state, state, 3);
    }

    private void consumeCoin() {
        inventory.extractItem(0, 32, false);
        assert this.level != null;
        Containers.dropContents(this.level, this.worldPosition, NonNullList.of(new ItemStack(ModItems.CELESTIA_COIN.get(), 2)));
    }

    private boolean isInputCorrect() {
        return inventory.getStackInSlot(0).is(ModItems.CELESTIA_COIN) && inventory.getStackInSlot(0).getCount() >= 32;
    }
}
