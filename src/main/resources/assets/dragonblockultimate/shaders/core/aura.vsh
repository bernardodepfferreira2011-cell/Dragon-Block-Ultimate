#version 150

in vec3 Position;
in vec4 Color;
in vec3 Normal;

// Outputs para o fragment shader
out vec3 vNormalWorld;
out vec3 vViewDir;
out vec3 vWorldPos;
out float vLayer;         // qual camada (inner=0, outer=1)
out float vNoiseValue;    // noise para usar no frag
out float vFalloff;       // fade vertical
out float vAngular;       // forma angular da aura

// Matrizes
uniform mat4 modelMatrix;
uniform mat4 ProjMat;
uniform mat3 normalMatrix;

// Tempo e poder
uniform float time;
uniform float power;       // ki power normalizado 0.0 - 1.0
uniform float kiCharge;    // 0.0 = normal, 1.0 = carregando ki
uniform float attackState; // 0.0 = idle, 1.0 = atacando
uniform float transformation; // 0=base 1=SSJ 2=SSJ2 3=SSJ3 4=SSJ4 5=SSJBlue
uniform float auravar;     // escala da aura 0-1 (easing)
uniform float layer;       // 0.0 = camada interna, 1.0 = camada externa

// =============================================
// SIMPLEX NOISE 3D
// =============================================
vec3 mod289v3(vec3 x) { return x - floor(x * (1.0/289.0)) * 289.0; }
vec4 mod289v4(vec4 x) { return x - floor(x * (1.0/289.0)) * 289.0; }
vec4 permute(vec4 x) { return mod289v4(((x*34.0)+1.0)*x); }

float snoise(vec3 v) {
    const vec2 C = vec2(1.0/6.0, 1.0/3.0);
    const vec4 D = vec4(0.0, 0.5, 1.0, 2.0);
    vec3 i  = floor(v + dot(v, C.yyy));
    vec3 x0 = v - i + dot(i, C.xxx);
    vec3 g = step(x0.yzx, x0.xyz);
    vec3 l = 1.0 - g;
    vec3 i1 = min(g.xyz, l.zxy);
    vec3 i2 = max(g.xyz, l.zxy);
    vec3 x1 = x0 - i1 + C.xxx;
    vec3 x2 = x0 - i2 + C.yyy;
    vec3 x3 = x0 - D.yyy;
    i = mod289v3(i);
    vec4 p = permute(permute(permute(
        i.z + vec4(0.0, i1.z, i2.z, 1.0))
        + i.y + vec4(0.0, i1.y, i2.y, 1.0))
        + i.x + vec4(0.0, i1.x, i2.x, 1.0));
    float n_ = 0.142857142857;
    vec3 ns = D.wyz - D.xyz * n_;
    vec4 j = p - 49.0 * floor(p * ns.z * ns.z);
    vec4 x_ = floor(j * ns.z);
    vec4 y_ = floor(j - 7.0 * x_);
    vec4 x = x_ * ns.x + ns.yyyy;
    vec4 y = y_ * ns.x + ns.xxxx;
    vec4 h = 1.0 - abs(x) - abs(y);
    vec4 b0 = vec4(x.xy, y.xy);
    vec4 b1 = vec4(x.zw, y.zw);
    vec4 s0 = floor(b0)*2.0 + 1.0;
    vec4 s1 = floor(b1)*2.0 + 1.0;
    vec4 sh = -step(h, vec4(0.0));
    vec4 a0 = b0.xzyw + s0.xzyw*sh.xzyw;
    vec4 a1 = b1.xzyw + s1.xzyw*sh.xzyw;
    vec3 p0 = vec3(a0.xy, h.x);
    vec3 p1 = vec3(a0.zw, h.y);
    vec3 p2 = vec3(a1.xy, h.z);
    vec3 p3 = vec3(a1.zw, h.w);
    vec4 norm = inversesqrt(vec4(dot(p0,p0),dot(p1,p1),dot(p2,p2),dot(p3,p3)));
    p0 *= norm.x; p1 *= norm.y; p2 *= norm.z; p3 *= norm.w;
    vec4 m = max(0.6 - vec4(dot(x0,x0),dot(x1,x1),dot(x2,x2),dot(x3,x3)), 0.0);
    m = m * m;
    return 42.0 * dot(m*m, vec4(dot(p0,x0),dot(p1,x1),dot(p2,x2),dot(p3,x3)));
}

// FBM - Fractal Brownian Motion para ruído em camadas
float fbm(vec3 p) {
    float value = 0.0;
    float amplitude = 0.5;
    float frequency = 1.0;
    for (int i = 0; i < 4; i++) {
        value += amplitude * snoise(p * frequency);
        frequency *= 2.0;
        amplitude *= 0.5;
    }
    return value;
}

float triangleWave(float x) {
    return abs(fract(x * 0.5) * 2.0 - 1.0);
}

void main() {
    vec4 pos = vec4(Position, 1.0);

    // =========================================
    // PARÂMETROS POR CAMADA
    // =========================================
    float isOuter = layer; // 0=inner 1=outer
    float speedMult = mix(5.5, 3.5, isOuter);       // outer mais lento
    float ampMult   = mix(0.7, 1.4, isOuter);        // outer mais largo
    float freqY     = mix(2.0, 1.2, isOuter);
    float scaleMult = mix(1.0, 1.35, isOuter);       // outer maior

    // =========================================
    // POWER MODIFICA VELOCIDADE E AMPLITUDE
    // =========================================
    float powerBoost = 1.0 + power * 2.5;
    float chargeBoost = 1.0 + kiCharge * 3.0; // ki charge expande muito
    float attackBoost = 1.0 + attackState * 1.5;

    // =========================================
    // NOISE MULTICAMADA PARA ENERGIA VIVA
    // =========================================
    // Camada 1: ondas verticais + angular (como original mas com FBM)
    float angle = atan(Position.z, Position.x);
    float angularWave = triangleWave(angle * 2.0 + time * 0.3);
    float vertWave = triangleWave((Position.y * freqY) - (time * speedMult * powerBoost));

    // Camada 2: noise 3D lento para "quebrar" a forma
    vec3 noiseCoord1 = Position * 3.0;
    noiseCoord1.y -= time * 1.2;
    float noise1 = snoise(noiseCoord1) * 0.5 + 0.5;

    // Camada 3: FBM rápido para energia frenética em SSJ
    vec3 noiseCoord2 = Position * 6.0;
    noiseCoord2.y -= time * 2.5;
    float noise2 = fbm(noiseCoord2) * 0.5 + 0.5;

    // Mistura de camadas de noise baseado em transformação
    float noiseBlend = clamp(transformation / 4.0, 0.0, 1.0);
    float combinedNoise = mix(noise1, mix(noise1, noise2, noiseBlend), 0.6);

    // =========================================
    // FLICKER: pulsação rápida controlada por poder
    // =========================================
    float flicker = 1.0 + sin(time * 25.0 * powerBoost) * 0.05 * power;
    float pulse   = 1.0 + sin(time * 6.0) * 0.1; // pulsação suave
    float chargePulse = 1.0 + sin(time * 15.0) * 0.2 * kiCharge; // pulso ao carregar

    // =========================================
    // FALLOFF VERTICAL (fade em cima e embaixo)
    // =========================================
    float expansionY = 2.0 + power * 1.0 + kiCharge * 2.0;
    float falloff = smoothstep(-1.8, 0.0, Position.y) *
                    (1.0 - smoothstep(0.0, expansionY, Position.y));

    // =========================================
    // DISPLACEMENT FINAL
    // =========================================
    float totalWave = vertWave * angularWave * combinedNoise;
    float amp = ampMult * powerBoost * chargeBoost * attackBoost * flicker * pulse * chargePulse;

    vec3 dir = normalize(pos.xyz);
    vec3 upDir = vec3(0.0, 1.0, 0.0);
    vec3 finalDir = normalize(mix(dir, upDir, 0.45));

    // Easing da aura (smooth cubic - igual ao original)
    float easedVar = auravar * auravar * (3.0 - 2.0 * auravar);

    // Escala XZ da aura por camada e poder
    pos.xz *= easedVar * scaleMult * (1.0 + power * 0.3 + kiCharge * 0.5);

    // Displacement volumétrico
    pos.xyz += finalDir * (totalWave * amp * falloff) * pow(auravar, 5.0);

    // Expansão de "onda de choque" ao atacar
    float shockwave = attackState * sin(time * 20.0) * 0.15 * falloff;
    pos.xz += dir.xz * shockwave;

    // =========================================
    // OUTPUTS
    // =========================================
    vNoiseValue = combinedNoise;
    vFalloff    = falloff;
    vAngular    = angularWave;
    vLayer      = layer;

    vec4 viewPos  = modelMatrix * vec4(pos.xyz, 1.0);
    vec4 worldPos = modelMatrix * vec4(Position, 1.0);

    vNormalWorld = normalize(normalMatrix * pos.xyz);
    vViewDir     = -viewPos.xyz;
    vWorldPos    = worldPos.xyz;

    gl_Position = ProjMat * viewPos;
}
