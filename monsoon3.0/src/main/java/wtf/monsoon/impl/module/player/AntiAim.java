package wtf.monsoon.impl.module.player;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.hypixel.api.reply.PlayerReply;
import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.entity.PlayerUtil;
import wtf.monsoon.api.util.misc.MathUtils;
import wtf.monsoon.impl.event.EventPacket;
import wtf.monsoon.impl.event.EventPreMotion;
import wtf.monsoon.impl.module.combat.Aura;

import java.util.Random;

public class AntiAim extends Module {

    private final Setting<ViewModes> viewModeProperty = new Setting<>("View Mode", ViewModes.CLIENT);

    private final Setting<Modes> modeProperty = new Setting<>("Mode", Modes.CUSTOM);

    //main antiaim
    private final Setting<Boolean> antiaimProperty = new Setting<>("Anti-Aim", false);

    private final Setting<PitchModes> pitchProperty = new Setting<>("Pitch", PitchModes.DOWN)
            .visibleWhen(antiaimProperty::getValue);

    private final Setting<YawModes> yawProperty = new Setting<>("Yaw Base", YawModes.BACKWARD)
            .visibleWhen(antiaimProperty::getValue);

    private final Setting<Integer> yawAddProperty = new Setting<>("Yaw Add", 0)
            .minimum(-179)
            .maximum(179)
            .incrementation(1)
            .visibleWhen(antiaimProperty::getValue);

    private final Setting<YawModModes> yawModProperty = new Setting<>("Yaw Modifier", YawModModes.JITTER)
            .visibleWhen(antiaimProperty::getValue);

    private final Setting<Integer> modSpinSpeedProperty = new Setting<>("Spin Speed", 60)
            .minimum(0)
            .maximum(100)
            .incrementation(1)
            .visibleWhen(() -> antiaimProperty.getValue() || yawModProperty.getValue().equals(YawModModes.JITTER));

    private final Setting<Integer> modJitterRangeProperty = new Setting<>("Jitter Base", 0)
            .minimum(-90)
            .maximum(90)
            .incrementation(1)
            .visibleWhen(() -> antiaimProperty.getValue() || yawModProperty.getValue().equals(YawModModes.SPIN));

    //fake angles
    private final Setting<Boolean> fakeAngleProperty = new Setting<>("Fake Angles", false);

    /*private final Setting<Boolean> inverterProperty = new Setting<>("Inverter", false)
            .visibleWhen(fakeAngleProperty::getValue);

    private final Setting<Integer> leftLimitProperty = new Setting<>("Left Limit", 0)
            .minimum(0)
            .maximum(60)
            .visibleWhen(fakeAngleProperty::getValue);

    private final Setting<Integer> rightLimitProperty = new Setting<>("Right Limit", 0)
            .minimum(0)
            .maximum(60)
            .visibleWhen(fakeAngleProperty::getValue);

    private final Setting<LBYModes> lbyModeProperty = new Setting<>("LBY Mode", LBYModes.SWAY)
            .visibleWhen(fakeAngleProperty::getValue);*/

    Random rand = new Random();
    private EntityOtherPlayerMP fakePlayer;
    private float[] lastAngles;
    private boolean tick;

    public AntiAim() {
        super("Anti Aim", "Automatically switch to the correct tool when mining a block.", Category.PLAYER);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if(fakeAngleProperty.getValue()){
            (fakePlayer = new EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.getGameProfile())).clonePlayer(mc.thePlayer, true);
            fakePlayer.setLocationAndAngles(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
            fakePlayer.rotationYawHead = mc.thePlayer.rotationYawHead;
            fakePlayer.setEntityId(1337);
            fakePlayer.setSneaking(mc.thePlayer.isSneaking());
            mc.theWorld.addEntityToWorld(fakePlayer.getEntityId(), fakePlayer);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (fakeAngleProperty.getValue() && fakePlayer != null){
            mc.theWorld.removeEntityFromWorld(fakePlayer.getEntityId());
            fakePlayer = null;
        }
    }

    @EventLink
    private final Listener<EventPreMotion> eventPreMotionListener = event -> {
        double posX = mc.thePlayer.posX;
        double posY = mc.thePlayer.posY;
        double posZ = mc.thePlayer.posZ;
        float pPitch = mc.thePlayer.rotationPitch;
        float pYaw = mc.thePlayer.rotationYaw;

        if (lastAngles == null) {
            lastAngles = new float[] { pYaw, pPitch };
        }

        if(mc.thePlayer.ticksExisted % 10 == 0)
            updateTick();

        switch(modeProperty.getValue()) {
            case VERUS:
                double valX = rand.nextDouble()/1.6;
                double valZ = rand.nextDouble()/1.6;

                if (mc.thePlayer.hurtTime >= 9) {
//                        Wrapper.setEventPosition(event,
//                                posX - valX,
//                                posY,
//                                posZ - valZ,
//                                false);
                    mc.thePlayer.setPosition(posX - valX,
                            posY,
                            posZ - valZ);
                }
                break;
            case CUSTOM:
                if(Wrapper.getModule(Aura.class).getTarget()!= null && mc.thePlayer.isSwingInProgress) { break; }
                if(antiaimProperty.getValue()) {
                    // pitch
                    switch(pitchProperty.getValue()) {
                        case UP:
                            updateServerPitch(event, -90);
                            updateClientPitch(-90);
                            break;
                        case DOWN:
                            updateServerPitch(event, 90);
                            updateClientPitch(90);
                            break;
                        case HALFDOWN:
                            updateServerPitch(event, 60);
                            updateClientPitch(60);
                            break;
                        case STUTTER:
                            if(!(mc.thePlayer.ticksExisted % 10 == 0)) {
                                updateServerPitch(event, 90);
                                updateClientPitch(90);
                            } else {
                                updateServerPitch(event, -45);
                                updateClientPitch(-45);
                            }
                            break;
                        case MEME:
                            float lastMeme = pPitch;

                            lastMeme += 10.0f;
                            if (lastMeme > 90.0f)
                                lastMeme = -90.0f;

                            updateServerPitch(event, lastMeme);
                            updateClientPitch(lastMeme);
                            break;
                        case NORMAL:
                            break;
                    }
                    // yaw base
                    switch(yawProperty.getValue()) {
                        case FORWARD:
                            updateServerYaw(event, lastAngles[0]);
                            updateClientYaw(lastAngles[0]);
                            break;
                        case BACKWARD:
                            //float backwardYaw = lastAngles[0] - 180.f;
                            float backwardYaw = MathHelper.wrapAngleTo180_float(pYaw - 180.f);
                            lastAngles = new float[] { MathHelper.wrapAngleTo180_float(backwardYaw), lastAngles[1] };
                            updateServerYaw(event, backwardYaw);
                            //updateClientYaw(backwardYaw);
                            pYaw = backwardYaw;
                            break;
                    }
                    // yaw add
                    // updateServerYaw(event, eYaw + yawAddProperty.getValue());
                    // updateClientYaw(pYaw + yawAddProperty.getValue());
                    pYaw += yawAddProperty.getValue();

                    // mc.thePlayer.isSneaking()

                    // yaw modifiers
                    switch(yawModProperty.getValue()) {
                        case NONE:
                            break;
                        case SPIN:
                            int speed = modSpinSpeedProperty.getValue();

                            //updateServerYaw(event, spinYaw);
                            //updateClientYaw(spinYaw);
                            pYaw = pYaw + speed;
                            break;
                        case JITTER:
                            int lower = modJitterRangeProperty.getValue() / -10;
                            int higher = modJitterRangeProperty.getValue() / 10;
                            //int jitterAdd = (int) (MathUtils.randomNumber(7, -7));

                            //updateServerYaw(event, eYaw+jitterAdd);
                            //updateClientYaw(pYaw+jitterAdd);
                            pYaw += (float) (MathUtils.randomNumber(7, -7));
                            break;
                        case LISP:
                            float lispYaw = pYaw + 150000.f;
                            lastAngles = new float[] { lispYaw, lastAngles[1] };
                            //updateServerYaw(event, lispYaw);
                            //updateClientYaw(lispYaw);
                            pYaw = lispYaw;
                            break;
                    }
                }

                mc.thePlayer.renderYawOffset = mc.thePlayer.rotationYawHead = pYaw;

                if(fakeAngleProperty.getValue()) {
                    if(fakePlayer == null) { break; }

                    if(mc.thePlayer.ticksExisted % 20 == 0) { // shitty fakelag kinda look
                        fakePlayer.setLocationAndAngles(event.getX(), event.getY(), event.getZ(), event.getYaw(), event.getPitch());
                        updateFakeAngles(fakePlayer, pPitch, pYaw);
                        fakePlayer.setSneaking(mc.thePlayer.isSneaking());
                    }
                } else {
                    if(fakePlayer != null) {
                        mc.theWorld.removeEntityFromWorld(fakePlayer.getEntityId());
                        fakePlayer = null;
                    }
                }
                break;
        }
    };

    private void updateTick() {
        this.tick = !this.tick;
    }
    private void updateServerPitch(EventPreMotion event, float pitch){
        if(viewModeProperty.getValue().equals(ViewModes.SERVER)) {
            event.setPitch(pitch);
        }
    }
    private void updateServerYaw(EventPreMotion event, float yaw){
        if(viewModeProperty.getValue().equals(ViewModes.SERVER)) {
            event.setYaw(yaw);
        }
    }
    private void updateClientPitch(float pitch) {
       mc.thePlayer.rotationPitchHead = pitch;
    }
    private void updateClientYaw(float yaw) {
        mc.thePlayer.rotationYawHead = yaw;
        mc.thePlayer.renderYawOffset = yaw;
    }

    private void updateFakeAngles(EntityOtherPlayerMP fake, float pitch, float yaw){
        fake.rotationPitchHead = pitch;
        fake.rotationYawHead = yaw;
        fake.renderYawOffset = yaw;
    }

    public boolean shouldRender() {
        return this.isEnabled() && !(Wrapper.getModule(Aura.class).getTarget() != null && mc.thePlayer.isSwingInProgress);
    }

    private enum Modes {
        CUSTOM("Custom"),
        VERUS("Verus");

        private final String name;

        Modes(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
    private enum PitchModes {
        UP("Up"),
        DOWN("Down"),
        HALFDOWN("Halfdown"),
        STUTTER("Stutter"),
        MEME("Meme"),
        NORMAL("Normal");

        private final String name;

        PitchModes(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
    private enum YawModes {
        FORWARD("Forward"),
        BACKWARD("Backward");

        private final String name;

        YawModes(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
    private enum YawModModes {
        NONE("None"),
        SPIN("Spin"),
        LISP("Lisp"),
        JITTER("Jitter");

        private final String name;

        YawModModes(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
    private enum ViewModes {
        CLIENT("Client"),
        SERVER("Server");

        private final String name;

        ViewModes(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
    private enum FakeOptionsModes {
        JITTER("Jitter"),
        IDK("idk");

        private final String name;

        FakeOptionsModes(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
    private enum LBYModes {
        OPPOSITE("Opposite"),
        SWAY("Sway");

        private final String name;

        LBYModes(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

}
