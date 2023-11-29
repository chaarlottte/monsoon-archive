#version 330

uniform vec2 iResolution;
uniform vec2 iChannelResolution;
uniform sampler2D tex;
uniform float blurStrength;

void main()
{
    vec2 uv = gl_FragCoord.xy / iResolution.xy;
    vec2 res = iChannelResolution.xy;

    float i = blurStrength;

    vec3 col = texture( tex, uv + vec2( i, i ) / res ).rgb;
    col += texture( tex, uv + vec2( i, -i ) / res ).rgb;
    col += texture( tex, uv + vec2( -i, i ) / res ).rgb;
    col += texture( tex, uv + vec2( -i, -i ) / res ).rgb;
    col /= 4.0;

    gl_FragColor = vec4( col, 1.0 );
}