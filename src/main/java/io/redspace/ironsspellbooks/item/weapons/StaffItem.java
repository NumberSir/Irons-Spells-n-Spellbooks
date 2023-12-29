package io.redspace.ironsspellbooks.item.weapons;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import io.redspace.ironsspellbooks.item.CastingItem;
import io.redspace.ironsspellbooks.render.StaffArmPose;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.Map;
import java.util.function.Consumer;

public class StaffItem extends CastingItem {
    private final Multimap<Attribute, AttributeModifier> defaultModifiers;

    public StaffItem(Item.Properties properties, double attackDamage, double attackSpeed, Map<Attribute, AttributeModifier> additionalAttributes) {
        super(properties);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", attackDamage, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", attackSpeed, AttributeModifier.Operation.ADDITION));
        for (Map.Entry<Attribute, AttributeModifier> modifierEntry : additionalAttributes.entrySet()) {
            builder.put(modifierEntry.getKey(), modifierEntry.getValue());
        }
        this.defaultModifiers = builder.build();
    }

    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot pEquipmentSlot) {
        return (pEquipmentSlot == EquipmentSlot.MAINHAND || pEquipmentSlot == EquipmentSlot.OFFHAND) ? this.defaultModifiers : super.getDefaultAttributeModifiers(pEquipmentSlot);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment.category == EnchantmentCategory.WEAPON;
    }

    @Override
    public boolean isEnchantable(ItemStack pStack) {
        return true;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        StaffArmPose.initializeClient(consumer);
    }

    @Override
    public int getEnchantmentValue() {
        return 20;
    }
}
