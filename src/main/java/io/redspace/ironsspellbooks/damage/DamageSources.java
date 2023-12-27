package io.redspace.ironsspellbooks.damage;

import io.redspace.ironsspellbooks.api.entity.NoKnockbackProjectile;
import io.redspace.ironsspellbooks.api.events.SpellDamageEvent;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.entity.mobs.MagicSummon;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.HashMap;

//https://github.com/cleannrooster/Spellblade-1.19.2/search?q=MobEffect
//https://github.com/LittleEzra/Augment-1.19.2/blob/334dc95462a3e6b25e6f73d3d909d012d63be109/src/main/java/com/littleezra/augment/item/enchantment/RecoilCurseEnchantment.java
//DamageSource
//StatusEffect
//MobEffect: https://forge.gemwire.uk/wiki/Mob_Effects/1.18

@Mod.EventBusSubscriber
public class DamageSources {
    public static DamageSource get(Level level, ResourceKey<DamageType> damageType) {
        return level.damageSources().source(damageType);
    }

    public static Holder<DamageType> getHolderFromResource(Entity entity, ResourceKey<DamageType> damageTypeResourceKey) {
        var option = entity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolder(damageTypeResourceKey);
        if (option.isPresent()) {
            return option.get();
        } else {
            return entity.level().damageSources().genericKill().typeHolder();
        }
    }

    /**
     * Use new overload {@link DamageSources#applyDamage(Entity, float, DamageSource)}<br>You can also now utilize the damage source itself to apply lifesteal, fire time, and freeze time <br>
     */
    @Deprecated(forRemoval = true)
    public static boolean applyDamage(Entity target, float baseAmount, DamageSource damageSource, @Nullable SchoolType damageSchool) {
        if (target instanceof LivingEntity livingTarget) {
            //Todo: should this be handled in damage event? (would by where enchantments and stuff also get put)
            float adjustedDamage = baseAmount * getResist(livingTarget, damageSchool);
            MagicSummon fromSummon = damageSource.getDirectEntity() instanceof MagicSummon summon ? summon : damageSource.getEntity() instanceof MagicSummon summon ? summon : null;
            if (fromSummon != null) {
                if (fromSummon.getSummoner() != null) {
                    adjustedDamage *= (float) fromSummon.getSummoner().getAttributeValue(AttributeRegistry.SUMMON_DAMAGE.get());
                }
            } else if (damageSource.getDirectEntity() instanceof NoKnockbackProjectile) {
                ignoreNextKnockback(livingTarget);
            }
            if (damageSource.getEntity() instanceof LivingEntity livingAttacker) {
                if (isFriendlyFireBetween(livingAttacker, livingTarget)) {
                    return false;
                }
                livingAttacker.setLastHurtMob(target);
            }
            var flag = livingTarget.hurt(damageSource, adjustedDamage);
            if (fromSummon instanceof LivingEntity livingSummon) {
                livingTarget.setLastHurtByMob(livingSummon);
            }
            return flag;
        } else {
            return target.hurt(damageSource, baseAmount);
        }
    }

    public static boolean applyDamage(Entity target, float baseAmount, DamageSource damageSource) {
        if (target instanceof LivingEntity livingTarget && damageSource instanceof SpellDamageSource spellDamageSource) {
            var e = new SpellDamageEvent(livingTarget, baseAmount, spellDamageSource);
            if (MinecraftForge.EVENT_BUS.post(e)) {
                return false;
            }
            baseAmount = e.getAmount();
            float adjustedDamage = baseAmount * getResist(livingTarget, spellDamageSource.spell.getSchoolType());
            MagicSummon fromSummon = damageSource.getDirectEntity() instanceof MagicSummon summon ? summon : damageSource.getEntity() instanceof MagicSummon summon ? summon : null;
            if (fromSummon != null) {
                if (fromSummon.getSummoner() != null) {
                    adjustedDamage *= (float) fromSummon.getSummoner().getAttributeValue(AttributeRegistry.SUMMON_DAMAGE.get());
                }
            } else if (damageSource.getDirectEntity() instanceof NoKnockbackProjectile) {
                ignoreNextKnockback(livingTarget);
            }
            if (damageSource.getEntity() instanceof LivingEntity livingAttacker) {
                if (isFriendlyFireBetween(livingAttacker, livingTarget)) {
                    return false;
                }
                livingAttacker.setLastHurtMob(target);
            }
            var flag = livingTarget.hurt(damageSource, adjustedDamage);
            if (fromSummon instanceof LivingEntity livingSummon) {
                livingTarget.setLastHurtByMob(livingSummon);
            }
            return flag;
        } else {
            return target.hurt(damageSource, baseAmount);
        }
    }

    //I can't tell if this is genius or incredibly stupid
    private static final HashMap<LivingEntity, Integer> knockbackImmunes = new HashMap<>();

    public static void ignoreNextKnockback(LivingEntity livingEntity) {
        if (!livingEntity.level.isClientSide)
            knockbackImmunes.put(livingEntity, livingEntity.tickCount);
    }

    @SubscribeEvent
    public static void cancelKnockback(LivingKnockBackEvent event) {
        //IronsSpellbooks.LOGGER.debug("DamageSources.cancelKnockback {}", event.getEntity().getName().getString());
        if (knockbackImmunes.containsKey(event.getEntity())) {
            var entity = event.getEntity();
            if (entity.tickCount - knockbackImmunes.get(entity) <= 1) {
                event.setCanceled(true);
            }
            knockbackImmunes.remove(entity);
        }
    }

    @SubscribeEvent
    public static void postHitEffects(LivingDamageEvent event) {
        if (event.getSource() instanceof SpellDamageSource spellDamageSource && spellDamageSource.hasPostHitEffects()) {
            float actualDamage = event.getAmount();
            var target = event.getEntity();
            var attacker = event.getSource().getEntity();
            if (attacker instanceof LivingEntity livingAttacker) {
                if (spellDamageSource.getLifestealPercent() > 0) {
                    livingAttacker.heal(spellDamageSource.getLifestealPercent() * actualDamage);
                }
            }
            if (spellDamageSource.getFreezeTicks() > 0 && target.canFreeze()) {
                //Freeze ticks count down by 2, so we * 2 so the spell damages source can be dumb
                target.setTicksFrozen(target.getTicksFrozen() + spellDamageSource.getFreezeTicks() * 2);
            }
            if (spellDamageSource.getFireTime() > 0) {
                target.setSecondsOnFire(spellDamageSource.getFireTime());
            }
        }

    }

    public static boolean isFriendlyFireBetween(Entity attacker, Entity target) {
        if (attacker == null || target == null)
            return false;
        var team = attacker.getTeam();
        if (team != null) {
            return team.isAlliedTo(target.getTeam()) && !team.isAllowFriendlyFire();
        }
        //We already manually checked for teams, so this will only return true for any overrides (such as summons)
        return attacker.isAlliedTo(target);
    }

    @Deprecated(since = "MC_1.20", forRemoval = true)
    public static DamageSource directDamageSource(DamageSource source, Entity attacker) {
        return new DamageSource(source.typeHolder(), attacker);
        //return new EntityDamageSource(source.getMsgId(), attacker);
    }

    @Deprecated(since = "MC_1.20", forRemoval = true)
    public static DamageSource indirectDamageSource(DamageSource source, Entity projectile, @Nullable Entity attacker) {
        return new DamageSource(source.typeHolder(), attacker, projectile);
    }

    /**
     * Returns the resistance multiplier of the entity. (If they are resistant, the value is < 1)
     */
    public static float getResist(LivingEntity entity, SchoolType damageSchool) {
        if (damageSchool == null)
            return 1;
        else
            return 2 - (float) Utils.softCapFormula(damageSchool.getResistanceFor(entity));
    }
}
