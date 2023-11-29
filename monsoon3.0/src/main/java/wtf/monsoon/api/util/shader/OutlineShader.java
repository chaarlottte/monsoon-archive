package wtf.monsoon.api.util.shader;


import net.minecraft.client.Minecraft;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;

public class OutlineShader {

    private int program;

    public static final String VERTEX_SHADER =
            "#version 120 \n" +
                    "\n" +
                    "void main() {\n" +
                    "    gl_TexCoord[0] = gl_MultiTexCoord0;\n" +
                    "    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;\n" +
                    "}";

    private Color colour = new Color(0, 0, 0);
    private float width = 1F;
    private int fill = 0;
    private int outline = 1;

    private final Map<String, Integer> uniformLocationMap;

    public OutlineShader() {
        uniformLocationMap = new HashMap<>();
        this.program = glCreateProgram();

        // Attach vertex & fragment shader to the program
        glAttachShader(this.program, createShader(VERTEX_SHADER, GL_VERTEX_SHADER));
        glAttachShader(this.program, createShader(OUTLINE_FRAG_SHADER, GL_FRAGMENT_SHADER));
        // Link the program
        glLinkProgram(this.program);
        // Check if linkage was a success
        final int status = glGetProgrami(this.program, GL_LINK_STATUS);
        // Check is status is a null ptr
        if (status == 0) {
            // Invalidate if error
            this.program = -1;
            return;
        }

        this.setupUniforms();
    }

    private static final String OUTLINE_FRAG_SHADER =
            "#version 120\n" +
                    "\n" +
                    "/*\n" +
                    " * centerCol is the pixel colour, we use rhis to determine whether we are drawing over an entity\n" +
                    " * Uniforms are values we can set externally\n" +
                    " * gl_FragColor is the pixels final colour\n" +
                    " * gl_TexCoord is the texture coordinate\n" +
                    "*/\n" +
                    "\n" +
                    "uniform sampler2D texture;\n" +
                    "uniform vec2 resolution;\n" +
                    "\n" +
                    "uniform vec4 colour;\n" +
                    "uniform float width;\n" +
                    "\n" +
                    "uniform int fill;\n" +
                    "uniform int outline;\n" +
                    "\n" +
                    "void main() {\n" +
                    "    vec4 centerCol = texture2D(texture, gl_TexCoord[0].xy);\n" +
                    "\n" +
                    "    if (centerCol.a > 0) {\n" +
                    "        if (fill == 1) {\n" +
                    "            gl_FragColor = vec4(colour.x, colour.y, colour.z, 0.5F);\n" +
                    "        } else {\n" +
                    "            gl_FragColor = vec4(0, 0, 0, 0);\n" +
                    "        }\n" +
                    "    } else if (outline == 1) {\n" +
                    "        float alpha = 0.0F;\n" +
                    "\n" +
                    "        for (float x = -width; x <= width; x++) {\n" +
                    "            for (float y = -width; y <= width; y++) {\n" +
                    "                vec4 pointColour = texture2D(texture, gl_TexCoord[0].xy + vec2(resolution.x * x, resolution.y * y));\n" +
                    "\n" +
                    "                if (pointColour.a > 0) {\n" +
                    "                    alpha = 1.0F;\n" +
                    "                }\n" +
                    "            }\n" +
                    "        }\n" +
                    "\n" +
                    "        gl_FragColor = vec4(colour.x, colour.y, colour.z, alpha);\n" +
                    "    }\n" +
                    "}";

    public void setColour(Color colour) {
        this.colour = colour;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setFill(int fill) {
        this.fill = fill;
    }

    public void setOutline(int outline) {
        this.outline = outline;
    }

    public void setupUniforms() {
        setupUniform("texture");
        setupUniform("resolution");

        setupUniform("colour");
        setupUniform("width");
        setupUniform("fill");
        setupUniform("outline");
    }

    public void updateUniforms() {
        glUniform1i(getUniform("texture"), 0);
        glUniform2f(getUniform("resolution"), 1F / Minecraft.getMinecraft().displayWidth, 1F / Minecraft.getMinecraft().displayHeight);

        glUniform4f(getUniform("colour"), colour.getRed() / 255f, colour.getGreen() / 255f, colour.getBlue() / 255f, colour.getAlpha() / 255f);
        glUniform1f(getUniform("width"), width);
        glUniform1i(getUniform("fill"), fill);
        glUniform1i(getUniform("outline"), outline);
    }

    private static int createShader(final String source, final int type) {
        final int shader = glCreateShader(type); // Create new shader of passed type (vertex or fragment)
        glShaderSource(shader, source); // Specify the source (the code of the shader)
        glCompileShader(shader);               // Compile the code

        final int status = glGetShaderi(shader, GL_COMPILE_STATUS); // Check if the compilation succeeded

        if (status == 0) { // Equivalent to checking invalid ptr
            return -1;
        }

        return shader;
    }

    public void startShader() {
        // Use shader program
        glUseProgram(this.program);
        this.updateUniforms();
    }

    public int getProgram() {
        return program;
    }

    public void setupUniform(final String uniform) {
        this.uniformLocationMap.put(uniform, glGetUniformLocation(this.program, uniform));
    }

    public int getUniform(final String uniform) {
        return this.uniformLocationMap.get(uniform);
    }
}