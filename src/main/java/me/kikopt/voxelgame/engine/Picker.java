package me.kikopt.voxelgame.engine;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.joml.*;

import java.lang.Math;

import static org.lwjgl.glfw.GLFW.*;

public class Picker {

	private Vector3f currentRay;

	private final Matrix4f projection;
	private Matrix4f view;
	private final Camera camera;
	private final World world;
	private final ChunkManager manager = new ChunkManager();

	private boolean hasPressed = false;

	public Picker(Camera camera, Matrix4f projection, World world) {
		this.camera = camera;
		this.projection = projection;
		this.view = Utils.createViewMatrix(camera);
		this.world = world;
	}

	public Vector3f getBlock() {
		int block;
		Vector3f coordinates = new Vector3f(camera.getPosition());
		for (int i = 0; i < 5; i++) {
			int chunkX = (int) Math.floor(coordinates.x / Chunk.CHUNK_WIDTH);
			int chunkZ = (int) Math.floor(coordinates.z / Chunk.CHUNK_WIDTH);

			Chunk chunk = world.getChunk(chunkX, 0, chunkZ);
			if (chunk == null) continue;

			int x = (int) Math.floor(coordinates.x >= 0 ? coordinates.x % Chunk.CHUNK_WIDTH : (Chunk.CHUNK_WIDTH - (Math.abs(coordinates.x) % Chunk.CHUNK_WIDTH)));
			int y = (int) Math.floor(coordinates.y);
			int z = (int) Math.floor(coordinates.z >= 0 ? coordinates.z % Chunk.CHUNK_WIDTH : (Chunk.CHUNK_WIDTH - (Math.abs(coordinates.z) % Chunk.CHUNK_WIDTH)));

			block = chunk.getBlock(x, y, z);
			if (block > 0 && block != 6) {
				if (glfwGetMouseButton(Display.getWindow(), GLFW_MOUSE_BUTTON_LEFT) == GLFW_PRESS && !hasPressed) {
					chunk.setBlockUpdate(manager, 0, x, y, z);
					hasPressed = true;
					break;
				} else if (glfwGetMouseButton(Display.getWindow(), GLFW_MOUSE_BUTTON_LEFT) != GLFW_PRESS && hasPressed) {
					hasPressed = false;
				}
			}
			coordinates.add(currentRay);
		}

		return coordinates;
	}

	public Vector3f getCurrentRay() {
		return currentRay;
	}

	public void update() {
		view = Utils.createViewMatrix(camera);
		currentRay = calculateRay();
	}

	private @NotNull Vector3f calculateRay() {
		Vector4f clipCoords = new Vector4f(0, 0, -1, 1);
		Vector4f eyeCoords = toEyeCoords(clipCoords);
		return toWorldCoords(eyeCoords);
	}

	private @NotNull Vector3f toWorldCoords(Vector4f eyeCoords) {
		Matrix4f invertexView = new Matrix4f();
		view.invert(invertexView);
		Vector4f rayWorld = invertexView.transform(eyeCoords);
		Vector3f mouseRay = new Vector3f(rayWorld.x, rayWorld.y, rayWorld.z);
		mouseRay.normalize();
		return mouseRay;
	}

	@Contract("_ -> new")
	private @NotNull Vector4f toEyeCoords(Vector4f clipCoords) {
		Matrix4f invertedProjection = new Matrix4f();
		projection.invert(invertedProjection);
		Vector4f eyeCoords = invertedProjection.transform(clipCoords);
		return new Vector4f(eyeCoords.x, eyeCoords.y, -1, 0);
	}
}
