#version 120

uniform vec2 size;
uniform vec4 color;
uniform float radius, thickness;

float round(vec2 center, vec2 size, float radius) {
    return length(max(abs(center) - size + radius, 0.0)) - radius;
}

void main() {
    float roundedRect = round((gl_TexCoord[0].xy * size) - (size / 2.0), size / 2.0, radius);
    float smoothedRect = (1.0 - smoothstep(-1.0, 1.0, roundedRect)) * color.a;

    float rounded = round((gl_TexCoord[0].xy * size) - (size / 2.0), size / 2.0, radius);
    float smoothed = ((smoothstep(-1.0, 1.0, rounded + thickness)) * color.a) * smoothedRect;

    gl_FragColor = vec4(color.rgb, smoothed);
}