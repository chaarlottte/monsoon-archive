package wtf.monsoon.misc.asm;


import net.minecraft.client.main.Main;
import net.minecraft.launchwrapper.*;
import wtf.monsoon.misc.asm.transformer.MonsoonClassTransformer;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

public class Tweaker implements ITweaker {

    private final ArrayList<String> arguments = new ArrayList<>();
    private Set<String> exceptions;

    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String version) {
        this.arguments.addAll(args);
        if (gameDir != null) {
            this.arguments.add("--gameDir");
            this.arguments.add(gameDir.getAbsolutePath());
        }
        if (assetsDir != null) {
            this.arguments.add("--assetsDir");
            this.arguments.add(assetsDir.getAbsolutePath());
        }
        if (version != null) {
            this.arguments.add("--version");
            this.arguments.add(version);
        }

    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        classLoader.registerTransformer(MonsoonClassTransformer.class.getName());
        unlockLwjgl();
    }
    private void unlockLwjgl() {
        try {
            Field transformerExceptions = LaunchClassLoader.class.getDeclaredField("classLoaderExceptions");
            transformerExceptions.setAccessible(true);
            Object o = transformerExceptions.get(Launch.classLoader);
            ((Set<String>) o).remove("org.lwjgl.");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getLaunchTarget() {
        return Main.class.getName();
    }

    @Override
    public String[] getLaunchArguments() {
        return arguments.toArray(new String[0]);
    }
}