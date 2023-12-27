package io.redspace.ironsspellbooks.entity.mobs.goals;

import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import net.minecraft.world.InteractionHand;

import java.util.List;

public class WarlockAttackGoal extends WizardAttackGoal {

    protected float meleeRange;
    protected boolean wantsToMelee;
    protected int meleeTime;
    protected int meleeTimeDelay;
    protected float meleeBias;

    public WarlockAttackGoal(AbstractSpellCastingMob abstractSpellCastingMob, double pSpeedModifier, int minAttackInterval, int maxAttackInterval, float meleeRange) {
        super(abstractSpellCastingMob, pSpeedModifier, minAttackInterval, maxAttackInterval);
        this.meleeRange = meleeRange;
        meleeTimeDelay = abstractSpellCastingMob.getRandom().nextIntBetweenInclusive(80, 200);
        meleeBias = .5f;
        this.allowFleeing = false;
    }

    @Override
    public void tick() {
        super.tick();
        if (++meleeTime > meleeTimeDelay) {
            meleeTime = 0;
            wantsToMelee = mob.getRandom().nextFloat() <= meleeBias;
            meleeTimeDelay = mob.getRandom().nextIntBetweenInclusive(60, 120);
        }
    }

    @Override
    protected void doMovement(double distanceSquared) {
        if (!wantsToMelee) {
            super.doMovement(distanceSquared);
            return;
        }
        if (target.isDeadOrDying()) {
            this.mob.getNavigation().stop();
        } else {
            float strafeBackwards = 0;

            if (distanceSquared > meleeRange * meleeRange) {
                if (isFlying)
                    this.mob.getMoveControl().setWantedPosition(target.getX(), target.getY(), target.getZ(), this.speedModifier * 1.3f);
                else
                    this.mob.getNavigation().moveTo(this.target, this.speedModifier * 1.3f);
                strafeBackwards = 0f;
            } else {
                strafeBackwards = (float) (-speedModifier * .25f);
            }
            //we do a little strafing
            if (++strafeTime > 25) {
                if (mob.getRandom().nextDouble() < .1) {
                    strafingClockwise = !strafingClockwise;
                    strafeTime = 0;
                }
            }

            float strafeDir = strafingClockwise ? 1f : -1f;
            mob.getMoveControl().strafe(strafeBackwards, (float) speedModifier * strafeDir);
        }
    }

    @Override
    protected void handleAttackLogic(double distanceSquared) {
        if (!wantsToMelee || distanceSquared > meleeRange * meleeRange || mob.isCasting()) {
            super.handleAttackLogic(distanceSquared);
        } else if (--this.attackTime == 0) {
            this.mob.swing(InteractionHand.MAIN_HAND);
            doMeleeAction();
        }

    }

    protected void doMeleeAction() {
        double distanceSquared = this.mob.distanceToSqr(this.target.getX(), this.target.getY(), this.target.getZ());
        this.mob.doHurtTarget(target);
        resetAttackTimer(distanceSquared);
    }

    public WarlockAttackGoal setMeleeBias(float meleeBias) {
        this.meleeBias = meleeBias;
        return this;
    }

    @Override
    public WarlockAttackGoal setSpells(List<AbstractSpell> attackSpells, List<AbstractSpell> defenseSpells, List<AbstractSpell> movementSpells, List<AbstractSpell> supportSpells) {
        return (WarlockAttackGoal) super.setSpells(attackSpells, defenseSpells, movementSpells, supportSpells);
    }

    @Override
    public WarlockAttackGoal setSpellQuality(float minSpellQuality, float maxSpellQuality) {
        return (WarlockAttackGoal) super.setSpellQuality(minSpellQuality, maxSpellQuality);
    }

    @Override
    public WarlockAttackGoal setSingleUseSpell(AbstractSpell spellType, int minDelay, int maxDelay, int minLevel, int maxLevel) {
        return (WarlockAttackGoal) super.setSingleUseSpell(spellType, minDelay, maxDelay, minLevel, maxLevel);
    }

    @Override
    public WarlockAttackGoal setIsFlying() {
        return (WarlockAttackGoal) super.setIsFlying();
    }
}
