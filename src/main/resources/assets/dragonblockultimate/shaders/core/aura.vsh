#version 150

in vec3 Position;
in vec4 Color;

out vec3 vNormalWorld;
out vec3 vViewDir;
out float vFlicker;
out float vHeightT;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

uniform float time;
uniform float intensity;

void main() {
    vec4 pos = vec4(Position, 1.0);

    vec4 viewPos = ModelViewMat * pos;

    vNormalWorld = normalize(mat3(ModelViewMat) * Position);
    vViewDir = -viewPos.xyz;

    vFlicker = 0.85 + 0.15 * sin(time * 18.0 + Position.y * 3.0) * clamp(intensity - 0.3, 0.0, 1.0);

    vHeightT = Color.a;

    gl_Position = ProjMat * viewPos;
}
