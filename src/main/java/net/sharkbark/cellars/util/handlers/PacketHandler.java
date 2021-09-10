package net.sharkbark.cellars.util.handlers;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.sharkbark.cellars.util.Reference;

public class PacketHandler
{
    public static final SimpleNetworkWrapper packetReq = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MOD_ID);

    public static void init()
    {
        packetReq.registerMessage(FDPacket.Handler.class, FDPacket.class, 0, Side.SERVER);
    };
}