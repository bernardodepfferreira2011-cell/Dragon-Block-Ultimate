#version 150

in vec3 Position;
in vec4 Color;

out vec4 vColor;
out vec3 vNormalWorld;
out vec3 vViewDir;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

void main() {
    vColor = Color;

    vec4 viewPos = ModelViewMat * vec4(Position, 1.0);
    vNormalWorld = normalize(mat3(ModelViewMat) * Position);
    vViewDir = -viewPos.xyz;

    gl_Position = ProjMat * viewPos;
}
