package net.minecraft.client.main;

import net.minecraft.launchwrapper.Launch;
import wtf.monsoon.launch.Tweaker;

import java.util.Arrays;

public class WrappedLauncher {

    public static void main(String[] args) {
        String[] thing = new String[] { "--tweakClass", Tweaker.class.getName(), "launchedCorrectlyYouMonkey" };
        Launch.main(concat(args, thing));
    }

    public static <T> T[] concat(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

}