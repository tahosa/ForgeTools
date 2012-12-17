package forgetools.common;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.CommandBase;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.ModLoader;
import net.minecraft.src.WorldServer;
import net.minecraft.src.WrongUsageException;
import cpw.mods.fml.common.FMLCommonHandler;

public class DropsCommand extends CommandBase
{

	@Override
	public String getCommandName()
	{
		return "drops";
	}

	public String getCommandUsage(ICommandSender par1ICommandSender)
    {
    	return "/drops [detail | kill | killall]";
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
		boolean details = false,  kill = false, killall = false;
		
		if (args.length > 1) throw new WrongUsageException(getCommandUsage(sender));
		else if (args.length == 1)
		{
			if(args[0].equals("detail"))
				details = true;
			else if (args[0].equals("kill"))
				kill = true;
			else if (args[0].equals("killall"))
				killall = true;
			else throw new WrongUsageException(getCommandUsage(sender));
		}
		
		int total = 0;
		for(WorldServer s : server.worldServers)
		{
			boolean playerInWorld = s.getWorldInfo().equals(player.worldObj.getWorldInfo());
			int amt = 0;
			for(int id=0; id < s.loadedEntityList.size(); id++)
			{
				
				if(s.loadedEntityList.get(id) instanceof EntityItem)
				{
					if((playerInWorld && kill) || killall)
					{
						EntityItem i = (EntityItem)s.loadedEntityList.get(id);
						if(i.addedToChunk) 
							i.setDead();
					}
					amt++;
				}
				
			}
			String prefix = (playerInWorld) ? "\u00a72" :  "" ;
			if(playerInWorld && kill) sender.sendChatToPlayer(amt + " items were removed from "+s.provider.worldObj.getWorldInfo().getWorldName()+" " + s.provider.getDimensionName());
			if (details) sender.sendChatToPlayer(prefix + amt + " loose items in "+s.provider.worldObj.getWorldInfo().getWorldName()+" " + s.provider.getDimensionName());
			total += amt;
		}
		
		if (killall) sender.sendChatToPlayer(total + " items were removed from all worlds");
		if (!(details || kill || killall)) sender.sendChatToPlayer(total + " loose items in all worlds");
	}
}
