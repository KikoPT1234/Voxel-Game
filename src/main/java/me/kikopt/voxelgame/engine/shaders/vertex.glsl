#version 400

in vec3 position;
in vec3 textureCoordinates;
in vec3 normal;

out vec3 pass_textureCoordinates;
out vec3 surfaceNormal;
out float visibility;

uniform mat4 transformation;
uniform mat4 projection;
uniform mat4 view;
uniform float gradient;
uniform float density;

void main() {

	vec4 worldPosition = transformation * vec4(position, 1.0);
	vec4 positionRelativeToCam = view * worldPosition;
	gl_Position = projection * positionRelativeToCam;
	pass_textureCoordinates = textureCoordinates;

	surfaceNormal = (transformation * vec4(normal, 0.0)).xyz;

	float distance = length(positionRelativeToCam.xyz);
	visibility = exp(-pow((distance * density), gradient));
	visibility = clamp(visibility, 0.0, 1.0);

}