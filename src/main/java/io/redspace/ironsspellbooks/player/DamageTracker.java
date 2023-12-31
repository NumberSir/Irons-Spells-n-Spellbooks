package io.redspace.ironsspellbooks.player;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

import java.util.HashMap;
import java.util.UUID;

//https://fastutil.di.unimi.it/docs/it/unimi/dsi/fastutil/package-summary.html
public class DamageTracker {
    public static DamageTracker INSTANCE = new DamageTracker();

    private HashMap<UUID, HashMap<UUID, CounterWrapper>> damageData = new HashMap<>();

    public void handle(LivingDamageEvent livingDamageEvent) {
        if (livingDamageEvent.getSource().getEntity() instanceof Player player) {
            var playerData = damageData.computeIfAbsent(player.getUUID(), k -> new HashMap<>());
            var entityData = playerData.get(livingDamageEvent.getEntity().getUUID());

            if (entityData == null) {
                playerData.put(livingDamageEvent.getEntity().getUUID(), new CounterWrapper(livingDamageEvent.getAmount()));
            } else {
                entityData.value += livingDamageEvent.getAmount();
            }
        }
    }

    public void clear() {
        damageData = new HashMap<>();
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        damageData.forEach((playerID, entityData) -> {
            entityData.forEach((entityId, cw) -> {
                sb.append(String.format("%s,%s,%f\n", playerID, entityId, cw.value));
            });
        });
        return sb.toString();
    }

    public static class CounterWrapper {
        public double value;

        public CounterWrapper(double value) {
            this.value = value;
        }
    }
}
