package me.kikopt.voxelgame.engine;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3i;

public class ChunkMeshBuilder {

	public static final int COMPONENT_SIZE = 9;
	public static final int MAX_VERTICES_FLOATS = Chunk.CHUNK_VOLUME * (4 * (COMPONENT_SIZE)) * 6;
	public static final int MAX_INDICES = Chunk.CHUNK_VOLUME * 6 * 6;

	private final float[] vertices;
	private int vertexIndex;

	private final int[] indices;
	private int indicesIndex;

	public ChunkMeshBuilder() {
		this.vertices = new float[MAX_VERTICES_FLOATS];
		this.vertexIndex = 0;

		this.indices = new int[MAX_INDICES];
		System.out.println("Create new Mesh Builder instance with " + MAX_VERTICES_FLOATS + " vertices and " + MAX_INDICES + " indices.");
	}

	public void vertex(float x, float y, float z) {
		final int index = vertexIndex;

		vertices[index] = x;
		vertices[index + 1] = y;
		vertices[index + 2] = z;

		vertexIndex += COMPONENT_SIZE;
	}

	public void uv(float u, float v, float w) {
		final int index = vertexIndex + 3;

		vertices[index] = u;
		vertices[index + 1] = v;
		vertices[index + 2] = w;
	}

	public void normal(@NotNull Vector3i normal) {
		final int index = vertexIndex + 6;
		vertices[index] = normal.x;
		vertices[index + 1] = normal.y;
		vertices[index + 2] = normal.z;
	}

	public void generateIndices() {
		indicesIndex = (vertexIndex / 45) * 60;

		int j = 0;
		for(int i = 0; i < indicesIndex; i += 6, j += 4) {
			indices[i] = j;
			indices[i + 1] = (j + 1);
			indices[i + 2] = (j + 2);
			indices[i + 3] = (j + 2);
			indices[i + 4] = (j + 3);
			indices[i + 5] = j;
		}
	}

	public void reset() {
		vertexIndex = 0;
		indicesIndex = 0;
	}

	public float[] getVertices() {
		return vertices;
	}

	public int[] getIndices() {
		return indices;
	}

	public int getVertexIndex() {
		return vertexIndex;
	}

	public int getIndicesIndex() {
		return indicesIndex;
	}
}
