package net.abhinandan.bettergambling.block.entity;

import net.abhinandan.bettergambling.BetterGambling;
import net.abhinandan.bettergambling.Config;
import net.abhinandan.bettergambling.block.ModBlockEntities;
import net.abhinandan.bettergambling.item.ModItems;
import net.abhinandan.bettergambling.screen.custom.WheelMenu;
import net.abhinandan.bettergambling.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
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
    private int spinCooldown = 60;
    private static final String[] REWARD_ORDER = { "COMMON", "RARE", "EPIC", "COMMON", "OMEGA", "RARE", "UNCOMMON" };

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

    public void tick(@NotNull Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;

        // 0 = idle, 1 = spinning, 2 = stopped
        if (isSpinning == 0) {
            if (isInputCorrect()) {
                isSpinning = 1;
                spinSpeed = 30f + level.random.nextFloat() * 10f;
                consumeCoin();
            }
            rotationAngle = (rotationAngle + 1) % 360;
        } else if (isSpinning == 1) {
            rotationAngle += (int) spinSpeed;
            spinSpeed *= 0.95f;

            if (spinSpeed < 1f) {
                giveReward();
                spinCooldown = 60;
                isSpinning = 2;
            }
        } else if (isSpinning == 2) {
            if (spinCooldown > 0) {
                spinCooldown--;
            } else {
                isSpinning = 0;
            }
        }

        if (shouldPlaySound()) {
            level.playSound(null, pos, ModSounds.WHEEL_SPIN.get(), SoundSource.BLOCKS);
        }

        setChanged(level, pos, state);
        level.sendBlockUpdated(pos, state, state, 3);
    }

    private boolean shouldPlaySound() {
        List<Integer> weights = new Config().WEIGHTS;
        int totalWeight = weights.stream().mapToInt(Integer::intValue).sum();
        float degreesPerWeight = 360f / totalWeight;

        float cumulative = 0f;
        for (int weight : weights) {
            cumulative += weight * degreesPerWeight;

            // How close is rotationAngle (mod 360) to this boundary?
            float diff = Math.abs((rotationAngle % 360f) - cumulative);
            if (diff < 2f || Math.abs(diff - 360f) < 2f) {
                return true;
            }
        }

        return false;
    }

    private void consumeCoin() {
        inventory.extractItem(0, 1, false);
    }

    private void giveReward() {
        List<? extends String> items = getItemList();
        int itemSize = items.size();

        if (itemSize > 0) {
            assert level != null;
            String itemId = items.get(level.random.nextInt(itemSize));

            ResourceLocation rl = ResourceLocation.parse(itemId);

            Item item = BuiltInRegistries.ITEM.get(rl);

            if (item != Items.AIR) {
                ItemStack itemToDrop = new ItemStack(item, 1);
                Containers.dropItemStack(level, worldPosition.getX() + 0.5, worldPosition.getY() + 1.0, worldPosition.getZ() + 0.5, itemToDrop);

                level.playSound(null, getBlockPos(), SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS);
            } else {
                BetterGambling.LOGGER.warn("Invalid item ID in config: {}", itemId);
            }
        }
    }

    private List<? extends String> getItemList() {
        return switch (REWARD_ORDER[getIndex()]) {
            case "COMMON" -> Config.COMMON_ITEMS.get();
            case "UNCOMMON" -> Config.UNCOMMON_ITEMS.get();
            case "RARE" -> Config.RARE_ITEMS.get();
            case "EPIC" -> Config.EPIC_ITEMS.get();
            case "OMEGA" -> Config.OMEGA_ITEMS.get();
            default -> List.of("minecraft:air");
        };
    }

    private int getIndex() {
        List<Integer> weights = new Config().WEIGHTS;

        rotationAngle = ((rotationAngle % 360) + 360) % 360;

        int totalWeight = weights.stream().mapToInt(Integer::intValue).sum();
        float degreesPerWeight = 360f / totalWeight;

        int accumulated = 0;
        for (int i = 0; i < weights.size(); i++) {
            int next = (int) (accumulated + (weights.get(i) * degreesPerWeight));
            if (rotationAngle >= accumulated && rotationAngle < next) {
                return i;
            }
            accumulated = next;
        }

        return 0;
    }

    private boolean isInputCorrect() {
        return inventory.getStackInSlot(0).is(ModItems.CELESTIA_COIN);
    }
}
