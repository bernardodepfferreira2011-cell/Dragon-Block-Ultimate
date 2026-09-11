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
    vec3 bloom = texture(Bloom0, texCoord).rgb * 0.55;
    bloom += texture(Bloom1, texCoord).rgb * 0.30;
    bloom += texture(Bloom2, texCoord).rgb * 0.15;
    fragColor = vec4(scene.rgb + bloom * BloomStrength, scene.a);
}
