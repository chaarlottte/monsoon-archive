package wtf.monsoon.api.util.misc;

import net.minecraft.network.Packet;

public class PacketSleepThread extends Thread {

    private final Packet<?> packet;
    private final long delay;

    public PacketSleepThread(Packet<?> packet, long delay) {
        this.packet = packet;
        this.delay = delay;
    }

    @Override
    public void run() {
        try {
            sleep(this.delay);
            PacketUtil.sendPacketNoEvent(packet);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
