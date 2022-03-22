package me.kikopt.voxelgame.engine;

import org.joml.Vector3f;
import org.joml.Vector3i;

public class Light {

	private Vector3i position;
	private Vector3f color;

	public Light(Vector3i position, Vector3f color) {
		this.position = position;
		this.color = color;
	}

	public Vector3i getPosition() {
		return position;
	}

	public void setPosition(Vector3i position) {
		this.position = position;
	}

	public Vector3f getColor() {
		return color;
	}

	public void setColor(Vector3f color) {
		this.color = color;
	}
}
