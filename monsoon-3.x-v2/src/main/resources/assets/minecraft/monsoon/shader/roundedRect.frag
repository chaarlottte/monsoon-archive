#version 120

uniform vec2 size;
uniform vec4 color;
uniform float radius;

float round(vec2 center, vec2 size, float radius) {
    return length(max(abs(center) - size + radius, 0.0)) - radius;
}

void main() {
    float rounded = round((gl_TexCoord[0].xy * size) - (size / 2.0), size / 2.0, radius);
    float smoothed = (1.0 - smoothstep(-1.0, 0.0, rounded)) * color.a;

    vec4 quadColor = mix(vec4(0.0, 0.0, 0.0, 0.0), vec4(color.rgb, smoothed), smoothed);

    gl_FragColor = quadColor;
}