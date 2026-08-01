#version 150

in vec4 vColor;
in float vPulse;
in float vHeight;

uniform vec3 color1;
uniform vec3 color2;
uniform float intensity;
uniform float radius;

out vec4 fragColor;

void main() {
    float glow = smoothstep(0.0, 1.0, vHeight * radius) * intensity;
    vec3 base = mix(color1, color2, glow);
    vec3 pulse = base * (0.8 + 0.2 * vPulse);
    float alpha = clamp(0.35 + 0.65 * glow, 0.0, 1.0) * vPulse;
    fragColor = vec4(pulse, alpha);
}
