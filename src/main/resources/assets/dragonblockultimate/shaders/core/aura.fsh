#version 150

in vec4 vColor;
in vec3 vNormalWorld;
in vec3 vViewDir;

uniform float power;
uniform float divis;

out vec4 fragColor;

void main() {
    vec3 N = normalize(vNormalWorld);
    vec3 V = normalize(vViewDir);
    float facingAbs = abs(dot(V, N));
    float edgeFactor = clamp(1.0 - facingAbs, 0.0, 1.0);
    edgeFactor = pow(edgeFactor, max(power, 0.1)) / max(divis, 0.01);
    float rim = clamp(edgeFactor, 0.0, 1.0);

    vec3 color = vColor.rgb * (1.0 + rim * 0.5);
    float alpha = vColor.a * mix(1.0, 0.4, rim);

    fragColor = vec4(color, clamp(alpha, 0.0, 1.0));
}
