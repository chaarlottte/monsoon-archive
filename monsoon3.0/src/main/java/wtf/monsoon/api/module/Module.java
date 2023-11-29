package wtf.monsoon.api.module;

import lombok.Getter;
import lombok.Setter;
import me.surge.animation.Animation;
import me.surge.animation.Easing;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.setting.Bind;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.obj.MonsoonPlayerObject;
import wtf.monsoon.impl.module.annotation.DefaultBind;
import wtf.monsoon.impl.ui.notification.NotificationType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class Module implements Cloneable {

    // The name of the module
    @Getter @Setter
    private String name;

    // The description of the module
    @Getter
    private final String description;

    // The module's enabled state
    @Getter
    private boolean enabled;

    // Whether the module is visible in the arraylist
    @Getter @Setter
    private boolean visible = true;

    // The module's keybind
    @Getter @Setter
    private Setting<Bind> key = new Setting<>("Keybinding", new Bind(0, Bind.Device.KEYBOARD))
                .describedBy("The bind used to toggle the module");;

    // The module's category
    @Getter @Setter
    private Category category;

    // The module's animation for the arraylist
    @Getter
    private final Animation animation = new Animation(() -> 250f, false, () -> Easing.CUBIC_IN_OUT);

    // ?????
    @Getter
    private final Animation toggleHudAnimation = new Animation(() -> 250f, false, () -> Easing.CUBIC_IN_OUT);

    // The module's settings
    @Getter
    private final List<Setting<?>> settings = new ArrayList<>();

    // suffix
    @Getter @Setter
    private Supplier<String> metadata = () -> "";

    // Minecraft instance
    public Minecraft mc = Wrapper.getMinecraft();

    // Player instance
    public MonsoonPlayerObject player = Wrapper.getMonsoon().getPlayer();

    @Getter @Setter
    private boolean isDuplicate = false;

    public Module(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.category = category;

        if (this.getClass().isAnnotationPresent(DefaultBind.class)) {
            DefaultBind bindData = this.getClass().getAnnotation(DefaultBind.class);
            this.getKey().setValue(new Bind(bindData.code(), bindData.device()));
        }
    }

    /**
     * Sets the enabled state of the module
     *
     * @param enabled The new state
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;

        this.getAnimation().setState(enabled);

        if (enabled) {
            onEnable();
        } else {
            onDisable();
        }
    }

    /**
     * Sets the enabled state of the module <b>without</b> firing <code>onEnable()</code> and <code>onDisable()</code>
     *
     * @param enabled The new state
     */
    public void setEnabledSilent(boolean enabled) {
        this.enabled = enabled;

        this.getAnimation().setState(enabled);

        if (enabled) {
            Wrapper.getEventBus().subscribe(this);
        } else {
            Wrapper.getEventBus().unsubscribe(this);
        }
    }

    /**
     * Toggles the module's enabled state
     */
    public void toggle() {
        setEnabled(!enabled);
    }

    /**
     * Fired when the module is enabled
     */
    public void onEnable() {
        Wrapper.getEventBus().subscribe(this);
        Wrapper.getNotifManager().notify(NotificationType.YES, "Enabled Module", name);
    }

    /**
     * Fired when the module is disabled
     */
    public void onDisable() {
        Wrapper.getEventBus().unsubscribe(this);
        Wrapper.getNotifManager().notify(NotificationType.NO, "Disabled Module", name);
    }

    /**
     * Gets the data for the arraylist
     *
     * @return The data for the arraylist
     */
    public String getDisplayName() {
        return getName() + (!getMetaData().equals("") ? (" " + EnumChatFormatting.GRAY + getMetaData()) : "");
    }

    public String getMetaData() {
        return this.metadata.get();
    }

    public Module clone() throws CloneNotSupportedException {
        return (Module) super.clone();
    }

    public List<Setting<?>> getSettingHierarchy() {
        List<Setting<?>> hierarchy = new ArrayList<>();

        for (Setting<?> setting : settings) {
            hierarchy.add(setting);
            hierarchy.addAll(setting.getHierarchy());
        }

        return hierarchy;
    }
}
