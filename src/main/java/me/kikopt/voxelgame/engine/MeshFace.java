package me.kikopt.voxelgame.engine;

import org.joml.Vector3i;

public class MeshFace {

	public final float[] vertices;

	public Vector3i normal;

	public MeshFace(float[] vertices, Vector3i normal) {
		this.vertices = vertices;
		this.normal = normal;
	}

}
