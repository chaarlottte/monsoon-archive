#version 120

uniform vec2 location, size;
uniform vec4 color;
uniform float radius;
uniform float edgeSoftness;
uniform float shadowSoftness;

float roundSDF(vec2 p, vec2 b, float r) {
    return length(max(abs(p) - b, 0.0)) - r;
}

void main() {
    vec2 rectHalf = size * 0.5;
    vec2 st = gl_FragCoord.xy - location - rectHalf;

    float smoothedAlpha =  (1.0 - smoothstep(0.0, 1.0, roundSDF(st, rectHalf - radius - 1.0, radius))) * color.a;
    float shadowAlpha = 1.0 - smoothstep(-shadowSoftness, shadowSoftness, roundSDF(st, rectHalf - radius - 1.0, radius));

    gl_FragColor = vec4(color.rgb, shadowAlpha - smoothedAlpha);
}
