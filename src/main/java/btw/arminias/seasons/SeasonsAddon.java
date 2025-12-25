package btw.arminias.seasons;

import api.AddonHandler;
import api.BTWAddon;
import api.config.AddonConfig;
import btw.BTWMod;
import btw.arminias.seasons.entity.EntityFXLeaf;
import api.world.data.DataEntry;
import api.world.data.DataProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.List;
import java.util.Map;

import static net.minecraft.src.CommandBase.getListOfStringsMatchingLastWord;

public class SeasonsAddon extends BTWAddon {
    public static final DataEntry.WorldDataEntry<Integer> YEAR_LENGTH = DataProvider.getBuilder(Integer.class)
            .name("year_length")
            .defaultSupplier(() -> -1)
            .readNBT(NBTTagCompound::getInteger)
            .writeNBT(NBTTagCompound::setInteger)
            .global()
            .build();
    private AddonConfig config;

    public SeasonsAddon() {
        super();
    }

    @Override
    public void initialize() {
        AddonHandler.logMessage(this.getName() + " Version " + this.getVersionString() + " Initializing...");
        if (!MinecraftServer.getIsServer()) {
            initClientPacketInfo();
        }
        registerAddonCommand(new CommandBase() {
            @Override
            public String getCommandName() {
                return "seasons";
            }

            @Override
            public String getCommandUsage(ICommandSender iCommandSender) {
                return "/seasons year_length <length>";
            }

            @Override
            public void processCommand(ICommandSender iCommandSender, String[] strings) {
                if (strings.length == 2 && strings[0].equals("year_length")) {
                    try {
                        int yearLength = Integer.parseInt(strings[1]);
                        MinecraftServer.getServer().worldServers[0].setData(YEAR_LENGTH, yearLength);
                        SeasonsAddonMod.YEAR_LENGTH_VALUE_S = yearLength;
                        SeasonsAddonMod.YEAR_LENGTH_VALUE = yearLength;
                        sendYearLengthToAllPlayers();
                    } catch (NumberFormatException e) {
                        throw new WrongUsageException("Invalid year length.");
                    }
                } else if (strings.length == 1 && strings[0].equals("year_length")) {
                    iCommandSender.sendChatToPlayer(ChatMessageComponent.createFromText("Year length: " + MinecraftServer.getServer().worldServers[0].getData(YEAR_LENGTH)));
                }
                else {
                    throw new WrongUsageException(getCommandUsage(iCommandSender));
                }
            }

            @Override
            public boolean canCommandSenderUseCommand(ICommandSender iCommandSender) {
                return iCommandSender.canCommandSenderUseCommand(4, getCommandName());
            }

            @Override
            public List addTabCompletionOptions(ICommandSender iCommandSender, String[] strings) {
                return strings.length == 1 ? getListOfStringsMatchingLastWord(strings, "year_length") : null;
            }

        });
    }

    @Environment(EnvType.CLIENT)
    private void initClientPacketInfo() {
        AddonHandler.registerPacketHandler("se|settings", (packet, player) -> {
            DataInputStream dataStream = new DataInputStream(new ByteArrayInputStream(packet.data));
            int yearLength = -1;
            try {
                yearLength = dataStream.readInt();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (yearLength != -1) {
                player.worldObj.setData(YEAR_LENGTH, yearLength);
                SeasonsAddonMod.YEAR_LENGTH_VALUE = yearLength;
            }
        });
    }

    @Override
    public void serverPlayerConnectionInitialized(NetServerHandler serverHandler, EntityPlayerMP playerMP) {
        sendYearLengthToClient(serverHandler);
    }

    private static void sendYearLengthToClient(NetServerHandler serverHandler) {
        Packet250CustomPayload packet = createSeasonSettingsPacket();
        serverHandler.sendPacketToPlayer(packet);
    }

    private static Packet250CustomPayload createSeasonSettingsPacket() {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream dataStream = new DataOutputStream(byteStream);

        try {
            dataStream.writeInt(MinecraftServer.getServer().worldServers[0].getData(YEAR_LENGTH));
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        Packet250CustomPayload packet = new Packet250CustomPayload("se|settings", byteStream.toByteArray());
        return packet;
    }

    public static void sendYearLengthToAllPlayers() {
        Packet250CustomPayload packet = createSeasonSettingsPacket();
        for (Object player : MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
            if (player instanceof EntityPlayerMP) {
                ((EntityPlayerMP) player).playerNetServerHandler.sendPacketToPlayer(packet);
            }
        }
    }

    @Override
    public void preInitialize() {
        YEAR_LENGTH.register();
    }

    @Override
    public void registerConfigProperties(AddonConfig config) {
        config.registerBoolean("FORCE_SNOW_REWORK", true, "If true, enables the snow rework feature regardless of other settings.");
    }

    @Override
    public void handleConfigProperties(AddonConfig config) {
        this.config = config;
        try {
            BTWMod.enableSnowRework |= this.config.getBoolean("FORCE_SNOW_REWORK");
        } catch (NumberFormatException e) {
               e.printStackTrace();
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public EntityFX spawnCustomParticle(World world, String particleType, double x, double y, double z, double velX, double velY, double velZ) {
        if (particleType.equals("seasons_leaf")) {
            return new EntityFXLeaf(world, x, y, z, velX, velY, velZ);
        }
        return null;
    }
}
