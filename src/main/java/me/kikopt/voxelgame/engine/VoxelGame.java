package me.kikopt.voxelgame.engine;

import me.kikopt.voxelgame.engine.shaders.StaticShader;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.BufferUtils;

import java.nio.IntBuffer;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class VoxelGame {

	private static final float FOG_DENSITY = 0f;
	private static final float FOG_GRADIENT = 15;
	private static final float FOV = 110;
	private static final float NEAR_PLANE = 0.1f;
	private static final float FAR_PLANE = 1000;
	private static final int RENDER_DISTANCE = 24;
	private static final Vector3f SKY_COLOR = new Vector3f(0.398f, 0.773f, 1);

	private static final ExecutorService executor = Executors.newSingleThreadExecutor();

	private static Matrix4f projectionMatrix;

	public static void main(String[] args) {

		long window = Display.createDisplay();

//		glEnable(GL43.GL_DEBUG_OUTPUT);
//		GL43.glDebugMessageCallback((int source, int type, int id, int severity, int length, long message, long userParam) -> {
//			String msg = GLDebugMessageCallback.getMessage(length, message);
//			System.out.println(msg);
//		}, 0);

		StaticShader shader = new StaticShader();

		glfwSetWindowSizeCallback(window, (window1, width, height) -> {
			glViewport(0, 0, width, height);

			IntBuffer w = BufferUtils.createIntBuffer(1);
			IntBuffer h = BufferUtils.createIntBuffer(1);

			glfwGetWindowSize(Display.getWindow(), w, h);
			glfwSetCursorPos(Display.getWindow(), (float) w.get() / 2, (float) h.get() / 2);

			createProjectionMatrix();

			shader.start();
			shader.loadProjectionMatrix(projectionMatrix);
			shader.stop();
		});

		World world = new World();
		Map<Vector3i, Chunk> chunks = world.getChunks();

		Camera camera = new Camera(world);

		executor.execute(() -> {
			Random random = new Random();
			PerlinNoise noise = new PerlinNoise();
			ChunkManager manager = new ChunkManager();
			while (!glfwWindowShouldClose(window)) {
				for (int x = -RENDER_DISTANCE + (int) camera.getPosition().x / Chunk.CHUNK_WIDTH; x < RENDER_DISTANCE + camera.getPosition().x / Chunk.CHUNK_WIDTH; x++) {
					for (int z = -RENDER_DISTANCE + (int) camera.getPosition().z / Chunk.CHUNK_WIDTH; z < RENDER_DISTANCE + camera.getPosition().z / Chunk.CHUNK_WIDTH; z++) {
						Chunk chunk = world.getChunk(x, 0, z);
						if (chunk == null) {
							chunk = new Chunk(new Vector3i(x, 0, z), world);

							for (int blockX = 0; blockX < Chunk.CHUNK_WIDTH; blockX++) {
								for (int blockZ = 0; blockZ < Chunk.CHUNK_WIDTH; blockZ++) {
									int blockY = (int) (noise.getPerlinNoise(blockX + chunk.getPosition().x + 30000, blockZ + chunk.getPosition().z + 30000)) - 20;

									for (int i = Math.max(blockY, 80); i >= 0; i--) {
										if (i > blockY && i <= 80) chunk.setBlock(6, blockX, i, blockZ);
										else if (i > random.nextFloat() * 10 + 120) chunk.setBlock(4, blockX, i, blockZ);
										else if (i > random.nextFloat() * 5 + 110) chunk.setBlock(3, blockX, i, blockZ);
										else if (blockY - i <= 1) {
											if (i > random.nextFloat() * 5 + 82) chunk.setBlock(1, blockX, i, blockZ);
											else chunk.setBlock(5, blockX, i, blockZ);
										}
										else if (blockY - i <= random.nextFloat() * 4 + 1) chunk.setBlock(2, blockX, i, blockZ);
										else chunk.setBlock(3, blockX, i, blockZ);
									}
								}
							}
						} else if ((chunk.isLoaded() || chunk.isGenerated()) && !chunk.isUpdated()) continue;

						chunk.setUpdated(false);

						if (!chunks.containsValue(chunk)) chunks.put(chunk.getGridPosition(), chunk);

						manager.buildChunk(chunk, world);

						Vector3i gridPosition = chunk.getGridPosition();

						Vector3i backChunkPos = new Vector3i(gridPosition.x, gridPosition.y, gridPosition.z - 1);
						Chunk backChunk = world.getChunk(backChunkPos);

						Vector3i frontChunkPos = new Vector3i(gridPosition.x, gridPosition.y, gridPosition.z + 1);
						Chunk frontChunk = world.getChunk(frontChunkPos);

						Vector3i leftChunkPos = new Vector3i(gridPosition.x - 1, gridPosition.y, gridPosition.z);
						Chunk leftChunk = world.getChunk(leftChunkPos);

						Vector3i rightChunkPos = new Vector3i(gridPosition.x + 1, gridPosition.y, gridPosition.z);
						Chunk rightChunk = world.getChunk(rightChunkPos);

						if (backChunk != null && (manager.getForceQueue().contains(backChunk))) {
							manager.buildChunk(backChunk, world);
						}
						if (frontChunk != null && (manager.getForceQueue().contains(frontChunk))) {
							manager.buildChunk(frontChunk, world);
						}
						if (leftChunk != null && (manager.getForceQueue().contains(leftChunk))) {
							manager.buildChunk(leftChunk, world);
						}
						if (rightChunk != null && (manager.getForceQueue().contains(rightChunk))) {
							manager.buildChunk(rightChunk, world);
						}


					}
				}
				for (Chunk chunk : new ArrayList<>(manager.getForceQueue())) {
					manager.buildChunk(chunk, world);
				}
			}
		});

		Loader.createArrayTexture(16, 16, 7);

		Loader.addTexture("grass.jpg", 0);
		Loader.addTexture("dirt.jpg", 1);
		Loader.addTexture("stone.jpg", 2);
		Loader.addTexture("snow.jpg", 3);
		Loader.addTexture("sand.jpg", 4);
		Loader.addTexture("water.png", 5);
		Loader.addTexture("log.jpg", 6);

		long timer = System.currentTimeMillis();

		int fps = 0;

		createProjectionMatrix();

		Picker picker = new Picker(camera, projectionMatrix, world);

		while (!glfwWindowShouldClose(window)) {

			camera.move();
			picker.update();

			picker.getBlock();

			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				System.out.println("FPS: " + fps + ", X: " + camera.getPosition().x + ", Y: " + camera.getPosition().y + ", Z: " + camera.getPosition().z);
				fps = 0;
			}

			world.getSunDirection().rotateZ(0.0002f);

			glEnable(GL_CULL_FACE);
			glEnable(GL_BLEND);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
			glCullFace(GL_BACK);
			glEnable(GL_DEPTH_TEST);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			glClearColor(SKY_COLOR.x, SKY_COLOR.y, SKY_COLOR.z, 1);

			shader.start();
			shader.loadViewMatrix(camera);
			shader.loadProjectionMatrix(projectionMatrix);
			shader.loadFog(SKY_COLOR, FOG_DENSITY, FOG_GRADIENT);

			for (Chunk chunk : chunks.values()) {
				if (chunk.isGenerated() || chunk.isUpdated()) {
					if (Math.abs(camera.getPosition().x - chunk.getPosition().x) > Chunk.CHUNK_WIDTH * RENDER_DISTANCE || Math.abs(camera.getPosition().z - chunk.getPosition().z) > Chunk.CHUNK_WIDTH * RENDER_DISTANCE) {
						chunk.getMesh().unloadBuffers();
						//chunks.remove(chunk.getGridPosition());
						chunk.setLoaded(false);
						chunk.setGenerated(false);
						chunk.setUpdated(false);
						continue;
					}
					if (!chunk.isLoaded()) {
						chunk.getMesh().loadBuffers();
					}
					chunk.getMesh().render(shader, world.getSunDirection());
				}
			}

			shader.stop();

			Display.updateDisplay();

			fps++;
		}

		shader.cleanup();
		Loader.cleanup();
		Display.closeDisplay();

		executor.shutdownNow();

		System.exit(0);

	}

	private static void createProjectionMatrix() {
		IntBuffer w = BufferUtils.createIntBuffer(1);
		IntBuffer h = BufferUtils.createIntBuffer(1);

		glfwGetWindowSize(Display.getWindow(), w, h);

		int width = w.get();
		int height = h.get();

		float aspectRatio = (float) width / (float) height;
		float yScale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
		float xScale = yScale / aspectRatio;
		float frustumLength = FAR_PLANE - NEAR_PLANE;

		projectionMatrix = new Matrix4f();
		projectionMatrix.m00(xScale);
		projectionMatrix.m11(yScale);
		projectionMatrix.m22(-((FAR_PLANE + NEAR_PLANE) / frustumLength));
		projectionMatrix.m23(-1);
		projectionMatrix.m32(-((2 * NEAR_PLANE * FAR_PLANE) / frustumLength));
		projectionMatrix.m33(0);
	}

}