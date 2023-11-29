#version 120

uniform vec2 size;
uniform vec4 color1, color2, color3, color4;
uniform float radius, thickness, alpha;

#define NOISE 0.00196078431

vec3 createGradient(vec2 coords, vec3 color1, vec3 color2, vec3 color3, vec3 color4) {
    vec3 color = mix(mix(color1.rgb, color2.rgb, coords.y), mix(color3.rgb, color4.rgb, coords.y), coords.x);
    color += mix(NOISE, -NOISE, fract(sin(dot(coords.xy, vec2(12.9898, 78.233))) * 43758.5453));
    return color;
}

float round(vec2 center, vec2 size, float radius) {
    return length(max(abs(center) - size + radius, 0.0)) - radius;
}

void main() {
    float roundedRect = round((gl_TexCoord[0].xy * size) - (size / 2.0), size / 2.0, radius);
    float smoothedRect = (1.0 - smoothstep(-1.0, 1.0, roundedRect)) * alpha;

    float rounded = round((gl_TexCoord[0].xy * size) - (size / 2.0), size / 2.0, radius);
    float smoothed = ((smoothstep(-1.0, 1.0, rounded + thickness)) * alpha) * smoothedRect;

    gl_FragColor = vec4(createGradient(gl_TexCoord[0].xy, color1.rgb, color2.rgb, color3.rgb, color4.rgb), smoothed);
}