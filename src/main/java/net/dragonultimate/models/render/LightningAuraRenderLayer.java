package net.dragonultimate.models.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.dragonultimate.save.SaveAuraColor;
import net.dragonultimate.shader.AuraState;
import net.dragonultimate.shader.LightRenderTypes;
import net.dragonultimate.shader.LightShaderManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Camera;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LightningAuraRenderLayer
    extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    private static final int ITERATIONS = 4;
    private static final float BRANCH_CHANCE = 0.34f;
    private static final float BASE_WIDTH = 0.045f;
    private static final float HEIGHT = 2.45f;

    public LightningAuraRenderLayer(
        RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                       AbstractClientPlayer player, float limbSwing, float limbSwingAmount,
                       float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!AuraState.isActive() || LightShaderManager.getShader() == null) return;

        SaveAuraColor.AuraColorData colors = SaveAuraColor.getAuraColor(player);
        float[] color = colors.outerArray();
        long seed = player.getUUID().getMostSignificantBits()
            ^ player.getUUID().getLeastSignificantBits()
            ^ (long) (ageInTicks * 2.0f);
        Random random = new Random(seed);

        Vector3f start = new Vector3f(0.0f, 0.05f, 0.0f);
        Vector3f end = new Vector3f(0.0f, HEIGHT, 0.0f);
        List<Branch> branches = new ArrayList<>();
        generateBranches(start, end, ITERATIONS, HEIGHT * 0.28f,
            BRANCH_CHANCE, random, 1.0f, branches);

        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        Vec3 cameraPosition = camera.getPosition();
        Vec3 playerPosition = player.position();
        Vector3f cameraVector = new Vector3f(
            (float) (cameraPosition.x - playerPosition.x),
            (float) (cameraPosition.y - playerPosition.y),
            (float) (cameraPosition.z - playerPosition.z)
        );

        VertexConsumer consumer = buffer.getBuffer(LightRenderTypes.lightning());
        Matrix4f matrix = poseStack.last().pose();
        for (Branch branch : branches) {
            renderBranch(consumer, matrix, branch, cameraVector,
                color[0], color[1], color[2], AuraState.getIntensity());
        }
    }

    private static void generateBranches(Vector3f start, Vector3f end, int iterations,
                                         float displacement, float branchChance, Random random,
                                         float intensity, List<Branch> output) {
        Branch branch = new Branch(intensity);
        subdivide(start, end, iterations, displacement, branchChance, random, branch, output);
    }

    private static void subdivide(Vector3f start, Vector3f end, int iterations,
                                  float displacement, float branchChance, Random random,
                                  Branch branch, List<Branch> output) {
        if (iterations <= 0) {
            branch.segments.add(new Segment(new Vector3f(start), new Vector3f(end)));
            if (!output.contains(branch)) output.add(branch);
            return;
        }

        Vector3f direction = new Vector3f(end).sub(start);
        float length = direction.length();
        direction.normalize();

        Vector3f offsetDirection = randomUnitVector(random);
        offsetDirection.sub(new Vector3f(direction).mul(offsetDirection.dot(direction)));
        if (offsetDirection.lengthSquared() < 0.0001f) {
            offsetDirection = new Vector3f(direction).cross(0.0f, 1.0f, 0.0f);
        }
        offsetDirection.normalize();

        Vector3f midpoint = new Vector3f(start).add(end).mul(0.5f);
        midpoint.add(offsetDirection.mul((random.nextFloat() * 2.0f - 1.0f) * displacement));

        subdivide(start, midpoint, iterations - 1, displacement * 0.5f,
            branchChance * 0.7f, random, branch, output);
        subdivide(midpoint, end, iterations - 1, displacement * 0.5f,
            branchChance * 0.7f, random, branch, output);

        if (random.nextFloat() < branchChance) {
            Vector3f branchDirection = randomUnitVector(random);
            branchDirection.mul(length * (0.32f + random.nextFloat() * 0.28f));
            Vector3f branchEnd = new Vector3f(midpoint).add(branchDirection);
            Branch child = new Branch(branch.intensity * (0.55f + random.nextFloat() * 0.2f));
            subdivide(midpoint, branchEnd, iterations - 1, displacement * 0.5f,
                branchChance * 0.7f, random, child, output);
        }
    }

    private static Vector3f randomUnitVector(Random random) {
        float z = random.nextFloat() * 2.0f - 1.0f;
        float angle = random.nextFloat() * (float) (Math.PI * 2.0);
        float radius = (float) Math.sqrt(Math.max(0.0f, 1.0f - z * z));
        return new Vector3f(
            radius * (float) Math.cos(angle),
            radius * (float) Math.sin(angle),
            z
        );
    }

    private static void renderBranch(VertexConsumer consumer, Matrix4f matrix, Branch branch,
                                     Vector3f cameraVector, float r, float g, float b,
                                     float auraIntensity) {
        int segmentCount = Math.max(1, branch.segments.size());
        for (int index = 0; index < segmentCount; index++) {
            Segment segment = branch.segments.get(index);
            Vector3f direction = new Vector3f(segment.end).sub(segment.start).normalize();
            Vector3f billboard = new Vector3f(direction).cross(cameraVector);
            if (billboard.lengthSquared() < 0.0001f) {
                billboard = new Vector3f(direction).cross(0.0f, 1.0f, 0.0f);
            }
            billboard.normalize();

            float fade = 1.0f - (index / (float) segmentCount) * 0.72f;
            float alpha = Math.min(1.0f, auraIntensity * branch.intensity * fade);
            float width = BASE_WIDTH * (0.55f + branch.intensity * 0.75f) * fade;
            billboard.mul(width);

            Vector3f a = new Vector3f(segment.start).add(billboard);
            Vector3f bPoint = new Vector3f(segment.start).sub(billboard);
            Vector3f c = new Vector3f(segment.end).sub(billboard);
            Vector3f d = new Vector3f(segment.end).add(billboard);

            vertex(consumer, matrix, a, r, g, b, alpha);
            vertex(consumer, matrix, bPoint, r, g, b, alpha);
            vertex(consumer, matrix, c, r, g, b, alpha * 0.72f);
            vertex(consumer, matrix, d, r, g, b, alpha * 0.72f);
        }
    }

    private static void vertex(VertexConsumer consumer, Matrix4f matrix, Vector3f position,
                               float r, float g, float b, float alpha) {
        consumer.addVertex(matrix, position.x, position.y, position.z)
            .setColor(r, g, b, alpha);
    }

    private static final class Branch {
        private final float intensity;
        private final List<Segment> segments = new ArrayList<>();

        private Branch(float intensity) {
            this.intensity = intensity;
        }
    }

    private record Segment(Vector3f start, Vector3f end) {}
}
