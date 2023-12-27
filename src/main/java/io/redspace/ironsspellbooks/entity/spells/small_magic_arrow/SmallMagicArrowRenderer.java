package io.redspace.ironsspellbooks.entity.spells.small_magic_arrow;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.spells.magic_arrow.MagicArrowRenderer;
import io.redspace.ironsspellbooks.entity.spells.poison_arrow.PoisonArrow;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class SmallMagicArrowRenderer extends EntityRenderer<SmallMagicArrow> {
    private static final ResourceLocation TEXTURE = IronsSpellbooks.id("textures/entity/small_magic_arrow.png");

    public SmallMagicArrowRenderer(Context context) {
        super(context);
    }

    @Override
    public void render(SmallMagicArrow entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
        poseStack.pushPose();

        Vec3 motion = entity.getDeltaMovement();
        float xRot = -((float) (Mth.atan2(motion.horizontalDistance(), motion.y) * (double) (180F / (float) Math.PI)) - 90.0F);
        float yRot = -((float) (Mth.atan2(motion.z, motion.x) * (double) (180F / (float) Math.PI)) + 90.0F);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(yRot));
        poseStack.mulPose(Vector3f.XP.rotationDegrees(xRot));

        float f9 = entity.shakeTime - partialTicks;
        if (f9 > 0.0F) {
            float f10 = -Mth.sin(f9 * 3.0F) * f9;
            poseStack.mulPose(Vector3f.XP.rotationDegrees(f10));
        }

        renderModel(poseStack, bufferSource);
        poseStack.popPose();

        super.render(entity, yaw, partialTicks, poseStack, bufferSource, light);
    }

    public static void renderModel(PoseStack poseStack, MultiBufferSource bufferSource) {
        poseStack.scale(0.05625F, 0.05625F, 0.05625F);

        Pose pose = poseStack.last();
        Matrix4f poseMatrix = pose.pose();
        Matrix3f normalMatrix = pose.normal();

        VertexConsumer consumer = bufferSource.getBuffer(MagicArrowRenderer.CustomRenderType.magic(getTextureLocation()));
        poseStack.mulPose(Vector3f.YP.rotationDegrees(90.0F));
        poseStack.translate(-2, 0, 0);

        for (int j = 0; j < 4; ++j) {
            poseStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
            vertex(poseMatrix, normalMatrix, consumer, -8, -2, 0, 0.0F, 0.0F, 0, 1, 0, LightTexture.FULL_BRIGHT);
            vertex(poseMatrix, normalMatrix, consumer, 8, -2, 0, 0.5F, 0.0F, 0, 1, 0, LightTexture.FULL_BRIGHT);
            vertex(poseMatrix, normalMatrix, consumer, 8, 2, 0, 0.5F, 0.15625F, 0, 1, 0, LightTexture.FULL_BRIGHT);
            vertex(poseMatrix, normalMatrix, consumer, -8, 2, 0, 0.0F, 0.15625F, 0, 1, 0, LightTexture.FULL_BRIGHT);
        }
    }

    public static void vertex(Matrix4f pMatrix, Matrix3f pNormals, VertexConsumer pVertexBuilder, int pOffsetX, int pOffsetY, int pOffsetZ, float pTextureX, float pTextureY, int pNormalX, int p_113835_, int p_113836_, int pPackedLight) {
        pVertexBuilder.vertex(pMatrix, (float) pOffsetX, (float) pOffsetY, (float) pOffsetZ).color(255, 255, 255, 255).uv(pTextureX, pTextureY).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(pPackedLight).normal(pNormals, (float) pNormalX, (float) p_113836_, (float) p_113835_).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(SmallMagicArrow entity) {
        return getTextureLocation();
    }

    public static ResourceLocation getTextureLocation() {
        return TEXTURE;
    }

}