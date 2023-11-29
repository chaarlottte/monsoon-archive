package wtf.monsoon.impl.ui.primitive;

import java.util.Arrays;

/**
 * @author Surge
 * @since 30/07/2022
 */
public enum Click {

    LEFT(0),
    RIGHT(1),
    MIDDLE(2),
    SIDE_ONE(3),
    SIDE_TWO(4);

    private final int button;

    Click(int button) {
        this.button = button;
    }

    public int getButton() {
        return button;
    }

    public static Click getClick(int in) {
        return Arrays.stream(Click.values()).filter(c -> c.getButton() == in).findFirst().orElse(null);
    }

}
