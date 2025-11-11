package net.abhinandan.bettergambling.screen.custom;

import net.abhinandan.bettergambling.block.ModBlocks;
import net.abhinandan.bettergambling.block.entity.WheelBlockEntity;
import net.abhinandan.bettergambling.screen.ModMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class WheelMenu extends AbstractContainerMenu {
    public final WheelBlockEntity wheelBlockEntity;
    private final Level level;
    private final ContainerData data;

    public WheelMenu(int containerId, Inventory inventory, @NotNull FriendlyByteBuf extraData) {
        this(containerId, inventory, inventory.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(2));
    }

    public WheelMenu(int containerId, @NotNull Inventory inventory, BlockEntity blockEntity, ContainerData data) {
        super(ModMenu.WHEEL_MENU.get(), containerId);
        this.wheelBlockEntity = ((WheelBlockEntity) blockEntity);
        this.level = inventory.player.level();
        this.data = data;

        addPlayerHotbar(inventory);

        this.addSlot(new SlotItemHandler(this.wheelBlockEntity.inventory, 0, 152, 120));

        addDataSlots(data);
    }

    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT;

    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;
    private static final int TE_INVENTORY_SLOT_COUNT = 1;

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player playerIn, int pIndex) {
        Slot sourceSlot = slots.get(pIndex);
        if (!sourceSlot.hasItem()) return ItemStack.EMPTY;

        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // Only handle hotbar <-> tile inventory moves
        if (pIndex < VANILLA_SLOT_COUNT) {
            // From player hotbar → TE inventory
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX,
                    TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else if (pIndex < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            // From TE inventory → player hotbar
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX,
                    VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            System.out.println("Invalid slotIndex: " + pIndex);
            return ItemStack.EMPTY;
        }

        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(ContainerLevelAccess.create(level, wheelBlockEntity.getBlockPos()), player, ModBlocks.WHEEL_BLOCK.get());
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    public int getRotationAngle() {
        return data.get(0);
    }

    public int getDisplayText() {
        return data.get(1);
    }
}
