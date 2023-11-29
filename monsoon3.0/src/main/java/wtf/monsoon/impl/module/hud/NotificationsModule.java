package wtf.monsoon.impl.module.hud;

import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;

/**
 * @author Surge
 * @since 12/11/2022
 */
public class NotificationsModule extends Module {

    public final Setting<Float> barOpacity = new Setting<>("BarOpacity", 1.0f)
            .minimum(0f)
            .maximum(1f)
            .incrementation(0.01f)
            .describedBy("The opacity of the time bar");

    public final Setting<Float> barDarken = new Setting<>("BarDarken", 0.65f)
            .minimum(0f)
            .maximum(1f)
            .incrementation(0.01f)
            .describedBy("How much to darken the bar by");

    public final Setting<FlagAlertMode> flagAlert = new Setting<FlagAlertMode>("Flag Alert Mode", FlagAlertMode.NOTIFICATION)
            .describedBy("How to alert you to a flag.");

    public NotificationsModule() {
        super("Notifications", "Renders notifications on screen", Category.HUD);
    }

    public enum FlagAlertMode {
        NOTIFICATION, INDICATOR
    }

}
