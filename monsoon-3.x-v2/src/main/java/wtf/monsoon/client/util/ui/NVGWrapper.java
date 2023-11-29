package wtf.monsoon.client.util.ui;

import lombok.Getter;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVGGL2;
import org.lwjgl.opengl.Display;
import wtf.monsoon.client.util.buffer.BufferUtil;

import java.awt.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;

import static org.lwjgl.nanovg.NanoVGGL2.*;
import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.opengl.GL11.*;
import static wtf.monsoon.backend.MiscKt.colourise;

/**
 * @author surge
 * @since 10/02/2023
 */
public class NVGWrapper {

    public long vg;
    
    private final String[] fontIds = {
            "black",
            "bold",
            "ebold",
            "elight",
            "light",
            "medium",
            "regular",
            "sbold",
            "thin",
            "comic_sans",
            "comic_sans_bold",
            "entypo",
            "greycliff",
            "menu_icons",
            "minecraft.otf",
            "product_sans",
            "product_sans_bold",
            "superhero",
            "ubuntuwu",
            "category",
            "category2"
    };

    private int[] images;

    private final HashMap<String, ByteBuffer> fonts = new HashMap<>();
    private final HashMap<String, Integer> textures = new HashMap<>();
    private final HashMap<Integer, Integer> glTextures = new HashMap<>();

    public void init(List<String> textures) {
        this.vg = nvgCreate(NVG_ANTIALIAS);

        for (String font : fontIds) {
            try {
                ByteBuffer buffer = BufferUtil.getResourceBytes("font/" + font + (font.endsWith(".otf") ? "" : ".ttf"), 1024);

                nvgCreateFontMem(vg, font, buffer, 0);

                fonts.put(font, buffer);
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
        }

        images = new int[textures.size()];

        int i = 0;
        for (String texture : textures) {
            try {
                images[i] = nvgCreateImageMem(vg, NVG_IMAGE_NEAREST, BufferUtil.getResourceBytes("textures/" + texture + ".png", 512));
                this.textures.put(texture, images[i]);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            i++;
        }
    }

    public void terminate() {
        for (int image : images) {
            nvgDeleteImage(vg, image);
        }

        nvgDelete(vg);
    }

    public void beginFrame() {
        nvgBeginFrame(vg, Display.getWidth(), Display.getHeight(), 1f);
    }

    public void endFrame() {
        nvgEndFrame(vg);
    }

    public void glTexture(float x, float y, float width, float height, float radius, int texture) {
        int reference = 0;

        if(glTextures.containsKey(texture))
            reference = glTextures.get(texture);
        else {
            reference = NanoVGGL2.nvglCreateImageFromHandle(vg, texture, (int) width, -(int) height, 0);
            if (reference == 0L) throw new RuntimeException("Unable to create texture!");

            glTextures.put(texture, reference);
        }
        nvgImageSize(vg,reference,new int[]{(int) width}, new int[]{-(int) height});
        NVGPaint p = NVGPaint.calloc();
        p = nvgImagePattern(vg, x, y+height, width, -height, 0, reference, 1f, p);

        nvgBeginPath(vg);
        if(radius > 0)
            nvgRoundedRect(vg, x, y+height, width, -height, radius);
        else
            nvgRect(vg, x, y+height, width, -height);
        nvgFillPaint(vg, p);
        nvgFill(vg);
        nvgClosePath(vg);
        p.free();
    }
    
    public void round(float x, float y, float width, float height, float radius ,Color c) {
        NVGColor colourised = colourise(c);

        nvgBeginPath(vg);
        nvgRoundedRect(vg, x, y, width, height, radius);
        nvgFillColor(vg, colourised);
        nvgFill(vg);
        nvgClosePath(vg);

        colourised.free();
    }

    public void rect(float x, float y, float width, float height, Color colour) {
        NVGColor colourised = colourise(colour);

        nvgBeginPath(vg);
        nvgRect(vg, x, y, width, height);
        nvgFillColor(vg, colourised);
        nvgFill(vg);
        nvgClosePath(vg);

        colourised.free();
    }

    public void circle(float x, float y, float radius, Color colour) {
        NVGColor colourised = colourise(colour);

        nvgBeginPath(vg);
        nvgCircle(vg, x, y, radius);
        nvgFillColor(vg, colourised);
        nvgFill(vg);
        nvgClosePath(vg);

        colourised.free();
    }

    public void line(float x, float y, float targetX, float targetY, float thickness, Color colour) {
        NVGColor colourised = colourise(colour);

        nvgSave(vg);
        nvgBeginPath(vg);
        nvgMoveTo(vg, x, y);
        nvgLineTo(vg, targetX, targetY);
        nvgStrokeColor(vg, colourised);
        nvgStrokeWidth(vg, thickness);
        nvgStroke(vg);
        nvgClosePath(vg);
        nvgRestore(vg);

        colourised.free();
    }

    public void lineGradient(float x, float y, float targetX, float targetY, float thickness, Color a, Color b) {
        NVGColor colourisedA = colourise(a);
        NVGColor colourisedB = colourise(b);
        NVGPaint paint = NVGPaint.calloc();

        nvgLinearGradient(vg, x, y, targetX, targetY, colourisedA, colourisedB, paint);

        nvgSave(vg);
        nvgBeginPath(vg);
        nvgMoveTo(vg, x, y);
        nvgLineTo(vg, x, y);
        nvgStrokePaint(vg, paint);
        nvgStrokeWidth(vg, thickness);
        nvgStroke(vg);
        nvgClosePath(vg);
        nvgRestore(vg);

        colourisedA.free();
        colourisedB.free();
        paint.free();
    }

    public void roundedLinearGradient(float x, float y, float width, float height, float radius, Color start, Color end) {
        NVGColor startColour = colourise(start);
        NVGColor endColour = colourise(end);

        nvgBeginPath(vg);
        NVGPaint gradient = NVGPaint.calloc();
        nvgLinearGradient(vg, x, y, x + width, y + height, startColour, endColour, gradient);
        nvgRoundedRect(vg, x, y, width, height, radius);
        nvgFillPaint(vg, gradient);
        nvgFill(vg);
        nvgClosePath(vg);

        startColour.free();
        endColour.free();
    }

    public void text(String text, float x, float y, String font, float size, Color colour) {
        text(text, x, y, font, size, colour, Alignment.LEFT_TOP);
    }

    public void text(String text, float x, float y, String font, float size, Color colour, float blur) {
        text(text, x, y, font, size, colour, Alignment.LEFT_TOP, blur);
    }

    public void text(String text, float x, float y, String font, float size, Color colour, Alignment alignment, float blur) {
        NVGColor colourised = colourise(colour);

        nvgBeginPath(vg);

        nvgFontFace(vg, font);
        nvgFontBlur(vg, blur);
        nvgFontSize(vg, size);
        nvgTextAlign(vg, alignment.getAlignment());
        nvgFillColor(vg, colourised);

        nvgText(vg, x, y, text);
        nvgFontBlur(vg, 0);

        nvgClosePath(vg);

        colourised.free();
    }

    public void text(String text, float x, float y, String font, float size, Color colour, Alignment alignment) {
        for (String line : text.replace("\r", "").replace("\t", "    ").split("\n")) {
            NVGColor colourised = colourise(colour);

            nvgBeginPath(vg);

            nvgFontFace(vg, font);
            nvgFontSize(vg, size);
            nvgTextAlign(vg, alignment.getAlignment());
            nvgFillColor(vg, colourised);

            nvgText(vg, x, y, line);

            nvgClosePath(vg);

            colourised.free();

            y += textHeight(font, size);
        }
    }

    public void scissor(float x, float y, float width, float height, Runnable block) {
        nvgSave(vg);
        nvgIntersectScissor(vg, x, y, width, height);
        block.run();
        nvgRestore(vg);
    }

    public void line2colors(float x1, float y1, float x2, float y2, float thickness, Color c, Color c2) {
        NVGColor clr = colourise(c);
        NVGColor clr2 = colourise(c2);

        NVGPaint paint = NVGPaint.calloc();
        nvgLinearGradient(vg,x1,y2,x2,y2,clr,clr2,paint);

        nvgSave(vg);
        nvgBeginPath(vg);
        nvgMoveTo(vg,x1,y1);
        nvgLineTo(vg,x2,y2);
        nvgStrokePaint(vg,paint);
        nvgStrokeWidth(vg,thickness);
        nvgStroke(vg);
        nvgClosePath(vg);
        nvgRestore(vg);

        clr.free();
        clr2.free();
        paint.free();
    }

    public void translate(float x, float y) {
        nvgTranslate(vg, x, y);
    }

    public void rotate(float angle) {
        nvgRotate(vg, angle);
    }

    public float textWidth(String text, String face, float size) {
        float[] bounds = new float[4];

        float f = 0;

        nvgSave(vg);
        nvgFontFace(vg, face);
        nvgFontSize(vg, size);
        f  = nvgTextBounds(vg, 0, 0, text, bounds);
        nvgRestore(vg);

        return f;
    }

    public float textHeight(String face, float size) {
        float[] ascender = new float[1];
        float[] descender = new float[1];
        float[] lineh = new float[1];

        nvgFontFace(vg, face);
        nvgFontSize(vg, size);
        nvgTextMetrics(vg, ascender, descender, lineh);

        return lineh[0];
    }

    public void save() {
        nvgSave(vg);
    }

    public void restore() {
        nvgRestore(vg);
    }

    public enum Alignment {
        LEFT_TOP(NVG_ALIGN_LEFT | NVG_ALIGN_TOP),
        CENTER_TOP(NVG_ALIGN_CENTER | NVG_ALIGN_TOP),
        RIGHT_TOP(NVG_ALIGN_RIGHT | NVG_ALIGN_TOP),

        LEFT_MIDDLE(NVG_ALIGN_LEFT | NVG_ALIGN_MIDDLE),
        CENTER_MIDDLE(NVG_ALIGN_CENTER | NVG_ALIGN_MIDDLE),
        RIGHT_MIDDLE(NVG_ALIGN_RIGHT | NVG_ALIGN_MIDDLE),

        LEFT_BOTTOM(NVG_ALIGN_LEFT | NVG_ALIGN_BOTTOM),
        CENTER_BOTTOM(NVG_ALIGN_CENTER | NVG_ALIGN_BOTTOM),
        RIGHT_BOTTOM(NVG_ALIGN_RIGHT | NVG_ALIGN_BOTTOM);

        @Getter
        private final int alignment;

        Alignment(int alignment) {
            this.alignment = alignment;
        }
    }

    public static void glRound(float x, float y, float width, float height, float radius) {
        glPushMatrix();
        glShadeModel(GL_SMOOTH);
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glHint(GL_POLYGON_SMOOTH_HINT, GL_NICEST);

        glBegin(GL_POLYGON);
        int rad;

        GlStateManager.resetColor();
        for (rad = 0; rad <= 90; ++rad)
            glVertex2d(x + radius + Math.sin(rad * Math.PI / 180.0D) * radius * -1.0D, y + radius + Math.cos(rad * Math.PI / 180.0D) * radius * -1.0D);

        for (rad = 90; rad <= 180; ++rad)
            glVertex2d(x + radius + Math.sin(rad * Math.PI / 180.0D) * radius * -1.0D, y + height - radius + Math.cos(rad * Math.PI / 180.0D) * radius * -1.0D);

        for (rad = 0; rad <= 90; ++rad)
            glVertex2d(x + width - radius + Math.sin(rad * Math.PI / 180.0D) * radius, y + height - radius + Math.cos(rad * Math.PI / 180.0D) * radius);

        for (rad = 90; rad <= 180; ++rad)
            glVertex2d(x + width - radius + Math.sin(rad * Math.PI / 180.0D) * radius, y + radius + Math.cos(rad * Math.PI / 180.0D) * radius);

        glEnd();
        glEnable(GL_TEXTURE_2D);
        glShadeModel(GL_FLAT);
        glPopMatrix();

        GlStateManager.resetColor();
    }
}
