package forgetools.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.ModLoader;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import cpw.mods.fml.common.FMLCommonHandler;

public class DropsCommand extends ForgeToolsGenericCommand
{
	protected class ChunkRefComparator implements Comparator<Object>
	{
		@Override
		public int compare(Object o1, Object o2) {
			if(!(o1 instanceof Comparable))
				return 0;
			return ((Comparable)o1).compareTo(o2);
		}
		
	}
	
	protected class ChunkRef implements Comparable
	{
		private Chunk _chunk;
		private int _val;
		
		public ChunkRef(Chunk c, int value)
		{
			_chunk = c;
			_val = value;
		}
		
		public Chunk getChunk()
		{
			return _chunk;
		}
		
		public int getValue()
		{
			return _val;
		}
		
		@Override
		public int compareTo(Object o)
		{
			if(o instanceof ChunkRef)
			{
				ChunkRef c = (ChunkRef)o;
				if (c.getValue() < this.getValue())
					return -1;
				else if(c.getValue() == this.getValue())
					return 0;
				else return 1;
			}
			
			return 0;
		}
	}
	
	private Date lastCheck = new Date();
	private HashMap<Chunk, Integer> items = new HashMap<Chunk, Integer>(); 
	private int total;
	
	public DropsCommand(String cmds)
	{
		super(cmds);
	}
	
	public String getCommandUsage(ICommandSender par1ICommandSender)
    {
    	return "/" + cmdName + " [ [detail [force] ] | [kill [radius] ] | killall]";
    }
	
	public void processCommand(ICommandSender sender, String[] args)
	{
		if(!FMLCommonHandler.instance().getEffectiveSide().isServer()) return;
		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
				
		MinecraftServer server = ForgeTools.server;
		boolean details = false,  kill = false, killall = false, force = false;
		float radius = ForgeTools.killRadius;
		
		if(args.length >= 1)
		{
			if(args[0].equals("detail") || args[0].equals("d"))
			{
				details = true;
				if(args.length == 2 && (args[1].equals("force") || args[1].equals("f")))
					force = true;
				else if (args.length != 1)
					throw new WrongUsageException(getCommandUsage(sender));
			}
			else if(args[0].equals("kill") || args[0].equals("k"))
			{
				kill = true;
				if(args.length == 2)
				{
					try
					{
						radius = Float.parseFloat(args[1]);
					}
					catch(Exception e)
					{
						throw new WrongUsageException("Invalid radius.");
					}
				}
			}
			else if(args[0].equals("killall"))
				killall = true;
			else throw new WrongUsageException(getCommandUsage(sender));
		}
		
		if(lastCheck.getTime() < new Date().getTime() - (ForgeTools.timeout * 1000) || force || kill || killall)
		{
			items.clear();
			total = 0;
			
			lastCheck = new Date();
			for(WorldServer s : server.worldServers)
			{
				boolean playerInWorld = s.getWorldInfo().equals(player.worldObj.getWorldInfo());
				int worldItemCount = 0;
				int itemsDeleted = 0;
				
				if(!playerInWorld && (kill || killall))
					continue;
				
				for(int id=0; id < s.loadedEntityList.size(); ++id)
				{
					Object t = s.loadedEntityList.get(id);
					if(t instanceof EntityItem)
					{
						++worldItemCount;
						EntityItem e = (EntityItem)t;
						if(playerInWorld && (killall  || (kill && e.getDistanceToEntity(player) <= radius)))
						{
							e.setDead();
							++itemsDeleted;
							continue;
						}
						
						Chunk c = s.getChunkFromBlockCoords((int)Math.round(e.posX), (int)Math.round(e.posZ));
						
						if(items.get(c) == null)
						{
							items.put(c, 1);	
						}
						else
						{
							items.put(c, items.get(c) + 1);
						}
					}
				}
				
				total += worldItemCount;
				
				String prefix = (playerInWorld) ? "\u00a72" :  "" ;
				if(kill || killall)
					sender.sendChatToPlayer(prefix + itemsDeleted + " items removed from " + s.provider.worldObj.getWorldInfo().getWorldName()+" " + s.provider.getDimensionName());
				if(details)
					sender.sendChatToPlayer(prefix + worldItemCount + " items in " + s.provider.worldObj.getWorldInfo().getWorldName()+" " + s.provider.getDimensionName());
			}
		}
		
		if(details)
		{
			sender.sendChatToPlayer("Top 5 chunks by loose item count:");
			ChunkRef[] sortedList = getSortedChunkList(items);
			for (int i = 0; i < sortedList.length && i < 5; ++i)
			{
				ChunkRef cr = sortedList[i];
				Chunk c = cr.getChunk();
				String worldName = c.worldObj.getWorldInfo().getWorldName();
				String dimName = c.worldObj.provider.getDimensionName();
				sender.sendChatToPlayer(worldName + " " + dimName + "(" + (c.xPosition * 16) + ", " + (c.zPosition * 16) + ") " + cr.getValue() + " items");
			}
		}
		
		/*
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
		*/
		
		if (!(details || kill || killall)) sender.sendChatToPlayer(total + " loose items in all worlds");
	}
	
	public ChunkRef[] getSortedChunkList(HashMap<Chunk, Integer> chunks)
	{
		ArrayList<ChunkRef> list = new ArrayList<ChunkRef>(); 
		for(Chunk c : chunks.keySet())
		{
			list.add(new ChunkRef(c, chunks.get(c)));
		}
		
		ChunkRef[] ret = list.toArray(new ChunkRef[] {});
		Arrays.sort(ret, new ChunkRefComparator());
		return ret;
	}
	
	public boolean canCommandSenderUseCommand(ICommandSender sender)
	{
		return hasEnhancedPermissions(sender);
	}
}
