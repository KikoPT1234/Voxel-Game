package me.kikopt.voxelgame.engine;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3i;

import java.nio.charset.Charset;
import java.util.*;

public class ChunkManager {

	public static final MeshFace FRONT_FACE = new MeshFace(new float[] {1, 1, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1}, new Vector3i(0, 0, 1));
	public static final MeshFace BACK_FACE = new MeshFace(new float[] {0, 1, 0, 1, 1, 0, 1, 0, 0, 0, 0, 0}, new Vector3i(0, 0, -1));

	public static final MeshFace LEFT_FACE = new MeshFace(new float[] {0, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 1}, new Vector3i(-1, 0, 0));
	public static final MeshFace RIGHT_FACE = new MeshFace(new float[] {1, 1, 0, 1, 1, 1, 1, 0, 1, 1, 0, 0}, new Vector3i(1, 0, 0));

	public static final MeshFace TOP_FACE = new MeshFace(new float[] {1, 1, 0, 0, 1, 0, 0, 1, 1, 1, 1, 1}, new Vector3i(0, 1, 1));
	public static final MeshFace BOTTOM_FACE = new MeshFace(new float[] {0, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1}, new Vector3i(0, -1, 1));


	public static final MeshFace WATER_FRONT_FACE = new MeshFace(new float[] {1, .875f, 1, 0, .875f, 1, 0, 0, 1, 1, 0, 1}, new Vector3i(0, 0, 1));
	public static final MeshFace WATER_BACK_FACE = new MeshFace(new float[] {0, .875f, 0, 1, .875f, 0, 1, 0, 0, 0, 0, 0}, new Vector3i(0, 0, -1));

	public static final MeshFace WATER_LEFT_FACE = new MeshFace(new float[] {0, .875f, 1, 0, .875f, 0, 0, 0, 0, 0, 0, 1}, new Vector3i(-1, 0, 0));
	public static final MeshFace WATER_RIGHT_FACE = new MeshFace(new float[] {1, .875f, 0, 1, .875f, 1, 1, 0, 1, 1, 0, 0}, new Vector3i(1, 0, 0));

	public static final MeshFace WATER_TOP_FACE = new MeshFace(new float[] {1, .875f, 0, 0, .875f, 0, 0, .875f, 1, 1, .875f, 1}, new Vector3i(0, 1, 1));

	public final ChunkMeshBuilder meshBuilder = new ChunkMeshBuilder();

	public final List<Chunk> forceQueue = new ArrayList<>();

	public void buildChunk(@NotNull Chunk chunk, World world) {
		meshBuilder.reset();

		Vector3i gridPosition = chunk.getGridPosition();

		Vector3i backChunkPos = new Vector3i(gridPosition.x, gridPosition.y, gridPosition.z - 1);
		Chunk backChunk = world.getChunk(backChunkPos);

		Vector3i frontChunkPos = new Vector3i(gridPosition.x, gridPosition.y, gridPosition.z + 1);
		Chunk frontChunk = world.getChunk(frontChunkPos);

		Vector3i leftChunkPos = new Vector3i(gridPosition.x - 1, gridPosition.y, gridPosition.z);
		Chunk leftChunk = world.getChunk(leftChunkPos);

		Vector3i rightChunkPos = new Vector3i(gridPosition.x + 1, gridPosition.y, gridPosition.z);
		Chunk rightChunk = world.getChunk(rightChunkPos);

		if (backChunk == null || frontChunk == null || leftChunk == null || rightChunk == null) {
			if (!forceQueue.contains(chunk)) forceQueue.add(chunk);
			return;
		}
		else forceQueue.remove(chunk);

		for (int y = 0; y < Chunk.CHUNK_HEIGHT; y++) {
			for (int x = 0; x < Chunk.CHUNK_WIDTH; x++) {
				for (int z = 0; z < Chunk.CHUNK_WIDTH; z++) {
					int block = chunk.getBlock(x, y, z);
					int topBlock = chunk.getBlock(x, y + 1, z);

					if (block <= 0) continue;

					int blockFront = chunk.getBlock(x, y, z + 1);
					if (blockFront == -1) {
						blockFront = frontChunk.getBlock(x, y, 0);
					}

					if (blockFront == 0 || (blockFront == 6 && block != 6)) {
						addFace(block == 6 && topBlock <= 0 ? WATER_FRONT_FACE : FRONT_FACE, x, y, z, block - 1);
					}

					int blockBack = chunk.getBlock(x, y, z - 1);
					if (blockBack == -1) {
						blockBack = backChunk.getBlock(x, y, Chunk.CHUNK_WIDTH - 1);
					}

					if (blockBack == 0 || (blockBack == 6 && block != 6)) {
						addFace(block == 6 && topBlock <= 0 ? WATER_BACK_FACE : BACK_FACE, x, y, z, block - 1);
					}

					int blockLeft = chunk.getBlock(x - 1, y, z);
					if (blockLeft == -1) {
						blockLeft = leftChunk.getBlock(Chunk.CHUNK_WIDTH - 1, y, z);
					}

					if (blockLeft == 0 || (blockLeft == 6 && block != 6)) {
						addFace(block == 6 && topBlock <= 0 ? WATER_LEFT_FACE : LEFT_FACE, x, y, z, block - 1);
					}

					int blockRight = chunk.getBlock(x + 1, y, z);
					if (blockRight == -1) {
						blockRight = rightChunk.getBlock(0, y, z);
					}

					if (blockRight == 0 || (blockRight == 6 && block != 6)) {
						addFace(block == 6 && topBlock <= 0 ? WATER_RIGHT_FACE : RIGHT_FACE, x, y, z, block - 1);
					}

					int blockTop = chunk.getBlock(x, y + 1, z);
					if (blockTop <= 0 || (blockTop == 6 && block != 6)) {
						addFace(block == 6 && topBlock <= 0 ? WATER_TOP_FACE : TOP_FACE, x, y, z, block - 1);
					}

					int blockBottom = chunk.getBlock(x, y - 1, z);
					if (blockBottom <= 0 || (blockBottom == 6 && block != 6)) {
						addFace(BOTTOM_FACE, x, y, z, block - 1);
					}
				}
			}
		}

		meshBuilder.generateIndices();
		if (chunk.getMesh() == null) chunk.setMesh(new ChunkMesh(chunk));
		chunk.getMesh().buildBuffers(meshBuilder);
		chunk.setGenerated(true).setIndexCount(meshBuilder.getIndicesIndex());
	}

	private void addFace(MeshFace face, float x, float y, float z, int texture) {

		int index = 0;
		for(int i = 0; i < 4; i++) {
			float xPosition = face.vertices[index++] + x;
			float yPosition = face.vertices[index++] + y;
			float zPosition = face.vertices[index++] + z;

			switch (i) {
				case 0:
					meshBuilder.uv(1, 0, texture);
					break;
				case 1:
					meshBuilder.uv(0, 0, texture);
					break;
				case 2:
					meshBuilder.uv(0, 1, texture);
					break;
				case 3:
					meshBuilder.uv(1, 1, texture);
					break;
			}

			meshBuilder.normal(face.normal);

			meshBuilder.vertex(xPosition, yPosition, zPosition);
		}
	}

	public List<Chunk> getForceQueue() {
		return forceQueue;
	}
}
