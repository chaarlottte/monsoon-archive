package wtf.monsoon.client.util.ui;

import net.minecraft.client.shader.Shader;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;

public class TextureUtil {
    public static int generate(int width, int height, StretchFilter stretchFilter, RepeatFilter repeatFilter) {
        int texture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texture);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, stretchFilter.filter);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, stretchFilter.filter);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, repeatFilter.filter);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, repeatFilter.filter);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer) null);
        return texture;
    }

    public enum StretchFilter {
        LINEAR(GL_LINEAR), NEAREST(GL_NEAREST);

        int filter;
        StretchFilter(int filter) {
            this.filter = filter;
        }
    }

    public enum RepeatFilter {
        REPEAT(GL_REPEAT), CLAMP(GL_CLAMP);

        int filter;
        RepeatFilter(int filter) {
            this.filter = filter;
        }
    }
}
