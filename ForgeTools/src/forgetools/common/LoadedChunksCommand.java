package forgetools.common;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.CommandBase;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.WorldServer;

public class LoadedChunksCommand extends CommandBase
{

	@Override
	public String getCommandName()
	{
		return "loadedchunks";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args)
	{
		if(!FMLCommonHandler.instance().getEffectiveSide().isServer()) return;
		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
		
		MinecraftServer server = ForgeTools.server;
		
		int total = 0;
		for(WorldServer s : server.worldServers)
		{
			int amt = s.getPersistentChunks().keys().size();
			String prefix = (s.getWorldInfo().equals(player.worldObj.getWorldInfo())) ? "\u00a72" :  "" ;
			if (amt > 0) sender.sendChatToPlayer(prefix + amt + " force loaded chunks in world "+s.provider.worldObj.getWorldInfo().getWorldName()+" " + s.provider.getDimensionName());
			total += amt;
		}
		
		if (total == 0) sender.sendChatToPlayer("No force loaded chunks");
		
	}

}
