package net.abhinandan.bettergambling.util;

import net.abhinandan.bettergambling.BetterGambling;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> GACHA_BLOCKS = createTag();

        private static @NotNull TagKey<Block> createTag() {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(BetterGambling.MOD_ID, "gacha_blocks"));
        }
    }

    public static class Items {
        public static final TagKey<Item> GACHA_ITEMS = createTag();

        private static @NotNull TagKey<Item> createTag() {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(BetterGambling.MOD_ID, "gacha_items"));
        }
    }
}
