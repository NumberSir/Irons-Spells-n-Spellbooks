package io.redspace.ironsspellbooks.tetra.effects;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import se.mickelus.tetra.blocks.workbench.gui.WorkbenchStatsGui;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.gui.stats.StatsHelper;
import se.mickelus.tetra.gui.stats.bar.GuiStatBar;
import se.mickelus.tetra.gui.stats.getter.IStatGetter;
import se.mickelus.tetra.gui.stats.getter.LabelGetterBasic;
import se.mickelus.tetra.gui.stats.getter.StatGetterEffectLevel;
import se.mickelus.tetra.gui.stats.getter.TooltipGetterDecimal;
import se.mickelus.tetra.items.modular.ModularItem;
import se.mickelus.tetra.items.modular.impl.holo.gui.craft.HoloStatsGui;

public class FreezeTetraEffect {
    public static final ItemEffect freezeOnHit = ItemEffect.get(IronsSpellbooks.MODID + ":freeze");
    public static final String  freezeName = IronsSpellbooks.MODID + ".tetra_effect.freeze.name";
    public static final String  freezeTooltip = IronsSpellbooks.MODID + ".tetra_effect.freeze.tooltip";

    @OnlyIn(Dist.CLIENT)
    public static void init(){
        final IStatGetter effectStatGetter = new StatGetterEffectLevel(freezeOnHit, 1);
        final GuiStatBar effectBar = new GuiStatBar(0, 0, StatsHelper.barLength, freezeName, 0, 30, false, effectStatGetter, LabelGetterBasic.decimalLabel,
                new TooltipGetterDecimal(freezeTooltip, effectStatGetter));
        WorkbenchStatsGui.addBar(effectBar);
        HoloStatsGui.addBar(effectBar);
    }

    public static void handleFreezeEffect(LivingEntity attacker, LivingEntity target, ItemStack tetraStack) {
        int level = ((ModularItem) tetraStack.getItem()).getEffectLevel(tetraStack, freezeOnHit);
        if (level > 0) {
            if (target.canFreeze())
                target.setTicksFrozen(target.getTicksFrozen() + level * 20);
            IronsSpellbooks.LOGGER.debug("FreezeTetraEffect.level: {}", level);
        }
    }
}
