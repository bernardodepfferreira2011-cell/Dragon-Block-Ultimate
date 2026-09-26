#version 150

uniform sampler2D DiffuseSampler;
uniform float Radius;

in vec2 texCoord;
in vec2 sampleStep;
out vec4 fragColor;

void main() {
    float radiusScale = max(Radius, 1.0) / 5.0;
    vec4 result = texture(DiffuseSampler, texCoord) * 0.227027;
    result += texture(DiffuseSampler, texCoord + sampleStep * 1.384615 * radiusScale) * 0.316216;
    result += texture(DiffuseSampler, texCoord - sampleStep * 1.384615 * radiusScale) * 0.316216;
    result += texture(DiffuseSampler, texCoord + sampleStep * 3.230769 * radiusScale) * 0.070270;
    result += texture(DiffuseSampler, texCoord - sampleStep * 3.230769 * radiusScale) * 0.070270;
    fragColor = result;
}
