package net.abhinandan.bettergambling;

import java.util.List;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.IntValue COMMON_WEIGHT = BUILDER
            .comment("Weight for Common tier")
            .defineInRange("common_weight", 40, 20, 60);
    private static final ModConfigSpec.IntValue UNCOMMON_WEIGHT = BUILDER
            .comment("Weight for Uncommon tier")
            .defineInRange("uncommon_weight", 30, 15, 50);
    private static final ModConfigSpec.IntValue RARE_WEIGHT = BUILDER
            .comment("Weight for Rare tier")
            .defineInRange("rare_weight", 17, 10, 35);
    private static final ModConfigSpec.IntValue EPIC_WEIGHT = BUILDER
            .comment("Weight for epic tier")
            .defineInRange("epic_weight", 11, 5, 20);
    private static final ModConfigSpec.IntValue OMEGA_WEIGHT = BUILDER
            .comment("Weight for Omega tier")
            .defineInRange("omega_weight", 2, 1, 10);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> COMMON_ITEMS = BUILDER
            .comment("Pool of all common items.")
            .defineList("common", List.of("minecraft:stick"), () -> "", Config::validateItemName);
    public static final ModConfigSpec.ConfigValue<List<? extends String>> UNCOMMON_ITEMS = BUILDER
            .comment("Pool of all uncommon items.")
            .defineList("uncommon", List.of("minecraft:iron_ingot"), () -> "", Config::validateItemName);
    public static final ModConfigSpec.ConfigValue<List<? extends String>> RARE_ITEMS = BUILDER
            .comment("Pool of all rare items.")
            .defineList("rare", List.of("minecraft:gold_ingot"), () -> "", Config::validateItemName);
    public static final ModConfigSpec.ConfigValue<List<? extends String>> EPIC_ITEMS = BUILDER
            .comment("Pool of all epic items.")
            .defineList("epic", List.of("minecraft:diamond"), () -> "", Config::validateItemName);
    public static final ModConfigSpec.ConfigValue<List<? extends String>> OMEGA_ITEMS = BUILDER
            .comment("Pool of all omega items.")
            .defineList("omega", List.of("minecraft:netherite_ingot"), () -> "", Config::validateItemName);

    static final ModConfigSpec SPEC = BUILDER.build();

    public final List<Integer> WEIGHTS = List.of(
            COMMON_WEIGHT.get(), UNCOMMON_WEIGHT.get(), RARE_WEIGHT.get(), EPIC_WEIGHT.get(), OMEGA_WEIGHT.get()
    );

    private static boolean validateItemName(final Object obj) {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
    }
}
