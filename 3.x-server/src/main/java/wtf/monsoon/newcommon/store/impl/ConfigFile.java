package wtf.monsoon.newcommon.store.impl;

import wtf.monsoon.newcommon.store.File;

import java.awt.*;

public class ConfigFile extends File {
    public ConfigFile(String name, String description, Image image) {
        super(name, description, image);
    }

    public ConfigFile(String name, String description) {
        super(name, description);
    }
}
