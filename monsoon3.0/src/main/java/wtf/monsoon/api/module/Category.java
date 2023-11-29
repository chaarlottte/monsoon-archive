package wtf.monsoon.api.module;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Category {

    /**
     * Modules in the combat category
     * E.G. Aura, TargetStrafe
     */
    COMBAT("A"),

    /**
     * Modules in the movement category
     * E.G. Flight, Speed
     */
    MOVEMENT("B"),

    /**
     * Modules in the player category
     * E.G. AutoArmor, Scaffold
     */
    PLAYER("C"),

    /**
     * Modules in the visual category
     * E.G. ESP, Tracers
     */
    VISUAL("D"),

    /**
     * Modules in the exploit category
     * E.G. Disabler, NoC03
     */
    EXPLOIT("E"),

    /**
     * Modules in the ghost category
     * E.G. AutoBridger, ClickDelayRemover
     */
    GHOST("F"),

    /**
     * Modules in the HUD category
     * E.G. ArrayList, Speedometer
     */
    HUD("G"),

    /**
     * Modules in the script category
     * These are added through the scripting system
     */
    SCRIPT("H");

    @Getter
    String icon;
}
