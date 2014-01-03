package forgetools.commands;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.ImmutableSetMultimap;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.ModLoader;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import cpw.mods.fml.common.FMLCommonHandler;
import forgetools.ForgeTools;

public class LoadedChunksCommand extends ForgeToolsGenericCommand
{

	public LoadedChunksCommand(String cmds)
	{
		super(cmds);
	}

	public String getCommandUsage(ICommandSender par1ICommandSender)
    {
    	return  "/" + cmdName + " [detail | d]";
    }
	
	public void processCommand(ICommandSender sender, String[] args)
	{
		if(!FMLCommonHandler.instance().getEffectiveSide().isServer()) return;
		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
		
		MinecraftServer server = ForgeTools.server;
		boolean details = false;
		
		if(args.length > 1) throw new WrongUsageException(getCommandUsage(sender));
		else if (args.length == 1)
			if (args[0].equalsIgnoreCase("detail") || args[0].equalsIgnoreCase("d")) details = true;
		else throw new WrongUsageException(getCommandUsage(sender));
		
		int total = 0, totalTix = 0;
		for(WorldServer s : server.worldServers)
		{
			World tmp = ((World) s);
			ImmutableSetMultimap<ChunkCoordIntPair, Ticket> forcedChunks = tmp.getPersistentChunks();
			int amt = 0, tickets = 0;
			for(ChunkCoordIntPair c : forcedChunks.keys())
			{
				for(Ticket t : forcedChunks.get(c))
				{
					amt += t.getChunkList().size();
					tickets++;
				}
			}
			String prefix = (s.getWorldInfo().equals(player.worldObj.getWorldInfo())) ? "\u00a72" :  "" ;
			if (details) sender.sendChatToPlayer(ChatMessageComponent.createFromText(prefix + amt + " force loaded chunks (" + tickets + " tickets) in "+s.provider.worldObj.getWorldInfo().getWorldName()+" " + s.provider.getDimensionName()));
			total += amt;
			totalTix += tickets;
		}
		
		if(!details) sender.sendChatToPlayer(ChatMessageComponent.createFromText(total + " force loaded chunks in all worlds"));
		
	}
	
	public boolean canCommandSenderUseCommand(ICommandSender sender)
	{
		return hasEnhancedPermissions(sender);
	}

}
