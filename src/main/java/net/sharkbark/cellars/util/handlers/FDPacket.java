package net.sharkbark.cellars.util.handlers;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.sharkbark.cellars.blocks.tileentity.TEFreezeDryer;

public class FDPacket implements IMessage
{


    private int xCoord;
    private int yCoord;
    private int zCoord;
    private int bool;
    private boolean mode;

    public FDPacket() { }

    public FDPacket(int xCoord, int yCoord, int zCoord, int bool, boolean mode)
    {
        System.out.println("Packet Readout: " + xCoord);
        System.out.println("Packet Readout: " + yCoord);
        System.out.println("Packet Readout: " + zCoord);
        System.out.println("Packet Readout: " + bool);
        System.out.println("Packet Readout: " + mode);
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.zCoord = zCoord;
        this.bool = bool;
        this.mode = mode;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        xCoord = buf.readInt();
        yCoord = buf.readInt();
        zCoord = buf.readInt();
        bool = buf.readInt();
        mode = buf.readBoolean();
        System.out.println("Packet Readout: " + xCoord);
        System.out.println("Packet Readout: " + yCoord);
        System.out.println("Packet Readout: " + zCoord);
        System.out.println("Packet Readout: " + bool);
        System.out.println("Packet Readout: " + mode);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        System.out.println("Packet Readout: " + xCoord);
        System.out.println("Packet Readout: " + yCoord);
        System.out.println("Packet Readout: " + zCoord);
        System.out.println("Packet Readout: " + bool);
        System.out.println("Packet Readout: " + mode);
        buf.writeInt(xCoord);
        buf.writeInt(yCoord);
        buf.writeInt(zCoord);
        buf.writeInt(bool);
        buf.writeBoolean(mode);
    }

    public static class Handler implements IMessageHandler<FDPacket, IMessage>
    {
        @Override
        public IMessage onMessage(FDPacket msg, MessageContext ctx)
        {
            System.out.println("Packet Readout: " + msg.xCoord);
            System.out.println("Packet Readout: " + msg.yCoord);
            System.out.println("Packet Readout: " + msg.zCoord);
            System.out.println("Packet Readout: " + msg.bool);
            System.out.println("Packet Readout: " + msg.mode);

            if (ctx.side == Side.SERVER) {
                TileEntity tile = ctx.getServerHandler().player.world.getTileEntity(new BlockPos(msg.xCoord, msg.yCoord, msg.zCoord));
                TEFreezeDryer freezeDryer = (TEFreezeDryer) tile;

                if (msg.bool == 0) {
                    if (msg.mode) {
                        System.out.println("Server Sealed Freeze Dryer");
                        freezeDryer.seal();
                    } else{
                        System.out.println("Server Unsealed Freeze Dryer");
                        freezeDryer.unseal();
                    }
                }
                if (msg.bool == 1) {
                    if (msg.mode) {
                        System.out.println("Server Started Freeze Dryer Pump");
                        freezeDryer.startPump();
                    } else {
                        System.out.println("Server Stopped Freeze Dryer Pump");
                        freezeDryer.stopPump();
                    }
                }
            }
            return null;
        }
    }
}