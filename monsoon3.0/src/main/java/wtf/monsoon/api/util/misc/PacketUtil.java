package wtf.monsoon.api.util.misc;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.client.Minecraft;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import viamcp.ViaMCP;
import wtf.monsoon.api.util.Util;
import wtf.monsoon.api.util.entity.PlayerUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class PacketUtil extends Util {

    public static void sendPacket(Packet<?> p) {
        mc.getNetHandler().addToSendQueue(p);
    }

    public static void sendPacketNoEvent(Packet<?> p) {
        mc.getNetHandler().addToSendQueueNoEvent(p);
    }

    public static void processPacket(Packet<INetHandler> packet) {
        // mc.getNetHandler().addToSendQueue(p);
        if (mc.getNetHandler().getNetworkManager() != null && mc.getNetHandler().getNetworkManager().channel != null && mc.getNetHandler().getNetworkManager().channel.isOpen())
            packet.processPacket(mc.getNetHandler().getNetworkManager().packetListener);
    }

    public static void sendFunnyPacket() {
        sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(Minecraft.getMinecraft().thePlayer.posX, Math.PI / 100E-10d, Minecraft.getMinecraft().thePlayer.posZ, false));
    }

    public static boolean isInbound(Packet<?> packet) {
        for (Type type : packet.getClass().getGenericInterfaces()) {
            if (type instanceof ParameterizedType) {
                ParameterizedType paramType = (ParameterizedType) type;
                if (Packet.class.equals(paramType.getRawType())) {
                    Type[] typeArgs = paramType.getActualTypeArguments();
                    if (typeArgs.length > 0 && typeArgs[0] instanceof Class) {
                        Class<?> typeArgClass = (Class<?>) typeArgs[0];
                        return INetHandlerPlayClient.class.isAssignableFrom(typeArgClass);
                    }
                }
            }
        }
        return false;
    }

    public static boolean isOutbound(Packet<?> packet) {
        for (Type type : packet.getClass().getGenericInterfaces()) {
            if (type instanceof ParameterizedType) {
                ParameterizedType paramType = (ParameterizedType) type;
                if (Packet.class.equals(paramType.getRawType())) {
                    Type[] typeArgs = paramType.getActualTypeArguments();
                    if (typeArgs.length > 0 && typeArgs[0] instanceof Class) {
                        Class<?> typeArgClass = (Class<?>) typeArgs[0];
                        return INetHandlerPlayServer.class.isAssignableFrom(typeArgClass);
                    }
                }
            }
        }
        return false;
    }


    public static float fixRightClick() {
        if (ViaMCP.getInstance().getVersion() == ViaMCP.PROTOCOL_VERSION) {
            return 16.0F;
        } else {
            return 1.0F;
        }
    }

    public static void sendBlocking(boolean callEvent, boolean placement) {
        if(mc.thePlayer == null)
            return;

        if(placement) {
            C08PacketPlayerBlockPlacement packet = new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.getHeldItem(), 0, 0, 0);

            if(callEvent) {
                sendPacket(packet);
            } else {
                sendPacketNoEvent(packet);
            }
        } else {
            C08PacketPlayerBlockPlacement packet = new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem());
            if(callEvent) {
                sendPacket(packet);
            } else {
                sendPacketNoEvent(packet);
            }
        }
    }

    public static void releaseUseItem(boolean callEvent) {
        if(mc.thePlayer == null)
            return;

        C07PacketPlayerDigging packet = new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN);
        if(callEvent) {
            sendPacket(packet);
        } else {
            sendPacketNoEvent(packet);
        }
    }


}
