package wtf.monsoon.impl.module.hud;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.entity.EntityUtil;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RenderUtil;
import wtf.monsoon.impl.event.EventRender2D;

import java.awt.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Arrows extends Module {

    public static Setting<Double> radius = new Setting<>("Radius", 20D)
            .minimum(5D)
            .maximum(40D)
            .incrementation(1D)
            .describedBy("The arrows' distance from the center of the screen");

    public static Setting<Float> opacity = new Setting<>("Opacity", 100f)
            .minimum(0f)
            .maximum(100f)
            .incrementation(1f)
            .describedBy("The alpha of the arrows");

    public Arrows() {
        super("Arrows", "Renders arrows towards players", Category.HUD);
    }

    List<Entity> players;

    @EventLink
    Listener<EventRender2D> r2dListener = this::render2d;

    void render2d(EventRender2D e) {

        players = mc.theWorld.loadedEntityList.stream().filter((ent) -> ent instanceof EntityPlayer && ent != mc.thePlayer && !ent.isInvisible()).collect(Collectors.toList());

        float[] screenCenterCoord = {e.getSr().getScaledWidth() / 2f, e.getSr().getScaledHeight() / 2f};

        AtomicInteger i = new AtomicInteger();
        players.forEach(target -> {
            Vec3 vec = EntityUtil.getInterpolatedPosition(target);

            double yaw = ((StrictMath.atan2(vec.z - mc.thePlayer.posZ, vec.x - mc.thePlayer.posX) * 57.29577951308232) - 90.0);
            double finalAngle = 360 - (yaw - mc.thePlayer.rotationYaw);

            Color accentClr = ColorUtil.getClientAccentTheme()[0];

            Color color = new Color(
                    accentClr.getRed() / 255f,
                    accentClr.getGreen() / 255f,
                    accentClr.getBlue() / 255f,
                    opacity.getValue() / 100f
            );

            float[] scale = new float[]{2.5f, 2.5f};

            RenderUtil.scale(screenCenterCoord[0], screenCenterCoord[1], scale, () -> {
                RenderUtil.rotate(screenCenterCoord[0], screenCenterCoord[1], finalAngle, () -> {
                    Wrapper.getFontUtil().entypo18.drawCenteredString("v", screenCenterCoord[0] - 0.5f, (float) (screenCenterCoord[1] - 3 - radius.getValue()), color, false);
                });
            });

            i.getAndIncrement();
        });
    }
}
