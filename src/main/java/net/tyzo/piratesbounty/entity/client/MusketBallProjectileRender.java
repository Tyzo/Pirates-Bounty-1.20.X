package net.tyzo.piratesbounty.entity.client;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.tyzo.piratesbounty.PiratesBountyMod;
import net.tyzo.piratesbounty.entity.custom.MusketBallEntity;
import net.tyzo.piratesbounty.entity.layer.ModModelLayers;

public class MusketBallProjectileRender extends EntityRenderer<MusketBallEntity> {
	public static final Identifier TEXTURE = new Identifier(PiratesBountyMod.MOD_ID, "textures/entity/musket_ball.png");
	protected MusketBallProjectileModel model;

	public MusketBallProjectileRender(EntityRendererFactory.Context ctx) {
		super(ctx);
		model = new MusketBallProjectileModel(ctx.getPart(ModModelLayers.MUSKET_BALL_PROJECTILE));
	}

	@Override
	public void render(MusketBallEntity entity, float yaw, float tickDelta, MatrixStack matrices,
					   VertexConsumerProvider vertexConsumers, int light) {
		matrices.push();
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.lerp(tickDelta, entity.prevYaw, entity.getYaw()) - 90.0F));
		matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.lerp(tickDelta, entity.prevPitch, entity.getPitch()) + 90.0F));
		VertexConsumer vertexconsumer = ItemRenderer.getDirectItemGlintConsumer(vertexConsumers, this.model.getLayer(TEXTURE), false, false);
		this.model.render(matrices, vertexconsumer, light, OverlayTexture.DEFAULT_UV, 1.0F, 0.0F, 0.0F, 1.0F);

		matrices.pop();
		super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
	}

	@Override
	public Identifier getTexture(MusketBallEntity entity) {
		return TEXTURE;
	}
}