package wtf.monsoon.api.util.render;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.opengl.Display;
import wtf.monsoon.api.util.misc.IOUtil;

import java.awt.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;

import static org.lwjgl.nanovg.NanoVGGL3.*;
import static org.lwjgl.nanovg.NanoVG.*;

public class NVGR {
    public long vg;
    String[] s_fonts = {"black","bold","ebold","elight","light","medium","regular","sbold","thin",
            "comic_sans","comic_sans_bold","entypo","greycliff","menu_icons","minecraft.otf","product_sans","product_sans_bold","superhero","ubuntuwu","category","category2"};
    int images[];
    HashMap<String, ByteBuffer> fonts = new HashMap<>();
    HashMap<String, Integer> textures = new HashMap<>();
    public void init() {
        vg = nvgCreate(NVG_ANTIALIAS);
        for (String sf : s_fonts) {
            ByteBuffer buff = null;
            try {
                buff = IOUtil.getResourceBytes("font/"+sf+(sf.endsWith(".otf") ? "" : ".ttf"), 1024);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            nvgCreateFontMem(vg, sf, buff, 0);
            fonts.put(sf,buff);
        }
    }

    public void initTextures(String... textures) throws IOException {
        images = new int[textures.length];
        for (int i = 0; i < textures.length; i++) {
            images[i] = nvgCreateImageMem(vg,NVG_IMAGE_NEAREST, IOUtil.getResourceBytes("textures/"+textures[i]+".png",512));
            this.textures.put(textures[i],images[i]);
        }
    }

    public void deleteNanoVG() {
        for (int image : images) {
            nvgDeleteImage(vg, image);
        }
        nvgDelete(vg);
    }

    public void initFrame() {
        nvgBeginFrame(vg, Display.getWidth(),Display.getHeight(), 1f);
    }

    public void finishFrame() {
        nvgEndFrame(vg);
    }

    public void circle(float x, float y, float radius, Color c) {
        NVGColor clr = colorize(c);

        nvgBeginPath(vg);
        nvgCircle(vg,x,y,radius);
        nvgFillColor(vg,clr);
        nvgFill(vg);
        nvgClosePath(vg);

        clr.free();
    }

    public void quad(float x1, float y1, float x2, float y2, float sw, Color c) {
        NVGColor clr = colorize(c);

        nvgBeginPath(vg);
        nvgMoveTo(vg,x1,y1);
        nvgQuadTo(vg,x1+(x2-x1)/2f,y1,x1+(x2-x1)/2f,y1+(y2-y1)/2f);
        nvgQuadTo(vg,x1+(x2-x1)/2f,y2,x2,y2);
        nvgStrokeColor(vg,clr);
        nvgStrokeWidth(vg,sw);
        nvgStroke(vg);
        nvgClosePath(vg);

        clr.free();
    }
    public void line(float x1, float y1, float x2, float y2, float thickness, Color c) {
        NVGColor clr = colorize(c);

        nvgSave(vg);
        nvgBeginPath(vg);
        nvgMoveTo(vg,x1,y1);
        nvgLineTo(vg,x2,y2);
        nvgStrokeColor(vg,clr);
        nvgStrokeWidth(vg,thickness);
        nvgStroke(vg);
        nvgClosePath(vg);
        nvgRestore(vg);

        clr.free();
    }
    public void line2colors(float x1, float y1, float x2, float y2, float thickness, Color c, Color c2) {
        NVGColor clr = colorize(c);
        NVGColor clr2 = colorize(c2);

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

    public void rect(float x, float y, float w, float h, Color c) {
        NVGColor clr = colorize(c);

        nvgBeginPath(vg);
        nvgRect(vg,x,y,w,h);
        nvgFillColor(vg,clr);
        nvgFill(vg);
        nvgClosePath(vg);
    }


    public void dropShadow(float x, float y, float w, float h, float r, float spread, Color c, Color c2,boolean clipInside) {
        NVGPaint shadowPaint = NVGPaint.calloc();
        NVGColor colorA = colorize(c);
        NVGColor colorB = colorize(c2);

        nvgBoxGradient(vg, x - spread, y - spread, w + spread*2, h + spread*2, r + spread, spread*2, colorA, colorB, shadowPaint);
        nvgBeginPath(vg);
        nvgRoundedRect(vg, x - spread - spread*2*2, y - spread - spread*2*2, w + 2 * spread + 2 * spread*2*2, h + 2 * spread + 2 * spread*2*2, r + spread*3);
        if(clipInside) {
            nvgPathWinding(vg, NVG_HOLE);
            nvgRoundedRect(vg, x, y, w, h, r);
        }
        nvgFillPaint(vg, shadowPaint);
        nvgFill(vg);
        nvgClosePath(vg);
        shadowPaint.free();
        colorA.free();
        colorB.free();
    }

    public void texture(float x, float y, float w, float h, int u, int v, String domain) {
        nvgImageSize(vg, textures.get(domain), new int[]{u}, new int[]{v});
        NVGPaint p = NVGPaint.calloc();
        p = nvgImagePattern(vg, x, y, w, h, 0, textures.get(domain), 1f, p);
        nvgBeginPath(vg);
        nvgRect(vg, x, y, w, h);
        nvgFillPaint(vg, p);
        nvgFill(vg);
        nvgClosePath(vg);
        p.free();
    }

    public void round(float x, float y, float w, float h, float r, Color c) {
        round(x,y,w,h,r,r,r,r,c);
    }

    public void round(float x, float y, float w, float h, float r1, float r2, float r3, float r4, Color c) {
        NVGColor clr = colorize(c);

        nvgBeginPath(vg);
        nvgRoundedRectVarying(vg,x,y,w,h,r1,r2,r3,r4);
        nvgFillColor(vg,clr);
        nvgFill(vg);
        nvgClosePath(vg);

        clr.free();
    }

    public void roundBorder(float x, float y, float width, float height, float radius, float thickness, Color colour) {
        NVGColor clr = colorize(colour);

        nvgBeginPath(vg);
        nvgRoundedRectVarying(vg, x, y, width, height, radius, radius, radius, radius);
        nvgStrokeColor(vg, clr);
        nvgStroke(vg);
        nvgClosePath(vg);

        clr.free();
    }

    public void roundedLinearGradient(float x, float y, float width, float height, float radius, Color start, Color end) {
        NVGColor startColour = colorize(start);
        NVGColor endColour = colorize(end);

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

    public void stroke(float x, float y, float w, float h, float r, float sw, Color c) {
        NVGColor clr = colorize(c);

        nvgBeginPath(vg);
        nvgRoundedRect(vg,x,y,w,h,r);
        nvgStrokeColor(vg,clr);
        nvgStrokeWidth(vg,sw);
        nvgStroke(vg);
        nvgClosePath(vg);

        clr.free();
    }

    public void stroke(float x, float y, float w, float h, float r1, float r2, float r3, float r4, float sw, Color c) {
        NVGColor clr = colorize(c);

        nvgBeginPath(vg);
        nvgRoundedRectVarying(vg,x,y,w,h,r1,r2,r3,r4);
        nvgStrokeColor(vg,clr);
        nvgStrokeWidth(vg,sw);
        nvgStroke(vg);
        nvgClosePath(vg);

        clr.free();
    }

    public void text(String text, String face, float size, float x, float y, Color c) {
        text(text,face,size,x,y,c,NVG_ALIGN_LEFT | NVG_ALIGN_TOP);
    }

    public void text(String text, String face, float size, float x, float y, Color c, int alignment) {
        NVGColor clr = colorize(c);

        nvgBeginPath(vg);
        nvgFillColor(vg, clr);
        nvgFontFace(vg, face);
        nvgFontSize(vg, size);
        nvgTextAlign(vg, alignment);
        nvgText(vg, x, y, text);
        nvgClosePath(vg);

        clr.free();
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
        NanoVG.nvgFontSize(vg, size);
        NanoVG.nvgTextMetrics(vg, ascender, descender, lineh);
        return lineh[0];
    }

    public void scissor(float x, float y, float width, float height, Runnable block) {
        nvgSave(vg);
        nvgScissor(vg, x, y, width, height);
        block.run();
        nvgRestore(vg);
    }

    NVGColor colorize(Color c) {
        return NVGColor.calloc().r(c.getRed()/255f).g(c.getGreen()/255f).b(c.getBlue()/255f).a(c.getAlpha()/255f);
    }
}
