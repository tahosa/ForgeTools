package forgetools.common;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.CommandBase;
import net.minecraft.src.EntityAnimal;
import net.minecraft.src.EntityCreature;
import net.minecraft.src.EntityMob;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.IAnimals;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.INpc;
import net.minecraft.src.ModLoader;
import net.minecraft.src.WorldServer;
import cpw.mods.fml.common.FMLCommonHandler;

public class MobsCommand extends CommandBase
{
	@Override
	public String getCommandName()
	{
		return "mobs";
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
		
		
		if (args.length == 0) {		// No arguments show general stats about loaded mobs
			for(WorldServer s : server.worldServers)
			{
				int amt = 0;
				for(int id=0; id < s.loadedEntityList.size(); id++)
				{
					if(s.loadedEntityList.get(id) instanceof EntityCreature) amt++;
				}
				String prefix = (s.getWorldInfo().equals(player.worldObj.getWorldInfo())) ? "\u00a72" :  "" ;
				if (amt > 0) sender.sendChatToPlayer(prefix + amt + " creatures spawned in world "+s.provider.worldObj.getWorldInfo().getWorldName()+" " + s.provider.getDimensionName());
				total += amt;
			}

			if (total == 0) sender.sendChatToPlayer("No creatures spawned in any world");
		} else if (args[0].equals("detail")) {		// Show a breakdown of the types of loaded mobs
			for(WorldServer s : server.worldServers)
			{
				int amtHos = 0, amtPas = 0, amtNPC = 0;
				for(int id=0; id < s.loadedEntityList.size(); id++)
				{
					if(s.loadedEntityList.get(id) instanceof EntityMob) amtHos++;
					else if((s.loadedEntityList.get(id) instanceof IAnimals) && !(s.loadedEntityList.get(id) instanceof INpc)) amtPas++;
					else if(s.loadedEntityList.get(id) instanceof INpc) amtNPC++;
				}
				String prefix = (s.getWorldInfo().equals(player.worldObj.getWorldInfo())) ? "\u00a72" :  "" ;
				if ((amtHos + amtPas + amtNPC) > 0) sender.sendChatToPlayer(prefix + amtHos + " hostile, " + amtPas + " passive, and " + amtNPC + " NPCs spawned in world "+s.provider.worldObj.getWorldInfo().getWorldName()+" " + s.provider.getDimensionName());
				total += (amtHos + amtPas + amtNPC);
			}
			
			if (total == 0) sender.sendChatToPlayer("No creatures spawned in any world");
		} else if (args[0].equals("total")) {		// Show a breakdown of the types of loaded mobs
			int amtHos = 0, amtPas = 0, amtNPC = 0;
			for(WorldServer s : server.worldServers)
			{
				for(int id=0; id < s.loadedEntityList.size(); id++)
				{
					if(s.loadedEntityList.get(id) instanceof EntityMob) amtHos++;
					else if((s.loadedEntityList.get(id) instanceof IAnimals) && !(s.loadedEntityList.get(id) instanceof INpc)) amtPas++;
					else if(s.loadedEntityList.get(id) instanceof INpc) amtNPC++;
				}				
			}
			if ((amtHos + amtPas + amtNPC) > 0) sender.sendChatToPlayer(amtHos + " hostile, " + amtPas + " passive, and " + amtNPC + " NPCs spawned across all worlds");
			total += (amtHos + amtPas + amtNPC);
			
			if (total == 0) sender.sendChatToPlayer("No creatures spawned in any world");
		} else {		// Unknown argument display message
			String prefix = "\u00a7c";
			sender.sendChatToPlayer(prefix + "Unrecongized argument " + args[0]);
		}
	}
}
