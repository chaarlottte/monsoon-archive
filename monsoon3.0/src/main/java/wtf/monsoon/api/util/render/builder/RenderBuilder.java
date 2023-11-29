package wtf.monsoon.api.util.render.builder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.AxisAlignedBB;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Surge
 * @since 30/12/2022
 */
public class RenderBuilder {

    private boolean depth = false;
    private boolean blend = false;
    private boolean texture = false;
    private boolean alpha = false;

    private AxisAlignedBB boundingBox;

    private Color innerColour;
    private Color outerColour;

    private BoxRenderMode renderMode = BoxRenderMode.BOTH;

    public RenderBuilder start() {
        glPushMatrix();
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);

        return this;
    }

    public void build(boolean offset) {
        if (offset) {
            boundingBox = boundingBox.offset(
                    -Minecraft.getMinecraft().getRenderManager().viewerPosX,
                    -Minecraft.getMinecraft().getRenderManager().viewerPosY,
                    -Minecraft.getMinecraft().getRenderManager().viewerPosZ
            );
        }

        if (renderMode == BoxRenderMode.FILL || renderMode == BoxRenderMode.BOTH) {
            RenderGlobal.renderFilledBox(boundingBox, innerColour.getRed(), innerColour.getGreen(), innerColour.getBlue(), innerColour.getAlpha());
        }

        if (renderMode == BoxRenderMode.FILL || renderMode == BoxRenderMode.BOTH) {
            RenderGlobal.bleh_drawSelectionBoundingBox(boundingBox, outerColour.getRed(), outerColour.getGreen(), outerColour.getBlue(), outerColour.getAlpha());
        }

        if (depth) {
            glDepthMask(true);
            glEnable(GL_DEPTH_TEST);
        }

        if (blend) {
            glEnable(GL_BLEND);
        }

        if (alpha) {
            glEnable(GL_ALPHA_TEST);
        }

        if (texture) {
            glEnable(GL_TEXTURE_2D);
        }

        glDisable(GL_LINE_SMOOTH);
        glPopMatrix();
    }

    public RenderBuilder boundingBox(AxisAlignedBB bb) {
        this.boundingBox = bb;

        return this;
    }

    public RenderBuilder type(BoxRenderMode mode) {
        this.renderMode = mode;

        return this;
    }

    public RenderBuilder depth() {
        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);

        depth = true;

        return this;
    }

    public RenderBuilder blend() {
        glEnable(GL_BLEND);

        blend = true;

        return this;
    }

    public RenderBuilder texture() {
        glDisable(GL_TEXTURE_2D);

        texture = true;

        return this;
    }

    public RenderBuilder alpha() {
        glDisable(GL_ALPHA_TEST);

        alpha = true;

        return this;
    }

    public RenderBuilder lineWidth(float lineWidth) {
        glLineWidth(lineWidth);

        return this;
    }

    public RenderBuilder innerColour(Color colour) {
        this.innerColour = colour;

        return this;
    }

    public RenderBuilder outerColour(Color colour) {
        this.outerColour = colour;

        return this;
    }

}
