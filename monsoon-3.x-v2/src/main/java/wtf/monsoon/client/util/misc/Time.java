package wtf.monsoon.client.util.misc;

import lombok.Getter;
import lombok.Setter;

public class Time {
    public static float timeStarted = System.nanoTime();
    public static float delta = 0;

    public static float getTime() {
        return (System.nanoTime() - timeStarted) * 1E-9f; // equivalent to '/1000000000f'
    }
}