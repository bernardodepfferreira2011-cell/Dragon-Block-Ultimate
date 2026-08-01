#version 150

in vec3 Position;
in vec4 Color;

out vec4 vColor;
out float vPulse;
out float vHeight;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform float time;
uniform float intensity;

void main() {
    vec4 pos = vec4(Position, 1.0);
    vec4 viewPos = ModelViewMat * pos;

    vColor = Color;
    vHeight = clamp((Position.y + 1.0) * 0.5, 0.0, 1.0);
    vPulse = 0.5 + 0.5 * sin(time * 6.0 + Position.y * 2.0) * intensity;

    gl_Position = ProjMat * viewPos;
}
