#version 120

// The original texture to apply the glow effect to
uniform sampler2D originalTexture;
uniform float width;

// The color of the glow
uniform vec3 glowColor;

// The size of the blur kernel
const int kernelSize = 9;

// The kernel of Gaussian weights to use for the blur filter
const float gaussianWeights[kernelSize] =
float[](0.05, 0.09, 0.12, 0.15, 0.16, 0.15, 0.12, 0.09, 0.05);

// The texture coordinates of the current fragment
varying vec2 texCoord;

void main()
{
    // Initialize the blur sum to 0
    vec4 blurSum = vec4(0.0);

    // Loop over the kernel and apply the blur filter
    for (int i = 0; i < kernelSize; i++) {
        // Calculate the offset for the current kernel element
        float offset = (float(i) - float(kernelSize - 1) / 2.0) / width;
        // Sample the original texture using the calculated offset
        vec4 samp = texture2D(originalTexture, texCoord + vec2(offset, 0.0));
        // Multiply the sample by the Gaussian weight and add it to the blur sum
        blurSum += samp * gaussianWeights[i];
    }

    // If the original texture sample is not transparent,
    // add the blurred texture to the original texture,
    // multiplied by the glow color
    if (texture2D(originalTexture, texCoord).a > 0.0) {
        gl_FragColor = texture2D(originalTexture, texCoord) + blurSum * vec4(glowColor, 1.0);
    } else {
        // If the original texture sample is transparent,
        // just output the original texture
        gl_FragColor = texture2D(originalTexture, texCoord);
    }
}