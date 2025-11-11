package net.abhinandan.bettergambling.sound;

import net.abhinandan.bettergambling.BetterGambling;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENT = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, BetterGambling.MOD_ID);

    public static final Supplier<SoundEvent> WHEEL_SPIN = registerSoundEvent();

    private static @NotNull Supplier<SoundEvent> registerSoundEvent() {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(BetterGambling.MOD_ID, "wheel_spin");
        return SOUND_EVENT.register("wheel_spin", () -> SoundEvent.createVariableRangeEvent(id));
    }

    public static void register(@NotNull IEventBus eventBus) {
        SOUND_EVENT.register(eventBus);
    }
}
