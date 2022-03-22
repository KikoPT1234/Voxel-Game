package me.kikopt.voxelgame.engine;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class World {

	public static final Vector3f SUN_DIRECTION = new Vector3f(1f, 1f, 0);

	private final Map<Vector3i, Chunk> chunks;
	private Vector3f sunDirection;

	public World(Map<Vector3i, Chunk> chunks, Vector3f sunDirection) {
		this.chunks = chunks;
		this.sunDirection = sunDirection;
	}

	public World(Vector3f sunDirection) {
		this(new ConcurrentHashMap<>(), sunDirection);
	}

	public World() {
		this(SUN_DIRECTION);
	}

	public Map<Vector3i, Chunk> getChunks() {
		return chunks;
	}

	public @Nullable Chunk getChunk(int gridX, int gridY, int gridZ) {
		return getChunk(new Vector3i(gridX, gridY, gridZ));
	}

	public @Nullable Chunk getChunk(@NotNull Vector3i gridPosition) {
		return chunks.get(gridPosition);
	}

	public Vector3f getSunDirection() {
		return sunDirection;
	}

	public World setSunDirection(Vector3f sunDirection) {
		this.sunDirection = sunDirection;
		return this;
	}
}
