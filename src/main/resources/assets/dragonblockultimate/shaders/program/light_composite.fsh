#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D Bloom0;
uniform sampler2D Bloom1;
uniform sampler2D Bloom2;
uniform float BloomStrength;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec4 scene = texture(DiffuseSampler, texCoord);

    vec3 bloom0 = texture(Bloom0, texCoord).rgb;
    vec3 bloom1 = texture(Bloom1, texCoord).rgb;
    vec3 bloom2 = texture(Bloom2, texCoord).rgb;

    // Weighted multi-scale bloom composition.
    vec3 bloomSum = bloom0 * 0.60 + bloom1 * 0.35 + bloom2 * 0.25;
    vec3 combined = scene.rgb + bloomSum * BloomStrength;

    // Smooth compression preserves detail in bright regions.
    vec3 result = combined / (1.0 + max(combined - vec3(1.0), vec3(0.0)) * 0.45);

    fragColor = vec4(result, scene.a);
}
