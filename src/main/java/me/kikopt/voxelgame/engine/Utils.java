package me.kikopt.voxelgame.engine;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3f;
import org.joml.Vector3i;

public final class Utils {

	public static @NotNull Matrix4f createTransformationMatrix(@NotNull Vector3f translation, float rx, float ry, float rz, float scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.translate(new Vector3f(translation.x, translation.y, translation.z));
		matrix.rotate((float) Math.toRadians(rx), new Vector3f(1, 0, 0));
		matrix.rotate((float) Math.toRadians(ry), new Vector3f(0, 1, 0));
		matrix.rotate((float) Math.toRadians(rz), new Vector3f(0, 0, 1));
		matrix.scale(scale);

		return matrix;
	}

	public static @NotNull Matrix4f createTransformationMatrix(@NotNull Vector3i translation, float rx, float ry, float rz, float scale) {
		return createTransformationMatrix(new Vector3f(translation.x, translation.y, translation.z), rx, ry, rz, scale);
	}

	public static @NotNull Matrix4f createViewMatrix(@NotNull Camera camera) {
		Matrix4f matrix = new Matrix4f();
		matrix.rotate((float) Math.toRadians(camera.getPitch()), new Vector3f(1, 0, 0));
		matrix.rotate((float) Math.toRadians(camera.getYaw()), new Vector3f(0, 1, 0));
		Vector3f position = camera.getPosition();
		Vector3f negativePosition = new Vector3f(-position.x, -position.y, -position.z);
		matrix.translate(negativePosition);
		return matrix;
	}

}
