#version 150

in vec3 vNormalWorld;
in vec3 vViewDir;
in vec3 vWorldPos;
in float vLayer;
in float vNoiseValue;
in float vFalloff;
in float vAngular;

out vec4 fragData[2];

// Cores base por camada
uniform vec3 colorInner;    // cor central (mais brilhante)
uniform vec3 colorOuter;    // cor da borda
uniform vec3 colorCore;     // cor do núcleo quase branco

// Cores por transformação (interpoladas em Java)
uniform vec3 transformColor1; // cor primária da forma
uniform vec3 transformColor2; // cor secundária / borda

// Parâmetros de intensidade
uniform float alp1;          // alpha inner
uniform float alp2;          // alpha outer
uniform float power;         // power level 0-1
uniform float kiCharge;      // carregando ki
uniform float attackState;   // estado de ataque
uniform float transformation; // 0=base ... 5=blue
uniform float time;
uniform float divis;

// =============================================
// FUNÇÕES UTILITÁRIAS
// =============================================
float hash21(vec2 p) {
    p = fract(p * vec2(234.34, 435.345));
    p += dot(p, p + 34.23);
    return fract(p.x * p.y);
}

// Noise 2D simples para distorção do frag
float noise2D(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    vec2 u = f * f * (3.0 - 2.0 * f);
    return mix(
        mix(hash21(i), hash21(i + vec2(1,0)), u.x),
        mix(hash21(i + vec2(0,1)), hash21(i + vec2(1,1)), u.x),
        u.y
    );
}

void main() {
    vec3 N = normalize(vNormalWorld);
    vec3 V = normalize(vViewDir);

    // =========================================
    // FACING RATIO (fresnel / rim)
    // =========================================
    float facingRaw = dot(V, N);
    if (facingRaw < 0.0) N = -N;
    float facingAbs  = abs(dot(V, N));
    float edgeFactor = 1.0 - facingAbs;

    // Fresnel rim mais forte nas bordas
    float rimPower = mix(2.0, 4.0 + power * 3.0, vLayer);
    float rim = pow(edgeFactor, rimPower) / divis;
    float blendFactor = clamp(rim, 0.0, 1.0);

    // =========================================
    // PULSAÇÃO TEMPORAL
    // =========================================
    float slowPulse  = sin(time * 3.0) * 0.5 + 0.5;             // pulsação lenta
    float fastPulse  = sin(time * 12.0 * (1.0 + power)) * 0.5 + 0.5; // pulsação rápida
    float chargePulse= sin(time * 20.0) * 0.5 + 0.5;            // ao carregar ki
    float flicker    = 1.0 + sin(time * 50.0) * 0.03 * power;   // micro flicker

    // Combina pulsações
    float pulse = mix(slowPulse, fastPulse, power * 0.6);
    pulse = mix(pulse, chargePulse, kiCharge * 0.7);
    pulse *= flicker;

    // =========================================
    // DISTORÇÃO DE ENERGIA (heat distortion simulada)
    // =========================================
    vec2 distCoord = vWorldPos.xy * 2.0 + vec2(time * 0.5, time * 0.3);
    float distortion = noise2D(distCoord) * 0.08 * power;
    blendFactor = clamp(blendFactor + distortion, 0.0, 1.0);

    // =========================================
    // COR DINÂMICA POR TRANSFORMAÇÃO
    // Interpolação entre cores base e cores da forma
    // =========================================
    float transBlend = clamp(transformation / 5.0, 0.0, 1.0);
    vec3 dynInner = mix(colorInner, transformColor1, transBlend);
    vec3 dynOuter = mix(colorOuter, transformColor2, transBlend);

    // Camada interna fica mais "branca/quente" no núcleo
    float coreBlend = (1.0 - blendFactor) * (1.0 - vLayer);
    vec3 finalColor = mix(dynOuter, dynInner, blendFactor);
    finalColor = mix(finalColor, colorCore, coreBlend * 0.6);

    // =========================================
    // NOISE PROCEDURAL QUEBRA A FORMA LISA
    // =========================================
    // Adiciona variação de cor pelo noise do vertex
    float noiseMod = vNoiseValue * 0.3 * power;
    finalColor = mix(finalColor, dynInner, noiseMod);

    // =========================================
    // BRILHO EMISSIVO FORTE
    // =========================================
    float emissiveBoost = 1.0 + power * 3.0 + kiCharge * 4.0 + attackState * 2.0;
    emissiveBoost *= pulse;
    finalColor *= emissiveBoost;

    // =========================================
    // ALPHA DINÂMICO
    // =========================================
    float alpha = mix(alp1, alp2, blendFactor);

    // Reduz alpha no interior (face traseira)
    if (facingRaw < 0.0) alpha = 0.01;

    // Boost de alpha ao carregar ki ou atacar
    alpha *= mix(1.0, 1.5, kiCharge);
    alpha *= mix(1.0, 1.3, attackState);
    alpha *= pulse * 0.7 + 0.3; // não zera nunca

    // Fade pelo falloff vertical
    alpha *= mix(1.0, vFalloff, 0.3);

    alpha = clamp(alpha, 0.0, 1.0);

    // =========================================
    // RIM LIGHT: contorno destacado
    // =========================================
    float rimLight = pow(1.0 - facingAbs, 5.0) * (0.5 + power);
    finalColor += dynOuter * rimLight * pulse;

    // =========================================
    // BLOOM PASS (fragData[1])
    // =========================================
    // Bloom mais forte no núcleo, fade nas bordas
    float bloomIntensity = (1.0 - blendFactor) * power * emissiveBoost;
    bloomIntensity += rimLight * 0.5;
    bloomIntensity = clamp(bloomIntensity, 0.0, 1.0);

    vec3 bloomColor = mix(dynInner, colorCore, 0.7) * bloomIntensity;

    fragData[0] = vec4(finalColor, alpha);
    fragData[1] = vec4(bloomColor, alpha * bloomIntensity);
}
