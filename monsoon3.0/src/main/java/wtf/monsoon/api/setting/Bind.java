package wtf.monsoon.api.setting;

import lombok.Getter;
import lombok.Setter;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

/**
 * @author Surge
 * @since 08/08/2022
 */
public class Bind {

    // The button index
    @Getter
    @Setter
    private int buttonCode;

    // Input device
    @Getter
    @Setter
    private Device device;

    public Bind(int buttonCode, Device device) {
        this.buttonCode = buttonCode;
        this.device = device;
    }

    public boolean isPressed() {
        if (buttonCode == 0) {
            return false;
        }

        // Our bind is pressed
        boolean pressed = Keyboard.isKeyDown(buttonCode) && device.equals(Device.KEYBOARD) || Mouse.isButtonDown(buttonCode) && device.equals(Device.MOUSE);

        return pressed;
    }

    public enum Device {
        /**
         * A key on the keyboard
         */
        KEYBOARD,

        /**
         * A mouse button
         */
        MOUSE
    }

    /**
     * Gets the button name for the GUI
     *
     * @return The button name
     */
    public String getButtonName() {
        return device.equals(Device.KEYBOARD) ? Keyboard.getKeyName(buttonCode) : Mouse.getButtonName(buttonCode);
    }

}
