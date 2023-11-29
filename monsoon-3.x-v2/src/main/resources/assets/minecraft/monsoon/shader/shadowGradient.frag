#version 120

uniform vec2 size;
uniform vec4 color1, color2, color3, color4;
uniform float radius, alpha;
uniform float edgeSoftness;
uniform float shadowSoftness;
uniform int inner;

#define NOISE .5/255.0

float roundSDF(vec2 p, vec2 b, float r) {
    return length(max(abs(p) - b, 0.0)) - r;
}

vec3 createGradient(vec2 coords, vec3 color1, vec3 color2, vec3 color3, vec3 color4){
    vec3 color = mix(mix(color1.rgb, color2.rgb, coords.y), mix(color3.rgb, color4.rgb, coords.y), coords.x);
    color += mix(NOISE, -NOISE, fract(sin(dot(coords.xy, vec2(12.9898, 78.233))) * 43758.5453));
    return color;
}

void main() {
    vec2 st = gl_TexCoord[0].st;
    vec2 rectHalf = size * 0.5;

    float smoothedAlpha = (1.0 - smoothstep(0.0, 1.0, roundSDF(rectHalf - (st * size), rectHalf - radius - 1.0, radius))) * alpha;
    float shadowAlpha = 1.0 - smoothstep(-shadowSoftness, shadowSoftness, roundSDF(rectHalf - (st * size), rectHalf - radius - 1.0, radius));

    vec4 finalColor;

    if (inner == 1) {
        finalColor = mix(vec4(createGradient(st, color1.rgb, color2.rgb, color3.rgb, color4.rgb), 1.0), vec4(0.0, 0.0, 0.0, 0.0), shadowAlpha - smoothedAlpha);
    } else {
        finalColor = vec4(createGradient(st, color1.rgb, color2.rgb, color3.rgb, color4.rgb), shadowAlpha - smoothedAlpha);
    }

    gl_FragColor = finalColor;
}
