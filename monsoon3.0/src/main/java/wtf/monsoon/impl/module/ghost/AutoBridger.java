package wtf.monsoon.impl.module.ghost;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.impl.event.EventPreMotion;

public class AutoBridger extends Module {

    public AutoBridger() {
        super("Auto Bridger", "Automatically bridges for you.", Category.GHOST);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        mc.gameSettings.keyBindSneak.pressed = false;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        mc.gameSettings.keyBindSneak.pressed = false;
    }

    @EventLink
    private final Listener<EventPreMotion> eventPreMotionListener = e -> {
        mc.gameSettings.keyBindSneak.pressed = playerOverAir();
    };

    private boolean playerOverAir() {
        double x = mc.thePlayer.posX;
        double y = mc.thePlayer.posY - 1.0D;
        double z = mc.thePlayer.posZ;

        BlockPos p = new BlockPos(MathHelper.floor_double(x), MathHelper.floor_double(y), MathHelper.floor_double(z));

        return mc.theWorld.isAirBlock(p);
    }
}
