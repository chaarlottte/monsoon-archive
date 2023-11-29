package wtf.monsoon.impl.ui.particle;

import lombok.Getter;
import net.minecraft.util.MathHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Surge
 * @since 21/08/2022
 */
public class ParticleSystem {

    @Getter
    private final List<Particle> particles = new ArrayList<>();

    public ParticleSystem() {
        for (int i = 0; i < 100; i++) {
            particles.add(new Particle(this));
        }
    }

    public void render() {
        particles.forEach(Particle::render);
    }

    public Particle getNearest(Particle particle) {
        Particle nearest = null;
        float nearestDist = Float.MAX_VALUE;

        for (Particle particle1 : particles) {
            float f = particle.getX() - particle1.getX();
            float f1 = particle.getY() - particle1.getY();

            if (particle1 != particle && MathHelper.sqrt_float(f * f + f1 * f1) < nearestDist) {
                nearest = particle1;
                nearestDist = MathHelper.sqrt_float(f * f + f1 * f1);
            }
        }

        return nearest;
    }

}
