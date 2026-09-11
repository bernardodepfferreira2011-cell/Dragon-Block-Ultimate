#version 150

uniform sampler2D DiffuseSampler;
uniform float Radius;

in vec2 texCoord;
in vec2 sampleStep;
out vec4 fragColor;

void main() {
    vec4 result = texture(DiffuseSampler, texCoord) * 0.227027;
    result += texture(DiffuseSampler, texCoord + sampleStep * 1.384615) * 0.316216;
    result += texture(DiffuseSampler, texCoord - sampleStep * 1.384615) * 0.316216;
    result += texture(DiffuseSampler, texCoord + sampleStep * 3.230769) * 0.070270;
    result += texture(DiffuseSampler, texCoord - sampleStep * 3.230769) * 0.070270;
    fragColor = result;
}
