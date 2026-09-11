#version 150

uniform sampler2D DiffuseSampler;
uniform float Threshold;
uniform float Knee;
uniform float BloomStrength;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec4 source = texture(DiffuseSampler, texCoord);
    float brightness = max(max(source.r, source.g), source.b);
    float soft = clamp((brightness - Threshold + Knee) / max(Knee, 0.001), 0.0, 1.0);
    float hard = max(brightness - Threshold, 0.0);
    float mask = max(soft * soft * 0.5, hard);
    fragColor = vec4(source.rgb * mask * BloomStrength, source.a * mask);
}
