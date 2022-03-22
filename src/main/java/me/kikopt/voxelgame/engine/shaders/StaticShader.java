package me.kikopt.voxelgame.engine.shaders;

import me.kikopt.voxelgame.engine.Camera;
import me.kikopt.voxelgame.engine.Light;
import me.kikopt.voxelgame.engine.Shader;
import me.kikopt.voxelgame.engine.Utils;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class StaticShader extends Shader {

	public static final String VERTEX_FILE = "src/main/java/me/kikopt/voxelgame/engine/shaders/vertex.glsl";
	public static final String FRAGMENT_FILE = "src/main/java/me/kikopt/voxelgame/engine/shaders/fragment.glsl";

	private int location_transformation;
	private int location_projection;
	private int location_view;
	private int location_sunDirection;
	private int location_fogColor;
	private int location_fogGradient;
	private int location_fogDensity;

	public StaticShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		bindAttribute(0, "position");
		bindAttribute(1, "textureCoordinates");
		bindAttribute(2, "normal");
	}

	@Override
	protected void getAllUniformLocations() {
		location_transformation = getUniformLocation("transformation");
		location_projection = getUniformLocation("projection");
		location_view = getUniformLocation("view");
		location_sunDirection = getUniformLocation("sunDirection");
		location_fogColor = getUniformLocation("fogColor");
		location_fogGradient = getUniformLocation("gradient");
		location_fogDensity = getUniformLocation("density");
	}

	public void loadTransformationMatrix(Matrix4f matrix) {
		loadMatrix(location_transformation, matrix);
	}

	public void loadProjectionMatrix(Matrix4f matrix) {
		loadMatrix(location_projection, matrix);
	}

	public void loadViewMatrix(Camera camera) {
		loadMatrix(location_view, Utils.createViewMatrix(camera));
	}

//	public void loadLight(@NotNull Light light) {
//		loadVector3D(location_lightPosition, light.getPosition());
//		loadVector3D(location_lightColor, light.getColor());
//	}

	public void loadSunDirection(Vector3f sunDirection) {
		loadVector3D(location_sunDirection, sunDirection);
	}

	public void loadFog(Vector3f color, float density, float gradient) {
		loadVector3D(location_fogColor, color);
		loadFloat(location_fogDensity, density);
		loadFloat(location_fogGradient, gradient);
	}
}
