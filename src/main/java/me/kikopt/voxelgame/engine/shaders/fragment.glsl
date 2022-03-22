#version 400

in vec3 pass_textureCoordinates;
in vec3 surfaceNormal;
in float visibility;

out vec4 out_Color;

uniform sampler2DArray textureSampler;
uniform vec3 sunDirection;
uniform mat4 view;
uniform mat4 transformation;
uniform vec3 fogColor;

void main() {

	float nDot1 = dot(surfaceNormal, sunDirection);
	float brightness = max(nDot1, 0.2);
	vec3 diffuse = brightness * vec3(1.0, 1.0, 1.0);

	out_Color = vec4(diffuse, 1.0) * texture(textureSampler, pass_textureCoordinates);
	out_Color = mix(vec4(fogColor, 1.0), out_Color, visibility);
}