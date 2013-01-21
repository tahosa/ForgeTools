package forgetools.common;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.ModLoader;
import net.minecraft.world.WorldServer;
import cpw.mods.fml.common.FMLCommonHandler;

public class LoadedChunksCommand extends CommandBase
{

	public String getCommandName()
	{
		return "loadedchunks";
	}

	public String getCommandUsage(ICommandSender par1ICommandSender)
    {
    	return "/loadedchunks [detail | d]";
    }
	
	public void processCommand(ICommandSender sender, String[] args)
	{
		if(!FMLCommonHandler.instance().getEffectiveSide().isServer()) return;
		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
		
		MinecraftServer server = ForgeTools.server;
		boolean details = false;
		
		if(args.length > 1) throw new WrongUsageException(getCommandUsage(sender));
		else if (args.length == 1)
			if (args[0].equals("detail") || args[0].equals("d")) details = true;
		else throw new WrongUsageException(getCommandUsage(sender));
		
		int total = 0;
		for(WorldServer s : server.worldServers)
		{
			int amt = s.getPersistentChunks().keys().size();
			String prefix = (s.getWorldInfo().equals(player.worldObj.getWorldInfo())) ? "\u00a72" :  "" ;
			if (details) sender.sendChatToPlayer(prefix + amt + " force loaded chunks in "+s.provider.worldObj.getWorldInfo().getWorldName()+" " + s.provider.getDimensionName());
			total += amt;
		}
		
		if(!details) sender.sendChatToPlayer(total + " force loaded chunks in all worlds");
		
	}
	
	public boolean canCommandSenderUseCommand(ICommandSender sender)
	{
		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
		if (player.username.equalsIgnoreCase("Server") 
				|| ModLoader.getMinecraftServerInstance().getConfigurationManager().getOps().contains(player.username.trim().toLowerCase()) 
				|| ForgeTools.advancedUsers.contains(player.username.trim().toLowerCase()))
			return true;
		return false;
	}

}
