#version 120

uniform vec2 size;
uniform vec4 color1, color2, color3, color4;
uniform float radius, alpha;

#define NOISE 0.00196078431

float round(vec2 center, vec2 size, float radius) {
    return length(max(abs(center) - size + radius, 0.0)) - radius;
}

vec3 createGradient(vec2 coords, vec3 color1, vec3 color2, vec3 color3, vec3 color4){
    vec3 color = mix(mix(color1.rgb, color2.rgb, coords.y), mix(color3.rgb, color4.rgb, coords.y), coords.x);
    color += mix(NOISE, -NOISE, fract(sin(dot(coords.xy, vec2(12.9898, 78.233))) * 43758.5453));
    return color;
}

void main() {
    float rounded = round((gl_TexCoord[0].xy * size) - (size / 2.0), size / 2.0, radius);
    float smoothed = (1.0 - smoothstep(-1.0, 0.0, rounded)) * alpha;

    vec4 quadColor = mix(vec4(0.0, 0.0, 0.0, 0.0), vec4(createGradient(gl_TexCoord[0].xy, color1.rgb, color2.rgb, color3.rgb, color4.rgb), smoothed), smoothed);

    gl_FragColor = quadColor;
}