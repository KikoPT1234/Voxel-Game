package me.kikopt.voxelgame.engine;

import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;

import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL11.*;

public final class Display {

	public static final int HEIGHT = 1080;
	public static final int WIDTH = 1920;

	private static long lastFrameTime;
	private static float delta;

	private static long window;

	public static long createDisplay() {

		boolean glfwResult = glfwInit();

		if (!glfwResult) throw new IllegalStateException("GLFW init failed");

		window = glfwCreateWindow(WIDTH, HEIGHT, "Display", MemoryUtil.NULL, MemoryUtil.NULL);
		if (window == MemoryUtil.NULL) throw new IllegalStateException("Window creation failed");

		glfwMakeContextCurrent(window);

		glfwShowWindow(window);

		createCapabilities();
		glViewport(0, 0, WIDTH, HEIGHT);

		lastFrameTime = getCurrentTime();

		return window;
	}

	public static void updateDisplay() {
		glfwSwapBuffers(window);
		glfwPollEvents();

		long currentFrameTime = getCurrentTime();
		delta = (currentFrameTime - lastFrameTime) / 1000f;
		lastFrameTime = currentFrameTime;
	}

	public static float getFrameTimeSeconds() {
		return delta;
	}

	public static void closeDisplay() {
		glfwDestroyWindow(window);
		glfwTerminate();
	}

	private static long getCurrentTime() {
		return System.currentTimeMillis();
	}

	public static long getWindow() {
		return window;
	}

}