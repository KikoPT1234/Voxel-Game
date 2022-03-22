package me.kikopt.voxelgame.engine;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.List;

public class Chunk {

	public static final int CHUNK_WIDTH = 16;
	public static final int CHUNK_HEIGHT = 256;
	public static final int CHUNK_VOLUME = CHUNK_WIDTH * CHUNK_WIDTH * CHUNK_HEIGHT;

	private int[][][] blocks;
	private Vector3i gridPosition;
	private Vector3i position;
	private final World world;
	private ChunkMesh mesh;
	private boolean isGenerated = false;
	private boolean isLoaded = false;
	private int indexCount;
	private boolean updated = false;
	private List<Vector3i> waterBlocks = new ArrayList<>();

	public Chunk(Vector3i position, World world) {
		this(position, new int[CHUNK_HEIGHT][CHUNK_WIDTH][CHUNK_WIDTH], world);
	}

	public Chunk(@NotNull Vector3i gridPosition, int[][][] blocks, World world) {
		this.gridPosition = gridPosition;
		position = new Vector3i(gridPosition.x * CHUNK_WIDTH, gridPosition.y * CHUNK_HEIGHT, gridPosition.z * CHUNK_WIDTH);
		this.blocks = blocks;
		this.world = world;
	}

	public int getBlock(int x, int y, int z) {
		if (x < 0 || x >= CHUNK_WIDTH) return -1;
		if (y < 0 || y >= CHUNK_HEIGHT) return -1;
		if (z < 0 || z >= CHUNK_WIDTH) return -1;

		return blocks[y][x][z];
	}

	public Chunk setBlock(int block, int x, int y, int z) {
		if (x < 0 || x >= CHUNK_WIDTH) return this;
		if (y < 0 || y >= CHUNK_HEIGHT) return this;
		if (z < 0 || z >= CHUNK_WIDTH) return this;

		blocks[y][x][z] = block;

		return this;
	}

	public Chunk setBlockUpdate(ChunkManager manager, int block, int x, int y, int z) {
		setBlock(block, x, y, z);

		mesh.unloadBuffers();
		manager.buildChunk(this, world);
		mesh.loadBuffers();

		return this;
	}

	public int[][][] getBlocks() {
		return blocks;
	}

	public void setBlocks(int[][][] blocks) {
		this.blocks = blocks;
	}

	public Vector3i getPosition() {
		return position;
	}

	public void setPosition(@NotNull Vector3i position) throws IllegalArgumentException {
		if (position.x % CHUNK_WIDTH != 0 || position.y % CHUNK_HEIGHT != 0 || position.z % CHUNK_WIDTH != 0) throw new IllegalArgumentException("Invalid position");
		this.position = position;
	}

	public Vector3i getGridPosition() {
		return gridPosition;
	}

	public void setGridPosition(Vector3i gridPosition) {
		this.gridPosition = gridPosition;
	}

	public World getWorld() {
		return world;
	}

	public ChunkMesh getMesh() {
		return mesh;
	}

	public Chunk setMesh(ChunkMesh mesh) {
		this.mesh = mesh;
		return this;
	}

	public Chunk setGenerated(boolean generated) {
		isGenerated = generated;
		return this;
	}

	public boolean isGenerated() {
		return isGenerated;
	}

	public boolean isLoaded() {
		return isLoaded;
	}

	public Chunk setLoaded(boolean loaded) {
		isLoaded = loaded;
		return this;
	}

	public int getIndexCount() {
		return indexCount;
	}

	public Chunk setIndexCount(int indexCount) {
		this.indexCount = indexCount;
		return this;
	}

	public boolean isUpdated() {
		return updated;
	}

	public Chunk setUpdated(boolean updated) {
		this.updated = updated;
		return this;
	}

	public List<Vector3i> getWaterBlocks() {
		return waterBlocks;
	}

	public void setWaterBlocks(List<Vector3i> waterBlocks) {
		this.waterBlocks = waterBlocks;
	}
}
