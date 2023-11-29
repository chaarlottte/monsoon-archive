package wtf.monsoon.api.util.misc;


import net.minecraft.util.Util;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import java.io.IOException;
import java.io.InputStream;

public class SoundUtil extends Util {

    public static void playSound(String resourceLocation) {
        try {
            InputStream sound = getFileFromResourceAsStream("assets/minecraft/monsoon/sound/" + resourceLocation);
            AudioStream stream = new AudioStream(sound);
            AudioPlayer.player.start(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static InputStream getFileFromResourceAsStream(String fileName) {
        ClassLoader classLoader = SoundUtil.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);
        if (inputStream == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {
            return inputStream;
        }
    }
}