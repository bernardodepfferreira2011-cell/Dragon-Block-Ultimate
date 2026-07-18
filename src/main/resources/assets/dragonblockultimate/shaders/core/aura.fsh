#version 150

in vec3 vNormalWorld;
in vec3 vViewDir;

uniform vec3 color1;
uniform vec3 color2;
uniform float alp1;
uniform float alp2;
uniform float power;
uniform float divis;

out vec4 fragColor;

void main() {
    vec3 N = normalize(vNormalWorld);
    vec3 V = normalize(vViewDir);

    // facingAbs: 1.0 = olhando direto pra normal (centro da forma),
    // 0.0 = de raspão (borda/silhueta). Sem o "if (facingRaw < 0) alpha = 0.01"
    // problemático de antes - aqui sempre usamos o valor absoluto.
    float facingAbs = abs(dot(V, N));
    float edgeFactor = clamp(1.0 - facingAbs, 0.0, 1.0);
    edgeFactor = pow(edgeFactor, max(power, 0.1)) / max(divis, 0.01);
    float blendFactor = clamp(edgeFactor, 0.0, 1.0);

    vec3 color = mix(color1, color2, blendFactor);
    float alpha = mix(alp1, alp2, blendFactor);

    fragColor = vec4(color, clamp(alpha, 0.0, 1.0));
}
