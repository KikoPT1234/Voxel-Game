package me.kikopt.voxelgame.engine;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.system.libc.LibCStdlib;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL42.glTexStorage3D;
import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_load;

public final class Loader {

	private static final List<Integer> vaos = new ArrayList<>();
	private static final List<Integer> vbos = new ArrayList<>();

	private static int textureID;

	public static void createArrayTexture(int width, int height, int depth) {

		if (textureID != 0) throw new IllegalStateException("Texture has already been created!");

		textureID = glGenTextures();

		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D_ARRAY, textureID);
		glTexStorage3D(GL_TEXTURE_2D_ARRAY, 1, GL_RGBA8, width, height, depth);

		glGenerateMipmap(GL_TEXTURE_2D_ARRAY);

		glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		glTexParameterf(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_LOD_BIAS, -1);

	}

	public static void addTexture(String fileName, int level) {

		if (textureID == 0) throw new IllegalStateException("Texture hasn't been created!");

		int width, height;
		ByteBuffer image;

		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer w = stack.mallocInt(1);
			IntBuffer h = stack.mallocInt(1);
			IntBuffer comp = stack.mallocInt(1);

			image = stbi_load("src/main/resources/textures/" + fileName, w, h, comp, 4);
			if (image == null) {
				System.err.println("Failed to load texture file: " + fileName + "\n" + stbi_failure_reason());
				System.exit(-1);
			}

			width = w.get();
			height = h.get();
		}
		glTexSubImage3D(GL_TEXTURE_2D_ARRAY, 0, 0, 0, level, width, height, 1, GL_RGBA, GL_UNSIGNED_BYTE, image);

		glTexParameteri(GL_TEXTURE_2D_ARRAY,GL_TEXTURE_MIN_FILTER,GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D_ARRAY,GL_TEXTURE_MAG_FILTER,GL_NEAREST);
	}

	public static void cleanup() {
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
		for (int vbo : vbos) {
			glDeleteBuffers(vbo);
		}
		for (int vao : vaos) {
			glDeleteVertexArrays(vao);
		}
		vaos.clear();
		vbos.clear();
	}

	private static void unbindVAO() {
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
	}

	public static int getTextureID() {
		return textureID;
	}

}