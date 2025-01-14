package io.redspace.ironsspellbooks.player;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.capabilities.spell.SpellData;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class CommonPlayerEvents {
    public static void onPlayerRightClickItem(PlayerInteractEvent.RightClickItem event) {
        //IronsSpellbooks.LOGGER.debug("CommonPlayerEvents.onPlayerRightClickItem {}", event.getSide());
        var stack = event.getItemStack();

        if (Utils.canImbue(stack)) {
            var spellData = SpellData.getSpellData(stack);
            var result = Utils.onUseCastingHelper(event.getLevel(), event.getEntity(), event.getHand(), stack, spellData);

            if (result != null) {
                event.setCancellationResult(result.getResult());
                event.setCanceled(true);
            }
        }
    }

    public static void onUseItemStop(LivingEntityUseItemEvent.Stop event) {
        //IronsSpellbooks.LOGGER.debug("CommonPlayerEvents.onUseItemStop {} {}", event.getEntity().getLevel().isClientSide, event.getItem().getItem());
        var stack = event.getItem();
        if (Utils.canImbue(stack)) {
            var spell = SpellData.getSpellData(stack).getSpell();
            var entity = event.getEntity();

            if (spell != SpellRegistry.none()) {
                entity.stopUsingItem();
                Utils.releaseUsingHelper(entity, stack, event.getDuration());
            }
        }
    }
}
