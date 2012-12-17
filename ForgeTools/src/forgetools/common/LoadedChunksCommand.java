package forgetools.common;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.CommandBase;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.WorldServer;
import net.minecraft.src.WrongUsageException;

public class LoadedChunksCommand extends CommandBase
{

	@Override
	public String getCommandName()
	{
		return "loadedchunks";
	}

	public String getCommandUsage(ICommandSender par1ICommandSender)
    {
    	return "/loadedchunks [detail]";
    }
	
	@Override
	public void processCommand(ICommandSender sender, String[] args)
	{
		if(!FMLCommonHandler.instance().getEffectiveSide().isServer()) return;
		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
		
		MinecraftServer server = ForgeTools.server;
		boolean details = false;
		
		if(args.length > 1) throw new WrongUsageException(getCommandUsage(sender));
		else if (args.length == 1)
			if (args[0].equals("detail")) details = true;
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

}
