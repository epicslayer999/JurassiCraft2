package org.jurassicraft.common.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.jurassicraft.JurassiCraft;
import org.jurassicraft.common.entity.data.JCPlayerData;
import org.jurassicraft.common.paleopad.JCFile;

import java.util.ArrayList;
import java.util.List;

public class MessageSendFile implements IMessage
{
    // Send
    private JCFile file;
    private JCPlayerData playerData;

    // Receive
    private String path;
    private List<String> children = new ArrayList<String>();
    private NBTTagCompound data;
    private boolean isDir;

    public MessageSendFile()
    {
    }

    public MessageSendFile(JCPlayerData data, JCFile file)
    {
        this.file = file;
        this.playerData = data;
    }

    @Override
    public void toBytes(ByteBuf buffer)
    {
        if (file == null)
        {
            ByteBufUtils.writeUTF8String(buffer, "");

            List<JCFile> children = playerData.getFilesAtPath("");

            int i = 0;

            for (JCFile child : children)
            {
                if (child != null)
                {
                    i++;
                }
            }

            buffer.writeInt(i);

            for (JCFile child : children)
            {
                if (child != null)
                {
                    ByteBufUtils.writeUTF8String(buffer, child.getName());
                }
            }
        }
        else
        {
            ByteBufUtils.writeUTF8String(buffer, file.getPath());

            List<JCFile> children = file.getChildren();

            int i = 0;

            for (JCFile child : children)
            {
                if (child != null)
                {
                    i++;
                }
            }

            buffer.writeInt(i);
            buffer.writeBoolean(file.isDirectory());

            for (JCFile child : children)
            {
                if (child != null)
                {
                    ByteBufUtils.writeUTF8String(buffer, child.getName());
                }
            }

            if (file.isFile() && file.getData() != null)
            {
                ByteBufUtils.writeTag(buffer, file.getData());
            }
        }
    }

    @Override
    public void fromBytes(ByteBuf buffer)
    {
        path = ByteBufUtils.readUTF8String(buffer);

        if (path.length() > 0)
        {
            int childCount = buffer.readInt();
            isDir = buffer.readBoolean();

            for (int i = 0; i < childCount; i++)
            {
                String childName = ByteBufUtils.readUTF8String(buffer);

                children.add(childName);
            }

            if (!isDir)
            {
                data = ByteBufUtils.readTag(buffer);
            }
        }
        else
        {
            int childCount = buffer.readInt();
            isDir = true;

            for (int i = 0; i < childCount; i++)
            {
                String childName = ByteBufUtils.readUTF8String(buffer);

                children.add(childName);
            }
        }
    }

    public static class Handler implements IMessageHandler<MessageSendFile, IMessage>
    {
        @Override
        public IMessage onMessage(final MessageSendFile packet, final MessageContext ctx)
        {
            JurassiCraft.proxy.scheduleTask(ctx, new Runnable()
            {
                @Override
                public void run()
                {
                    if (ctx.side.isClient())
                    {
                        JCFile file = JCPlayerData.getPlayerData(null).getFileFromPath(packet.path);

                        if (!packet.isDir)
                        {
                            file.setData(packet.data);
                        }

                        for (String child : packet.children)
                        {
                            new JCFile(child, file, JurassiCraft.proxy.getPlayer(), packet.isDir);
                        }
                    }
                    else
                    {
                        EntityPlayerMP player = ctx.getServerHandler().playerEntity;

                        if (player != null)
                        {
                            JCFile file = JCPlayerData.getPlayerData(player).getFileFromPath(packet.path);

                            if (!packet.isDir)
                            {
                                file.setData(packet.data);
                            }

                            for (String child : packet.children)
                            {
                                new JCFile(child, file, player, packet.isDir);
                            }
                        }
                    }
                }
            });

            return null;
        }
    }
}
