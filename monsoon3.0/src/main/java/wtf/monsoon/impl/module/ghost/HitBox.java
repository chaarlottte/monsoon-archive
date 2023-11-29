package wtf.monsoon.impl.module.ghost;

import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;

public class HitBox extends Module {

    public Setting<Float> expand = new Setting<>("Expand", 1.0F)
            .minimum(0.1F)
            .maximum(3.0F)
            .incrementation(0.1F)
            .describedBy("The multiplication factor of the entity's hitbox");

    public HitBox() {
        super("Hit Box", "Expands the entity hitboxes", Category.GHOST);
    }

}
