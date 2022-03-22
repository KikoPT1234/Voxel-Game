package me.kikopt.voxelgame.engine;

import org.jetbrains.annotations.NotNull;
import org.joml.*;
import org.lwjgl.BufferUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.*;

public abstract class Shader {

	private final int program;
	private final int vertex;
	private final int fragment;

	private static final FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

	public Shader(String vertex, String fragment) {
		this.vertex = loadShader(vertex, GL_VERTEX_SHADER);
		this.fragment = loadShader(fragment, GL_FRAGMENT_SHADER);
		program = glCreateProgram();
		glAttachShader(program, this.vertex);
		glAttachShader(program, this.fragment);
		bindAttributes();
		glLinkProgram(program);
		glValidateProgram(program);
		getAllUniformLocations();
	}

	protected int getUniformLocation(String name) {
		return glGetUniformLocation(program, name);
	}

	protected abstract void getAllUniformLocations();

	public void start() {
		glUseProgram(program);
	}

	public void stop() {
		glUseProgram(0);
	}

	public void cleanup() {
		stop();
		glDetachShader(program, vertex);
		glDetachShader(program, fragment);
		glDeleteShader(vertex);
		glDeleteShader(fragment);
		glDeleteProgram(program);
	}

	protected void bindAttribute(int attribute, String variable) {
		glBindAttribLocation(program, attribute, variable);
	}

	protected abstract void bindAttributes();

	protected void loadInt(int location, int value) {
		glUniform1i(location, value);
	}

	protected void loadFloat(int location, float value) {
		glUniform1f(location, value);
	}

	protected void loadVector2D(int location, @NotNull Vector2f vector) {
		glUniform2f(location, vector.x, vector.y);
	}

	protected void loadVector3D(int location, @NotNull Vector3f vector) {
		glUniform3f(location, vector.x, vector.y, vector.z);
	}

	protected void loadVector4D(int location, @NotNull Vector4f vector) {
		glUniform4f(location, vector.x, vector.y, vector.z, vector.w);
	}

	protected void loadVector2D(int location, @NotNull Vector2i vector) {
		glUniform2f(location, vector.x, vector.y);
	}

	protected void loadVector3D(int location, @NotNull Vector3i vector) {
		glUniform3f(location, vector.x, vector.y, vector.z);
	}

	protected void loadVector4D(int location, @NotNull Vector4i vector) {
		glUniform4f(location, vector.x, vector.y, vector.z, vector.w);
	}

	protected void loadBoolean(int location, boolean value) {
		glUniform1i(location, value ? 1 : 0);
	}

	protected void loadMatrix(int location, @NotNull Matrix4f matrix) {
		glUniformMatrix4fv(location, false, matrix.get(matrixBuffer));
	}

	private static int loadShader(String file, int type) {
		StringBuilder shaderSource = new StringBuilder();

		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			while ((line = reader.readLine()) != null) {
				shaderSource.append(line).append("\n");
			}
			reader.close();
		} catch (IOException e) {
			System.err.println("Could not read file " + file);
			e.printStackTrace();
			System.exit(-1);
		}

		int shader = glCreateShader(type);
		glShaderSource(shader, shaderSource);
		glCompileShader(shader);

		if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
			System.out.println(glGetShaderInfoLog(shader, 500));
			System.err.println("Could not compile shader");
			System.exit(-1);
		}

		return shader;
	}

}
