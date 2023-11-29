package wtf.monsoon.client.ui;

import lombok.Setter;
import org.lwjgl.nanovg.NanoVG;
import wtf.monsoon.Wrapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.surge.animation.Animation;
import me.surge.animation.Easing;
import wtf.monsoon.client.util.misc.Time;
import wtf.monsoon.client.util.ui.ColorUtil;
import wtf.monsoon.client.util.ui.NVGWrapper;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class ParticleEngine {

    private NVGWrapper ui = Wrapper.getMonsoon().nvg;
    private final ArrayList<Particle> particles = new ArrayList<>();
    private final long initTime = System.currentTimeMillis();
    private int beginSeconds = 0;

    public float maxVelocity;

    public ParticleEngine(float maxVelocity) {
        this.maxVelocity = maxVelocity;
    }

    public void render(float width, float height, int mx, int my) {
        particles.removeIf(p -> p.alpha == 0 && !p.lifeAnimation.getState());
        int seconds = ((int) (System.currentTimeMillis() - initTime) / 100);

        if (beginSeconds != seconds) {
            particles.add(new Particle(
                    ThreadLocalRandom.current().nextFloat() * width,
                    ThreadLocalRandom.current().nextFloat() * height,
                    (float) (Math.random() * 2 - 1) * maxVelocity * 50,
                    (float) (Math.random() * 2 - 1) * maxVelocity * 50,
                    new Color(Color.HSBtoRGB((float)(System.currentTimeMillis() % 3600) / 1000f,0.5f,1f))));
            beginSeconds = seconds;
        }

        particles.forEach(particle -> {
            particles.forEach(subParticle -> {
                if (particle != subParticle) {
                    float radius = 400*particle.alpha;

                    if (inCircle(particle.x, particle.y, radius, subParticle.x, subParticle.y)) {
                        Color c1 = ColorUtil.interpolate(ColorUtil.TRANSPARENT, particle.color, 1 - Math.min(1, distance(particle.x, particle.y, subParticle.x, subParticle.y) / radius));
                        Color c2 = ColorUtil.interpolate(ColorUtil.TRANSPARENT, subParticle.color, 1 - Math.min(1, distance(particle.x, particle.y, subParticle.x, subParticle.y) / radius));

                        ui.line2colors(particle.x, particle.y, subParticle.x, subParticle.y,1,
                                c1,
                                c2);
                    }
                }
            });
        });

        particles.forEach(p -> p.render());
    }

    boolean inCircle(float centerX, float centerY, float radius, float x, float y) {
        return Math.sqrt((x - centerX) * (x - centerX) + (y - centerY) * (y - centerY)) <= radius;
    }

    float distance(float centerX, float centerY, float x, float y) {
        return (float) Math.sqrt((x - centerX) * (x - centerX) + (y - centerY) * (y - centerY));
    }

    @RequiredArgsConstructor
    private static class Particle {
        private NVGWrapper ui = Wrapper.getMonsoon().nvg;
        private final Animation lifeAnimation = new Animation(() -> 200F, true, () -> Easing.LINEAR);
        private final Animation fadeAnim = new Animation(() -> 200F, false, () -> Easing.LINEAR);
        float alpha = 0f;
        @NonNull private float x;
        @NonNull private float y;
        @NonNull private float velocityX;
        @NonNull private float velocityY;

        @NonNull Color color;

        private long time;

        public void render() {
            time++;
            Color c = new Color(color.getRed()/255f,color.getGreen()/255f,color.getBlue()/255f,alpha);
            lifeAnimation.setState(time < 400);
            fadeAnim.setState(lifeAnimation.getState());
            if(lifeAnimation.getState())
                alpha = Math.min(1, alpha+0.01f);
            else
                alpha = Math.max(0, alpha-0.01f);

            x += velocityX  * Time.delta;
            y += velocityY  * Time.delta;
            NanoVG.nvgGlobalAlpha(ui.vg,1f);
//            ui.dropShadow(x-2,y-2,4,4,2*alpha, alpha*15/1.5f,new Color(color.getRed()/255f,color.getGreen()/255f,color.getBlue()/255f,alpha/30),ColorUtil.TRANSPARENT,false);
//            ui.dropShadow(x-2,y-2,4,4,2*alpha, alpha*25/1.5f,new Color(color.getRed()/255f,color.getGreen()/255f,color.getBlue()/255f,alpha/30),ColorUtil.TRANSPARENT,false);
            ui.circle(x, y,4*alpha, c);
        }
    }

}
