package com.example.testmod.entity.armor;

import net.minecraft.world.entity.EquipmentSlot;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.item.GeoArmorItem;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;
import software.bernie.geckolib3.util.GeoUtils;

public class GenericCustomArmorRenderer <T extends GeoArmorItem & IAnimatable> extends GeoArmorRenderer<T> {
    public String leggingTorsoLayerBoneName = "armorLeggingTorsoLayer";
    public String capeBoneName = "cape";

    public GenericCustomArmorRenderer(AnimatedGeoModel model) {
        super(model);

        this.headBone = "armorHead";
        this.bodyBone = "armorBody";
        this.rightArmBone = "armorRightArm";
        this.leftArmBone = "armorLeftArm";
        this.rightLegBone = "armorRightLeg";
        this.leftLegBone = "armorLeftLeg";
        this.rightBootBone = "armorRightBoot";
        this.leftBootBone = "armorLeftBoot";

        GeoBone leggingTorsoLayerBone = new GeoBone();
        leggingTorsoLayerBone.name = "armorLeggingTorsoLayer";
        GeoBone capeBone = new GeoBone();
        capeBone.name = "cape";
        var m = getGeoModelProvider();
        m.registerBone(leggingTorsoLayerBone);
        m.registerBone(capeBone);


    }

    @Override
    protected void fitToBiped() {
        super.fitToBiped();
        if (this.leggingTorsoLayerBoneName != null) {
            IBone torsoLayerBone = this.getGeoModelProvider().getBone(this.leggingTorsoLayerBoneName);

            GeoUtils.copyRotations(this.body, torsoLayerBone);
            torsoLayerBone.setPositionX(this.body.x);
            torsoLayerBone.setPositionY(-this.body.y);
            torsoLayerBone.setPositionZ(this.body.z);
        }
        //TestMod.LOGGER.debug("WizardArmorRenderer.fitToBiped all bones: {}", WizardArmorModel.listOfBonesToString(getGeoModelProvider().getAnimationProcessor().getModelRendererList()));
    }

    @Override
    public GeoArmorRenderer applySlot(EquipmentSlot slot) {
        //What is this for?
        this.getGeoModelProvider().getModel(this.getGeoModelProvider().getModelResource(this.currentArmorItem));

        setBoneVisibility(this.headBone, false);
        setBoneVisibility(this.bodyBone, false);
        setBoneVisibility(this.rightArmBone, false);
        setBoneVisibility(this.leftArmBone, false);
        setBoneVisibility(this.rightLegBone, false);
        setBoneVisibility(this.leftLegBone, false);
        setBoneVisibility(this.rightBootBone, false);
        setBoneVisibility(this.rightBootBone, false);
        setBoneVisibility(this.leftBootBone, false);
        setBoneVisibility(this.leggingTorsoLayerBoneName, false);

        switch (slot) {
            case HEAD -> setBoneVisibility(this.headBone, true);
            case CHEST -> {
                setBoneVisibility(this.bodyBone, true);
                setBoneVisibility(this.rightArmBone, true);
                setBoneVisibility(this.leftArmBone, true);
            }
            case LEGS -> {
                setBoneVisibility(this.rightLegBone, true);
                setBoneVisibility(this.leftLegBone, true);
                setBoneVisibility(this.leggingTorsoLayerBoneName, true);

            }
            case FEET -> {
                setBoneVisibility(this.rightBootBone, true);
                setBoneVisibility(this.leftBootBone, true);
            }
            default -> {
            }
        }

        return this;
    }
}