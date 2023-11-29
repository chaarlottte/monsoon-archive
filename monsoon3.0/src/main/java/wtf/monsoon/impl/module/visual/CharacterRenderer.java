package wtf.monsoon.impl.module.visual;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;

public class CharacterRenderer extends Module {

    @Getter
    private final Setting<Image> image = new Setting<>("Style", Image.ASTOLFO)
            .describedBy("The image to appear in GUIs.");

    private final Setting<Boolean> showImagesInMinecraftGuis = new Setting<>("Show in Minecraft GUIs", true)
            .describedBy("Whether to show the images in Minecraft GUIs.");

    public CharacterRenderer() {
        super("Character", "Render a character in GUIs.", Category.VISUAL);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    public boolean renderInMinecraftGuis() {
        return  showImagesInMinecraftGuis.getValue();
    }

    @AllArgsConstructor
    public enum Image {
        ASTOLFO("Astolfo"),
        FELIX("Felix"),
        BARRY("Barry"),
        SIMON("Simon"),
        BLAHAJ("BLÅHAJ"),
        TEE_GRIZZLY("Tee Grizzly (absolute retarded nn)"),
        KOBLEY("Kobley"),
        CONFIG_ISSUE("Config Issue"),
        SKEPPY("Skeppy"),
        MR_WOOD("Mr. Wood"),
        HAIKU("haiku :3");

        String character;

        @Override
        public String toString() {
            return character;
        }
    }
}
