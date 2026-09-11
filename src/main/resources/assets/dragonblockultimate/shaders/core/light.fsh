#version 150

in vec4 vColor;

out vec4 fragColor;

void main() {
    vec3 lightColor = vColor.rgb * 1.5;
    fragColor = vec4(lightColor, vColor.a);
}
