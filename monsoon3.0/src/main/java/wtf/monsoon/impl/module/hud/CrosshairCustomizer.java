package wtf.monsoon.impl.module.hud;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import me.surge.animation.Animation;
import me.surge.animation.ColourAnimation;
import me.surge.animation.Easing;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import org.luaj.vm2.ast.Str;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.impl.event.EventRender2D;

import java.awt.*;

public class CrosshairCustomizer extends Module {

    public final Setting<Float> sizeSetting = new Setting<>("Size", 2.5f)
            .minimum(0.25f)
            .maximum(15.0f)
            .incrementation(0.25f)
            .describedBy("Size");

    public final Setting<Float> gapSetting = new Setting<>("Gap", 2.5f)
            .minimum(0.25f)
            .maximum(15.0f)
            .incrementation(0.25f)
            .describedBy("Gap");

    public final Setting<Float> widthSetting = new Setting<>("Width", 1.0f)
            .minimum(0.25f)
            .maximum(10.0f)
            .incrementation(0.25f)
            .describedBy("Width");

    public Setting<Boolean> outline = new Setting<>("Outline", true)
            .describedBy("Outline");

    public Setting<Boolean> dynamic = new Setting<>("Dynamic", false)
            .describedBy("dynamic");

    public Setting<Float> dynamicGap = new Setting<>("Gap", 2.5f)
            .minimum(0.0f)
            .maximum(15.0f)
            .incrementation(0.25f)
            .describedBy("dynamic gap")
            .childOf(dynamic);

    public Setting<String> elements = new Setting<>("Elements", "Elements")
            .describedBy("Elements to render.");

    public Setting<Boolean> renderTop = new Setting<>("Top", true)
            .describedBy("Render the top crosshair element.")
            .childOf(elements);

    public Setting<Boolean> renderBottom = new Setting<>("Bottom", true)
            .describedBy("Render the bottom crosshair element.")
            .childOf(elements);

    public Setting<Boolean> renderLeft = new Setting<>("Left", true)
            .describedBy("Render the left crosshair element.")
            .childOf(elements);

    public Setting<Boolean> renderRight = new Setting<>("Right", true)
            .describedBy("Render the right crosshair element.")
            .childOf(elements);

    public Setting<Color> color = new Setting<>("Color", new Color(-1))
            .describedBy("Color");

    public Setting<Boolean> pointed = new Setting<>("Pointed", true)
            .describedBy("Change the colour when pointed at an entity");

    public Setting<Float> pointedGap = new Setting<>("Gap", 2.5f)
            .minimum(0.0f)
            .maximum(15.0f)
            .incrementation(0.25f)
            .describedBy("How much to expand the gap by when pointed at an entity")
            .childOf(pointed);

    public Setting<Color> pointedColour = new Setting<>("Color", new Color(-1))
            .describedBy("The colour of the crosshair when pointed at an entity")
            .childOf(pointed);

    private final Animation dynamicAnimation = new Animation(() -> 200f, false, () -> Easing.LINEAR);
    private final Animation pointAnimation = new Animation(() -> 200f, false, () -> Easing.CUBIC_IN_OUT);

    public CrosshairCustomizer() {
        super("Crosshair", "Customize the crosshair", Category.HUD);
    }

    @EventLink
    private final Listener<EventRender2D> eventRender2DListener = e -> {
        ScaledResolution sr = e.getSr();

        renderVerticalRects(sr);
        renderHorizontalRects(sr);
    };

    private void renderHorizontalRects(ScaledResolution sr) {
        float height = widthSetting.getValue() / 2;
        float gap = gapSetting.getValue();
        float outlineSize = 0.5f;
        Color color1 = color.getValue();

        if(dynamic.getValue()) {
            dynamicAnimation.setState(mc.thePlayer.isSprinting());
            gap += dynamicGap.getValue() * dynamicAnimation.getAnimationFactor();
        }

        if (pointed.getValue()) {
            pointAnimation.setState(mc.pointedEntity != null && !mc.pointedEntity.isDead);

            gap += pointedGap.getValue() * pointAnimation.getAnimationFactor();
            color1 = new Color(ColorUtil.fadeBetween(color1.getRGB(), pointedColour.getValue().getRGB(), (float) pointAnimation.getAnimationFactor()));
        }

        if(outline.getValue()) {
            if(renderLeft.getValue()) Gui.drawRect(sr.getScaledWidth() / 2 - gap - sizeSetting.getValue() - outlineSize, sr.getScaledHeight() / 2 - height - outlineSize, sr.getScaledWidth() / 2 - gap + outlineSize, sr.getScaledHeight() / 2 + height + outlineSize, Color.BLACK.getRGB()); // left
            if(renderRight.getValue()) Gui.drawRect(sr.getScaledWidth() / 2 + gap - outlineSize, sr.getScaledHeight() / 2 - height - outlineSize, sr.getScaledWidth() / 2 + gap + sizeSetting.getValue() + outlineSize, sr.getScaledHeight() / 2 + height + outlineSize, Color.BLACK.getRGB()); // right
        }

        if(renderLeft.getValue()) Gui.drawRect(sr.getScaledWidth() / 2 - gap - sizeSetting.getValue(), sr.getScaledHeight() / 2 - height, sr.getScaledWidth() / 2 - gap, sr.getScaledHeight() / 2 + height, color1.getRGB()); // left
        if(renderRight.getValue()) Gui.drawRect(sr.getScaledWidth() / 2 + gap, sr.getScaledHeight() / 2 - height, sr.getScaledWidth() / 2 + gap + sizeSetting.getValue(), sr.getScaledHeight() / 2 + height, color1.getRGB()); // right
    }

    private void renderVerticalRects(ScaledResolution sr) {
        float width = widthSetting.getValue() / 2;
        float gap = gapSetting.getValue();
        float outlineSize = 0.5f;
        Color color1 = color.getValue();

        if(dynamic.getValue()) {
            dynamicAnimation.setState(mc.thePlayer.isSprinting());
            gap += dynamicGap.getValue() * dynamicAnimation.getAnimationFactor();
        }

        if (pointed.getValue()) {
            pointAnimation.setState(mc.pointedEntity != null && !mc.pointedEntity.isDead);

            gap += pointedGap.getValue() * pointAnimation.getAnimationFactor();
            color1 = new Color(ColorUtil.fadeBetween(color1.getRGB(), pointedColour.getValue().getRGB(), (float) pointAnimation.getAnimationFactor()));
        }

        if(outline.getValue()) {
            if(renderTop.getValue()) Gui.drawRect(sr.getScaledWidth() / 2 - width - outlineSize, sr.getScaledHeight() / 2 - gap - sizeSetting.getValue() - outlineSize, sr.getScaledWidth() / 2 + width + outlineSize, sr.getScaledHeight() / 2 - gap + outlineSize, Color.BLACK.getRGB()); // top
            if(renderBottom.getValue()) Gui.drawRect(sr.getScaledWidth() / 2 - width - outlineSize, sr.getScaledHeight() / 2 + gap + sizeSetting.getValue() + outlineSize, sr.getScaledWidth() / 2 + width + outlineSize, sr.getScaledHeight() / 2 + gap - outlineSize, Color.BLACK.getRGB()); // bottom
        }

        if(renderTop.getValue()) Gui.drawRect(sr.getScaledWidth() / 2 - width, sr.getScaledHeight() / 2 - gap - sizeSetting.getValue(), sr.getScaledWidth() / 2 + width, sr.getScaledHeight() / 2 - gap, color1.getRGB()); // top
        if(renderBottom.getValue()) Gui.drawRect(sr.getScaledWidth() / 2 - width, sr.getScaledHeight() / 2 + gap + sizeSetting.getValue(), sr.getScaledWidth() / 2 + width, sr.getScaledHeight() / 2 + gap, color1.getRGB()); // bottom
    }

}
