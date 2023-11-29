package wtf.monsoon.client.util.ui;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.ARBShaderObjects;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

@Getter
public class Shader {
    private final int fragID, vertID, program;

    private final Map<String, Integer> uniformLocationMap = new HashMap<>();

    public Shader(ResourceLocation fragment) {
        vertID = createShader(readResourceLocation(new ResourceLocation("monsoon/shader/vertex.vert")), GL_VERTEX_SHADER);
        fragID = createShader(readResourceLocation(fragment), GL_FRAGMENT_SHADER);

        if (vertID != 0 && fragID != 0) {
            program = ARBShaderObjects.glCreateProgramObjectARB();

            if (program != 0) {
                ARBShaderObjects.glAttachObjectARB(program, vertID);
                ARBShaderObjects.glAttachObjectARB(program, fragID);
                ARBShaderObjects.glLinkProgramARB(program);
                ARBShaderObjects.glValidateProgramARB(program);
            }
        } else {
            program = -1;
        }
    }

    public void init() {
        glUseProgram(getProgram());
    }

    public void bind(float x, float y, float w, float h) {
        glBegin(GL_QUADS);

        glTexCoord2f(0f, 0f);
        glVertex2f(x, y);
        glTexCoord2f(0f, 1f);
        glVertex2f(x, y + h);
        glTexCoord2f(1f, 1f);
        glVertex2f(x + w, y + h);
        glTexCoord2f(1f, 0f);
        glVertex2f(x + w, y);

        glEnd();
    }

    public void finish() {
        glUseProgram(0);
    }

    public void setupUniform(final String uniform) {
        this.uniformLocationMap.put(uniform, glGetUniformLocation(this.program, uniform));
    }

    public int getUniform(final String uniform) {
        return this.uniformLocationMap.get(uniform);
    }

    String readResourceLocation(ResourceLocation loc) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Minecraft.getMinecraft().getResourceManager().getResource(loc).getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null)
                stringBuilder.append(line).append('\n');

        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    private int createShader(String source, int type) {
        int shader = 0;

        try {
            shader = ARBShaderObjects.glCreateShaderObjectARB(type);

            if (shader != 0) {
                ARBShaderObjects.glShaderSourceARB(shader, source);
                ARBShaderObjects.glCompileShaderARB(shader);

                if (ARBShaderObjects.glGetObjectParameteriARB(shader, 35713) == 0) {
                    throw new RuntimeException("Error creating shader: " + ARBShaderObjects.glGetInfoLogARB(shader, ARBShaderObjects.glGetObjectParameteriARB(shader, 35716)));
                }

                return shader;
            } else {
                return 0;
            }
        } catch (Exception e) {
            ARBShaderObjects.glDeleteObjectARB(shader);
            e.printStackTrace();
            throw e;
        }
    }
}