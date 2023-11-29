package wtf.monsoon.api.setting;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.util.obj.MonsoonPlayerObject;

import java.util.ArrayList;
import java.util.List;

public class ModeProcessor {

    protected final Minecraft mc = Minecraft.getMinecraft();
    protected final MonsoonPlayerObject player = Wrapper.getMonsoon().getPlayer();
    @Getter @Setter private Module parentModule;


    public ModeProcessor(Module parentModule) {
        this.parentModule = parentModule;
    }

    public void onEnable() { Wrapper.getEventBus().subscribe(this); }
    public void onDisable() { Wrapper.getEventBus().unsubscribe(this); }
    public Setting[] getModeSettings() { return new Setting[] { }; }
}