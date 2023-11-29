#version 120

uniform sampler2D texture;
uniform sampler2D mask;
uniform vec2 texelSize, direction;
uniform float radius;
uniform float weights[256];

#define offset texelSize * direction

void main() {
    vec3 blr = texture2D(texture, gl_TexCoord[0].st).rgb * weights[0];

    for (float f = 1.0; f <= radius; f++) {
        blr += texture2D(texture, gl_TexCoord[0].st + f * offset).rgb * (weights[int(abs(f))]);
        blr += texture2D(texture, gl_TexCoord[0].st - f * offset).rgb * (weights[int(abs(f))]);
    }

    gl_FragColor = vec4(blr, floor(texture2D(mask, texelSize).a));
}
