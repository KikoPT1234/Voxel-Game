package me.kikopt.voxelgame.engine;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.lwjgl.glfw.GLFW.*;

public class Camera {

	public static final float SPEED = 20;
	public static final float GRAVITY = 1;

	public float currentSpeed = 0;
	public float sidewaysSpeed = 0;
	public float verticalSpeed = 0;

	private final Vector3f position = new Vector3f(0, 200, 5);
	private float pitch = 0, yaw = 0;

	private final AtomicBoolean wIsPressed = new AtomicBoolean(false);
	private final AtomicBoolean aIsPressed = new AtomicBoolean(false);
	private final AtomicBoolean sIsPressed = new AtomicBoolean(false);
	private final AtomicBoolean dIsPressed = new AtomicBoolean(false);
	private final AtomicBoolean spaceIsPressed = new AtomicBoolean(false);
	private final AtomicBoolean shiftIsPressed = new AtomicBoolean(false);
	private final AtomicBoolean ctrlIsPressed = new AtomicBoolean(false);

	private final World world;

	public Camera(World world) {

		this.world = world;

		IntBuffer widthBuffer = BufferUtils.createIntBuffer(1);
		IntBuffer heightBuffer = BufferUtils.createIntBuffer(1);

		glfwGetWindowSize(Display.getWindow(), widthBuffer, heightBuffer);

		float width = widthBuffer.get();
		float height = heightBuffer.get();

		glfwSetCursorPos(Display.getWindow(), width / 2, height / 2);

		glfwSetInputMode(Display.getWindow(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);

		glfwSetKeyCallback(Display.getWindow(), (window, key, scancode, action, mods) -> {
			if (key == GLFW_KEY_W) wIsPressed.set(action != GLFW_RELEASE);
			if (key == GLFW_KEY_A) aIsPressed.set(action != GLFW_RELEASE);
			if (key == GLFW_KEY_S) sIsPressed.set(action != GLFW_RELEASE);
			if (key == GLFW_KEY_D) dIsPressed.set(action != GLFW_RELEASE);
			if (key == GLFW_KEY_SPACE) spaceIsPressed.set(action != GLFW_RELEASE);
			if (key == GLFW_KEY_LEFT_SHIFT) shiftIsPressed.set(action != GLFW_RELEASE);
			if (key == GLFW_KEY_LEFT_CONTROL) ctrlIsPressed.set(action != GLFW_RELEASE);
		});
	}

	public void move() {

		DoubleBuffer mouseXBuffer = BufferUtils.createDoubleBuffer(1);
		DoubleBuffer mouseYBuffer = BufferUtils.createDoubleBuffer(1);

		glfwGetCursorPos(Display.getWindow(), mouseXBuffer, mouseYBuffer);

		IntBuffer widthBuffer = BufferUtils.createIntBuffer(1);
		IntBuffer heightBuffer = BufferUtils.createIntBuffer(1);

		glfwGetWindowSize(Display.getWindow(), widthBuffer, heightBuffer);

		float width = widthBuffer.get();
		float height = heightBuffer.get();

		glfwSetCursorPos(Display.getWindow(),  width / 2, height / 2);

		double mouseX = mouseXBuffer.get() - width / 2;
		double mouseY = mouseYBuffer.get() - height / 2;

		yaw += mouseX / 10;
		pitch = (float) Math.max(Math.min(pitch + mouseY / 10, 90), -90);

		float speed = SPEED;

		if (ctrlIsPressed.get()) speed *= 5;

		if (wIsPressed.get()) currentSpeed = Math.max(currentSpeed - speed, -speed);
		if (aIsPressed.get()) sidewaysSpeed = Math.min(sidewaysSpeed + speed, speed);
		if (sIsPressed.get()) currentSpeed = Math.min(currentSpeed + speed, speed);
		if (dIsPressed.get()) sidewaysSpeed = Math.max(sidewaysSpeed - speed, -speed);
		if (spaceIsPressed.get()) verticalSpeed = Math.min(verticalSpeed + speed, speed);
		if (shiftIsPressed.get()) verticalSpeed = Math.max(verticalSpeed - speed, -speed);

		Chunk chunk = world.getChunk((int) (position.x / Chunk.CHUNK_WIDTH), 0, (int) (position.y / Chunk.CHUNK_WIDTH));

		if (!wIsPressed.get() && !sIsPressed.get()) currentSpeed = 0;
		if (!dIsPressed.get() && !aIsPressed.get()) sidewaysSpeed = 0;
		if (!spaceIsPressed.get() && !shiftIsPressed.get()) verticalSpeed = 0;

		float frontDistance = currentSpeed * Display.getFrameTimeSeconds();
		float frontDX = (float) (frontDistance * Math.sin(Math.toRadians(yaw)));
		float frontDZ = (float) (frontDistance * Math.cos(Math.toRadians(yaw)));

		float sideYaw = yaw + 90;
		while (sideYaw > 180) sideYaw -= 360;
		while (sideYaw < -180) sideYaw += 360;

		float sideDistance = sidewaysSpeed * Display.getFrameTimeSeconds();
		float sideDX = (float) (sideDistance * Math.sin(Math.toRadians(sideYaw)));
		float sideDZ = (float) (sideDistance * Math.cos(Math.toRadians(sideYaw)));

		position.x -= frontDX;
		position.x -= sideDX;
		position.y += verticalSpeed * Display.getFrameTimeSeconds();
		position.z += frontDZ;
		position.z += sideDZ;
	}

	public Vector3f getPosition() {
		return position;
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}
}
