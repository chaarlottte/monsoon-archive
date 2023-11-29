#version 120

uniform vec2 size;
uniform sampler2D texture;
uniform float radius, alpha;

float roundedBoxSDF(vec2 centerPos, vec2 size, float radius) {
    return length(max(abs(centerPos) -size, 0.)) - radius;
}

void main() {
    float distance = roundedBoxSDF((size * .5) - (gl_TexCoord[0].st * size), (size * .5) - radius - 1., radius);
    float smoothedAlpha =  (1.0-smoothstep(0.0, 2.0, distance)) * alpha;
    gl_FragColor = vec4(texture2D(texture, gl_TexCoord[0].st).rgb, smoothedAlpha);
}