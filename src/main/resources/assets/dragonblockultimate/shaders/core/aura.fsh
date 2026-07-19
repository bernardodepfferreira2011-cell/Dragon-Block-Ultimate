#version 150

in vec3 vNormalWorld;
in vec3 vViewDir;
in float vFlicker;
in float vHeightT;

uniform vec3 color1;
uniform vec3 color2;
uniform vec3 colorCore;
uniform float alp1;
uniform float alp2;
uniform float power;
uniform float divis;
uniform float intensity;
uniform float bloomStrength;

out vec4 fragColor;

void main() {
    vec3 N = normalize(vNormalWorld);
    vec3 V = normalize(vViewDir);

    float facingAbs = abs(dot(V, N));
    float edgeFactor = clamp(1.0 - facingAbs, 0.0, 1.0);
    edgeFactor = pow(edgeFactor, max(power, 0.1)) / max(divis, 0.01);
    float blendFactor = clamp(edgeFactor, 0.0, 1.0);

    vec3 color = mix(color1, color2, blendFactor);
    float alpha = mix(alp1, alp2, blendFactor);

    float coreMask = pow(facingAbs, 3.0) * clamp(intensity, 0.0, 1.0);
    color = mix(color, colorCore, clamp(coreMask * bloomStrength, 0.0, 0.85));
    alpha += coreMask * bloomStrength * 0.35;

    // Curva de densidade por altura: corpo principal (0..0.55 de vHeightT)
    // fica suave e estavel; a partir dai sobe forte ate saturar bem perto
    // da ponta -- e o padrao "corpo difuso, ponta vivida" da referencia,
    // em vez de densidade uniforme (ou pior, invertida) ao longo de tudo.
    float bodyDensity = 0.4;
    float tipBoost = smoothstep(0.55, 1.0, vHeightT) * 1.0;
    float verticalFade = bodyDensity + tipBoost;
    alpha *= verticalFade;

    color *= vFlicker;
    alpha *= vFlicker;

    fragColor = vec4(color, clamp(alpha, 0.0, 1.0));
}
