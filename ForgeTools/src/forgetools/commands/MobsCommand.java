package forgetools.commands;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.INpc;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.ModLoader;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import cpw.mods.fml.common.FMLCommonHandler;
import forgetools.ForgeTools;
import forgetools.util.ItemChunkRef;

public class MobsCommand extends ForgeToolsGenericCommand
{
	private Date lastCheck = new Date();	// Last time the chunk list was updated
	private HashMap<Chunk, Integer> mobs = new HashMap<Chunk, Integer>(); 	// Chunk list
	private int total;	// Total number of items in all worlds
	
	public MobsCommand(String cmds)
	{
		super(cmds);
	}

	public String getCommandUsage(ICommandSender par1ICommandSender)
    {
    	return "/" + cmdName + " [detail | kill <passive | hostile | npc | all> [radius] ] [force]";
    }

	public void processCommand(ICommandSender sender, String[] args)
	{
		if(!FMLCommonHandler.instance().getEffectiveSide().isServer()) return;
		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
				
		MinecraftServer server = ForgeTools.server;
		int amtHos = 0, amtPas = 0, amtNPC = 0;
		boolean details = false, kill= false, force = false;
		String type = "";
		float radius = ForgeTools.killRadius;
		
		if (args.length > 4) throw new WrongUsageException(getCommandUsage(sender));
		
		if (args.length >= 1)
		{
			if(args[0].equalsIgnoreCase("detail") || args[0].equalsIgnoreCase("d"))
				details = true;
			else if (args[0].equalsIgnoreCase("kill") || args[0].equalsIgnoreCase("k"))
				kill = true;
			else if(args[0].equalsIgnoreCase("force") || args[0].equalsIgnoreCase("f"))
				force = true;
			else throw new WrongUsageException(getCommandUsage(sender));
		}
		if(args.length >= 2)
		{
			if(args[1].equalsIgnoreCase("force") || args[1].equalsIgnoreCase("f"))
				force = true;
			else if(args[1].equalsIgnoreCase("passive"))
				type = "passive";
			else if(args[1].equalsIgnoreCase("hostile"))
				type = "hostile";
			else if(args[1].equalsIgnoreCase("npc"))
				type = "npc";
			else if(args[1].equalsIgnoreCase("all"))
				type = "all";
			else throw new WrongUsageException(getCommandUsage(sender));
		}
		if(args.length >= 3)
		{
			if(args[2].equalsIgnoreCase("force") || args[2].equalsIgnoreCase("f"))
				force = true;
			else
			{
				try
				{
					radius = Float.parseFloat(args[2]);
				}
				catch(Exception e)
				{
					throw new WrongUsageException("Invalid radius.");
				}
			}
		}
		if(args.length == 4)
		{
			if(args[2].equalsIgnoreCase("force") || args[2].equalsIgnoreCase("f"))
				force = true;
			else throw new WrongUsageException(getCommandUsage(sender));
		}
		
		if(lastCheck.getTime() < new Date().getTime() - (ForgeTools.timeout * 1000) || force || kill)
		{
			mobs.clear();
			total = 0;
			lastCheck = new Date();
			
			for(WorldServer s : server.worldServers)
			{
				amtHos = 0;
				amtPas = 0;
				amtNPC = 0;
				boolean playerInWorld = s.getWorldInfo().equals(player.worldObj.getWorldInfo());
				int amtRemoved = 0;
				for(int id=0; id < s.loadedEntityList.size(); id++)
				{
					Object m = s.loadedEntityList.get(id);
					Chunk c;
					
					if(m instanceof EntityLiving)
					{
						c = s.getChunkFromChunkCoords(((EntityLiving)m).chunkCoordX,((EntityLiving)m).chunkCoordY);
						if(!c.isChunkLoaded)
							continue;
					}
					else
						continue;
					
					if(m instanceof EntityMob)
					{
						if(kill && type.equals("hostile") && ((EntityLiving)m).getDistanceToEntity(player) <= radius)
						{
							((EntityLiving) m).setDead();
							amtRemoved++;
						}
						else
							amtHos++;
					}
					else if((m instanceof IAnimals) && !(s.loadedEntityList.get(id) instanceof INpc))
					{
						if(kill && type.equals("passive") && ((EntityLiving)m).getDistanceToEntity(player) <= radius)
						{
							((EntityLiving) m).setDead();
							amtRemoved++;
						}
						else
							amtPas++;
					}
					else if(m instanceof INpc)
					{
						if(kill && type.equals("npc") && ((EntityLiving)m).getDistanceToEntity(player) <= radius)
						{
							((EntityLiving) m).setDead();
							amtRemoved++;
						}
						else
							amtNPC++;
					}
					
					if(!kill)
					{
						if(mobs.get(c) == null)
						{
							mobs.put(c, 1);	
						}
						else
						{
							mobs.put(c, mobs.get(c) + 1);
						}
					}
				}
				
				String prefix = (playerInWorld) ? "\u00a72" :  "" ;
				if(details)
					sender.sendChatToPlayer(ChatMessageComponent.createFromText(prefix + amtHos + " hostile, " + amtPas + " passive, and " + amtNPC + " NPCs spawned in "+s.provider.worldObj.getWorldInfo().getWorldName()+" " + s.provider.getDimensionName()));					
				
				if(kill)
					sender.sendChatToPlayer(ChatMessageComponent.createFromText(prefix + amtRemoved + " " + type + " mobs removed from "+s.provider.worldObj.getWorldInfo().getWorldName()+" " + s.provider.getDimensionName()));
				
				total += amtHos + amtPas + amtNPC;
			}
			sender.sendChatToPlayer(ChatMessageComponent.createFromText(total + " mobs in all worlds."));
			if(details)
			{
				// Send extra info if details are needed
				sender.sendChatToPlayer(ChatMessageComponent.createFromText("Top 5 chunks by mob count:"));
				ItemChunkRef[] sortedList = ItemChunkRef.getSortedChunkList(mobs);
				for (int i = 0; i < sortedList.length && i < 5; ++i)
				{
					ItemChunkRef cr = sortedList[i];
					Chunk c = cr.getChunk();
					String worldName = c.worldObj.getWorldInfo().getWorldName();
					String dimName = c.worldObj.provider.getDimensionName();
					sender.sendChatToPlayer(ChatMessageComponent.createFromText(worldName + " " + dimName + " (" + (c.xPosition * 16) + ", " + (c.zPosition * 16) + ") " + cr.getValue() + " mobs"));
				}
			}
		}
	}
	
	public boolean canCommandSenderUseCommand(ICommandSender sender)
	{
		return hasEnhancedPermissions(sender);
	}
}
