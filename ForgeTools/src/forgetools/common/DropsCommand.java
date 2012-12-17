package forgetools.common;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.CommandBase;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.ModLoader;
import net.minecraft.src.WorldServer;
import cpw.mods.fml.common.FMLCommonHandler;

public class DropsCommand extends CommandBase
{

	@Override
	public String getCommandName()
	{
		return "drops";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args)
	{
		if(!FMLCommonHandler.instance().getEffectiveSide().isServer()) return;
		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
		if (!player.username.equalsIgnoreCase("Server") && !ModLoader.getMinecraftServerInstance().getConfigurationManager().getOps().contains(player.username.trim().toLowerCase()))
		{
			sender.sendChatToPlayer("\u00a74You do not have permission to use the /drops command.");
			return;
		}
		
		MinecraftServer server = ForgeTools.server;
		
		int total = 0;
		for(WorldServer s : server.worldServers)
		{
			int amt = 0;
			for(int id=0; id < s.loadedEntityList.size(); id++)
			{
				if(s.loadedEntityList.get(id) instanceof EntityItem) amt++;
			}
			String prefix = (s.getWorldInfo().equals(player.worldObj.getWorldInfo())) ? "\u00a72" :  "" ;
			if (amt > 0) sender.sendChatToPlayer(prefix + amt + " loose items in world "+s.provider.worldObj.getWorldInfo().getWorldName()+" " + s.provider.getDimensionName());
			total += amt;
		}
		
		if (total == 0) sender.sendChatToPlayer("No dropped items in any world");
	}
}
