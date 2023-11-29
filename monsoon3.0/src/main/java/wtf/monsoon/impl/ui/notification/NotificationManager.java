package wtf.monsoon.impl.ui.notification;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import wtf.monsoon.Wrapper;
import wtf.monsoon.impl.module.hud.NotificationsModule;

import java.awt.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class NotificationManager {

    private final CopyOnWriteArrayList<Notification> notifications = new CopyOnWriteArrayList<>();

    public void notify(NotificationType type, String title, String description) {
        if (!Wrapper.getModule(NotificationsModule.class).isEnabled() && (title.contains("Enabled Module") || title.contains("Disabled Module"))) {
            return;
        }

        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        notifications.add(new Notification(scaledResolution.getScaledWidth() - 205, scaledResolution.getScaledHeight() - 30 * notifications.size(), type, title, description));
    }

    public void render() {
        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        notifications.removeIf(Notification::shouldNotificationHide);

        float offset = 0;

        for (Notification notification : notifications) {
            notification.setX(scaledResolution.getScaledWidth() - (float) ((notification.getWidth() + 5) * notification.getAnimation().getAnimationFactor()));
            notification.setY(scaledResolution.getScaledHeight() - (offset * 1.1f) - notification.getHeight() - 5);

            notification.draw(0, 0, 0);

            offset += notification.getHeight() * notification.getAnimation().getAnimationFactor();
        }
    }

}
