package wtf.monsoon.impl.module.visual;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import me.surge.animation.Animation;
import me.surge.animation.Easing;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.util.Vec3;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.font.FontUtil;
import wtf.monsoon.api.util.font.impl.FontRenderer;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RenderUtil;
import wtf.monsoon.impl.event.EventPacket;
import wtf.monsoon.impl.event.EventRender3D;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Surge
 * @since 12/11/2022
 */
public class SuperheroFX extends Module {

    public final Setting<Double> maximum = new Setting<>("Maximum", 20.0)
            .minimum(1.0)
            .maximum(50.0)
            .incrementation(1.0)
            .describedBy("The maximum amount of popups to display per hit");

    public final Setting<Float> length = new Setting<>("Length", 400f)
            .minimum(100f)
            .maximum(500f)
            .incrementation(10f)
            .describedBy("The length of the popup animation");

    public final Setting<Boolean> useInterfaceColor = new Setting<>("Use Interface Color", false)
            .describedBy("Whether to use the client interface's color instead of the default ones");

    private final FontRenderer superheroFont = new FontRenderer(FontUtil.getFont("superhero.ttf", 40));

    private final List<String> texts = new ArrayList<>();

    private final ArrayList<Popup> popups = new ArrayList<>();

    private final Random random = new Random();

    public SuperheroFX() {
        super("SuperheroFX", "Renders superhero style messages when you hit people", Category.VISUAL);

        texts.addAll(Arrays.asList(
                "POW",
                "KAPOW",
                "BOOM",
                "ZAP",
                "KABOOM"
        ));
    }

    @EventLink
    private final Listener<EventRender3D> eventRender3DListener = event -> {
        popups.forEach(Popup::render);
        popups.removeIf(popup -> popup.animation.getAnimationFactor() == 0.0);
    };

    @EventLink
    private final Listener<EventPacket> eventAttackEntityListener = event -> {
        if (event.getDirection().equals(EventPacket.Direction.SEND) && event.getPacket() instanceof C02PacketUseEntity) {
            try {
                C02PacketUseEntity packetUseEntity = (C02PacketUseEntity) event.getPacket();

                if (packetUseEntity.getAction().equals(C02PacketUseEntity.Action.ATTACK)) {
                    for (int i = 0; i < random.nextInt(maximum.getValue().intValue()) + 1; i++) {
                        float offsetX = random.nextFloat() * 2;
                        float offsetY = random.nextFloat() * 2;
                        float offsetZ = random.nextFloat() * 2;

                        String text = texts.get(random.nextInt(texts.size()));

                        popups.add(new Popup(packetUseEntity.getEntityFromWorld(mc.theWorld).getPositionVector().addVector(offsetX - 1f, packetUseEntity.getEntityFromWorld(mc.theWorld).height + offsetY - 1f, offsetZ - 1f), text, getColor(popups.size())));
                    }
                }
            } catch (Exception exception) { // surge ur impl is crashing all the time man
                exception.printStackTrace();
            }
        }
    };

    private Color getColor(int index) {
        if (useInterfaceColor.getValue()) {
            Color[] colorArray = ColorUtil.getClientAccentTheme();
            return ColorUtil.fadeBetween(10, index, colorArray[0], colorArray[colorArray.length - 1]);
        } else {
            List<Color> colors = new ArrayList<>();
            colors.add(Color.BLUE);
            colors.add(Color.ORANGE);
            colors.add(Color.RED);
            colors.add(Color.YELLOW);
            colors.add(Color.RED);
            return colors.get(random.nextInt(texts.size()));
        }
    }

    private class Popup {

        private final Vec3 vec;
        private final String text;
        private final Color colour;

        private final Animation animation = new Animation(length::getValue, false, Easing.CUBIC_IN_OUT);

        public Popup(Vec3 vec, String text, Color colour) {
            this.vec = vec;
            this.text = text;
            this.colour = colour;
            animation.setState(true);
        }

        public void render() {
            if (animation.getState() && animation.getAnimationFactor() == 1.0) {
                animation.setState(false);
            }

            float width = superheroFont.getStringWidth(text);

            double[] renderValues = {
                    mc.getRenderManager().renderPosX,
                    mc.getRenderManager().renderPosY,
                    mc.getRenderManager().renderPosZ
            };

            glPushMatrix();

            glTranslated(vec.x - renderValues[0], vec.y - renderValues[1], vec.z - renderValues[2]);

            glRotated(-mc.getRenderManager().playerViewY, 0.0, 1.0, 0.0);
            glRotated(mc.getRenderManager().playerViewX, (mc.gameSettings.thirdPersonView == 2 ? -1 : 1), 0.0, 0.0);

            glScaled(-0.02, -0.02, -0.02);

            glDisable(GL_DEPTH_TEST);

            // Center nametag
            glTranslated((-width / 2), 0.0, 0.0);

            RenderUtil.scaleXY(width / 2f, superheroFont.getHeight() / 2, animation, () -> {
                superheroFont.drawStringWithShadow(text, 0, 0, colour);
            });

            glEnable(GL_DEPTH_TEST);
            glEnable(GL_ALPHA_TEST);

            glPopMatrix();
        }

    }

}