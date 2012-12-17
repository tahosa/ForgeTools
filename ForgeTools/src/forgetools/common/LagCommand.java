package forgetools.common;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.CommandBase;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.ModLoader;
import cpw.mods.fml.common.FMLCommonHandler;

public class LagCommand extends CommandBase
{
	public String getCommandName()
	{
		return "lag";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args)
	{
		if(!FMLCommonHandler.instance().getEffectiveSide().isServer()) return;
		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
		if (!player.username.equalsIgnoreCase("Server") && !ModLoader.getMinecraftServerInstance().getConfigurationManager().getOps().contains(player.username.trim().toLowerCase()))
		{
			sender.sendChatToPlayer("\u00a74You do not have permission to use the /lag command.");
			return;
		}		
		
		MinecraftServer server = ForgeTools.server;
		double tickMS = Math.round(avgTick(server.tickTimeArray)*1.0E-5D)/10d;
		double tickPct = (tickMS < 50) ? 100d : Math.round(50d/tickMS * 100);
		double tps = (tickMS < 50) ? 20d : Math.round(1000d/tickMS * 10d)/10d;
		sender.sendChatToPlayer("Tick: "+tickMS + "ms ("+tps+" tps, "+ tickPct+"%)");
		
	}
	
	private double avgTick(long[] serverTickArray)
	{
		long sum = 0L;
		long[] svTicks = serverTickArray;
		int size = serverTickArray.length;
		
		for(int i = 0; i<size; i++)
			sum += svTicks[i];
		
		return (double)sum / (double) size;
	}
}
