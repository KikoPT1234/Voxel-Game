package me.kikopt.voxelgame.engine;

import me.kikopt.voxelgame.engine.shaders.StaticShader;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL45.*;

public class ChunkMesh {

	private final Chunk chunk;
	private FloatBuffer dataBuffer;
	private IntBuffer indices;
	private int vao;
	private int dataVBO = 0;
	private int indexVBO = 0;
	public boolean allocated = false;

	public ChunkMesh(Chunk chunk) {
		this.chunk = chunk;
	}

	public ChunkMesh buildBuffers(@NotNull ChunkMeshBuilder builder) {

		if (allocated) deAllocate();

		dataBuffer = MemoryUtil.memAllocFloat(builder.getVertexIndex());
		for (int i = 0; i < builder.getVertexIndex(); i++) dataBuffer.put(builder.getVertices()[i]);
		dataBuffer.flip();

		indices = MemoryUtil.memAllocInt(builder.getIndicesIndex());
		for (int i = 0; i < builder.getIndicesIndex(); i++) indices.put(builder.getIndices()[i]);
		indices.flip();

		allocated = true;

		return this;
	}

	public void loadBuffers() {

		if (vao == 0) vao = glGenVertexArrays();
		glBindVertexArray(vao);

		if (dataVBO == 0) dataVBO = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, dataVBO);
		glBufferData(GL_ARRAY_BUFFER, dataBuffer, GL_DYNAMIC_DRAW);

		int floatByteSize = 4;
		int positionFloatCount = 3;
		int textureFloatCount = 3;
		int normalFloatCount = 3;
		int floatsPerVertex = positionFloatCount + textureFloatCount + normalFloatCount;
		int vertexFloatSizeInBytes = floatByteSize * floatsPerVertex;

		glEnableVertexAttribArray(0);
		glVertexAttribPointer(0, positionFloatCount, GL_FLOAT, false, vertexFloatSizeInBytes, 0);

		int byteOffset = floatByteSize * positionFloatCount;
		glEnableVertexAttribArray(1);
		glVertexAttribPointer(1, textureFloatCount, GL_FLOAT, false, vertexFloatSizeInBytes, byteOffset);

		int byteOffset2 = floatByteSize * (positionFloatCount + textureFloatCount);
		glEnableVertexAttribArray(2);
		glVertexAttribPointer(2, normalFloatCount, GL_FLOAT, false, vertexFloatSizeInBytes, byteOffset2);

		if (indexVBO == 0) indexVBO = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexVBO);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_DYNAMIC_DRAW);

		glBindVertexArray(0);

		deAllocate();

		dataBuffer = null;
		indices = null;

		chunk.setLoaded(true);
	}

	public void unloadBuffers() {
		if (vao == 0) return;
		glBindVertexArray(vao);
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(2);

		glDeleteBuffers(dataVBO);
		glDeleteBuffers(indexVBO);

		glBindVertexArray(0);

		glDeleteVertexArrays(vao);

		dataVBO = 0;
		indexVBO = 0;
		vao = 0;

		//chunk.setLoaded(false);
		//chunk.setGenerated(false);
	}

	public void render(@NotNull StaticShader shader, Vector3f sunDirection) {
		int indexCount = chunk.getIndexCount();

		glBindVertexArray(vao);
		Matrix4f transformationMatrix = Utils.createTransformationMatrix(chunk.getPosition(), 0, 0, 0, 1);
		shader.loadTransformationMatrix(transformationMatrix);
		shader.loadSunDirection(sunDirection);
		glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0);
		glBindVertexArray(0);
	}

	public void deAllocate() {
		MemoryUtil.memFree(dataBuffer);
		MemoryUtil.memFree(indices);

		allocated = false;
	}
}
