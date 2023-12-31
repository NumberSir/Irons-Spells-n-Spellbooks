package io.redspace.ironsspellbooks.player;

import com.google.common.eventbus.Subscribe;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ServerEvents {

    @SubscribeEvent
    public static void onLivingDamageEvent(LivingDamageEvent livingDamageEvent) {
        DamageTracker.INSTANCE.handle(livingDamageEvent);
    }
}
