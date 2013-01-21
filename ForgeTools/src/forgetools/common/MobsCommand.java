package forgetools.common;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.INpc;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.ModLoader;
import net.minecraft.world.WorldServer;
import cpw.mods.fml.common.FMLCommonHandler;

public class MobsCommand extends CommandBase
{
	public String getCommandName()
	{
		return "mobs";
	}
	
	public String getCommandUsage(ICommandSender par1ICommandSender)
    {
    	return "/mobs [detail | total]";
    }

	public void processCommand(ICommandSender sender, String[] args)
	{
		if(!FMLCommonHandler.instance().getEffectiveSide().isServer()) return;
		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
				
		MinecraftServer server = ForgeTools.server;
		int amtHos = 0, amtPas = 0, amtNPC = 0;
		boolean details = false, totalArg = false;
		
		if (args.length > 1) throw new WrongUsageException(getCommandUsage(sender));
		else if (args.length == 1)
		{
			if(args[0].equals("detail") || args[0].equals("d"))
				details = true;
			else if (args[0].equals("total") || args[0].equals("t"))
				totalArg = true;
			else throw new WrongUsageException(getCommandUsage(sender));
		}		
		
		for(WorldServer s : server.worldServers)
		{
			amtHos = 0;
			amtPas = 0;
			amtNPC = 0;
			boolean playerInWorld = s.getWorldInfo().equals(player.worldObj.getWorldInfo());
			int amt = 0;
			for(int id=0; id < s.loadedEntityList.size(); id++)
			{
				
				if(s.loadedEntityList.get(id) instanceof EntityMob) amtHos++;
				else if((s.loadedEntityList.get(id) instanceof IAnimals) && !(s.loadedEntityList.get(id) instanceof INpc)) amtPas++;
				else if(s.loadedEntityList.get(id) instanceof INpc) amtNPC++;				
			}
			String prefix = (playerInWorld) ? "\u00a72" :  "" ;
			if (!details && !totalArg && (amtHos + amtPas + amtNPC) > 0)
				sender.sendChatToPlayer(prefix + (amtHos + amtPas + amtNPC) + " creatures spawned in "+s.provider.worldObj.getWorldInfo().getWorldName()+" " + s.provider.getDimensionName());
			else if (details && (amtHos + amtPas + amtNPC) > 0)
				sender.sendChatToPlayer(prefix + amtHos + " hostile, " + amtPas + " passive, and " + amtNPC + " NPCs spawned in "+s.provider.worldObj.getWorldInfo().getWorldName()+" " + s.provider.getDimensionName());
		}
		if (totalArg)
			sender.sendChatToPlayer(amtHos + " hostile, " + amtPas + " passive, and " + amtNPC + " NPCs spawned across all worlds");
	}
	
	public boolean canCommandSenderUseCommand(ICommandSender sender)
	{
		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
		if (!player.username.equalsIgnoreCase("Server") && !ModLoader.getMinecraftServerInstance().getConfigurationManager().getOps().contains(player.username.trim().toLowerCase()))
			return false;
		return true;
	}
}
