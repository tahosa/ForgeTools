package forgetools.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatMessageComponent;
import cpw.mods.fml.common.FMLCommonHandler;
import forgetools.ForgeTools;

public class LagCommand extends ForgeToolsGenericCommand
{	
	public LagCommand(String cmds)
	{
		super(cmds);
	}
	
	public String getCommandUsage(ICommandSender par1ICommandSender)
	{
		return "/" + cmdName + " [detail | d | current | c | <dim_id>]";
	}

	public void processCommand(ICommandSender sender, String[] args)
	{
		if(!FMLCommonHandler.instance().getEffectiveSide().isServer()) return;
		
		boolean details = false, current = false;

		EntityPlayerMP player = null;
		if(!sender.getCommandSenderName().equals("Server"))
			player = getCommandSenderAsPlayer(sender);
		
		Integer dim = null;
		
		if (args.length > 1) throw new WrongUsageException(getCommandUsage(sender));
		else if (args.length == 1)
		{
			if(args[0].equalsIgnoreCase("detail") || args[0].equalsIgnoreCase("d"))
				details = true;
			else if (args[0].equalsIgnoreCase("current") || args[0].equalsIgnoreCase("c"))
				current = true;
			else 
			{
				try
				{
					dim = Integer.parseInt(args[0]);
				}
				catch(Exception e)
				{
					sender.sendChatToPlayer(ChatMessageComponent.createFromText("Invalid dimension ID."));
					return;
				}
			}
		}
		
		MinecraftServer server = ForgeTools.server;
		
		if(current || dim != null)
		{
			if(current && sender.getCommandSenderName().equals("Server"))
			{
				sender.sendChatToPlayer(ChatMessageComponent.createFromText("Invalid option. As the console you are not in a dimension!"));
				return;
			}
			
			int dimension = (dim != null) ? dim : player.dimension;
			String dimName;
			try
			{
				dimName = server.worldServerForDimension(dimension).provider.getDimensionName();
			}
			catch (Exception ex)
			{
				sender.sendChatToPlayer(ChatMessageComponent.createFromText("\u00a74" + dim + " is not a valid dimension id"));
				return;
			}
			long[] tickTimes = server.worldTickTimes.get(dimension);
			double tickMS = Math.round(avgTick(tickTimes)*1.0E-5D)/10d;
			double tickPct = (tickMS < 50) ? 100d : Math.round(50d/tickMS * 1000)/10d;
			double tps = (tickMS < 50) ? 20d : Math.round((1000d/tickMS) * 10d) / 10d;
			sender.sendChatToPlayer(ChatMessageComponent.createFromText(textColor(tps) + dimName + " tick: "+tps+" tps ("+tickMS + "ms, "+ tickPct+"%)"));
		}
		
		else if (details)
		{
			Hashtable<Integer,long[]> worldTickTimes = server.worldTickTimes;
			for(Integer i : worldTickTimes.keySet())
			{
				String dimName = i.toString();
				try 
				{
					dimName = server.worldServerForDimension(i).provider.getDimensionName();
				} 
				catch (Exception ex)
				{
					System.out.println(ex.getMessage());
				}
				
				double tickMS = Math.round(avgTick(worldTickTimes.get(i))*1.0E-5D)/10d;
				double tickPct = (tickMS < 50) ? 100d : Math.round(50d/tickMS * 1000)/10d;
				double tps = (tickMS < 50) ? 20d : Math.round((1000d/tickMS) * 10d) / 10d;
				sender.sendChatToPlayer(ChatMessageComponent.createFromText(textColor(tps) + dimName + " tick: "+tps+" tps ("+tickMS + "ms, "+ tickPct+"%)"));
			}
		}
		else
		{
			double tickMS = Math.round(avgTick(server.tickTimeArray)*1.0E-5D)/10d;
			double tickPct = (tickMS < 50) ? 100d : Math.round(50d/tickMS * 1000)/10d;
			double tps = (tickMS < 50) ? 20d : Math.round((1000d/tickMS) * 10d) / 10d;
			sender.sendChatToPlayer(ChatMessageComponent.createFromText(textColor(tps) + "Tick: "+tps+" tps ("+tickMS + "ms, "+ tickPct+"%)"));
		}
		
	}
	
	public boolean canCommandSenderUseCommand(ICommandSender sender)
	{
		return hasEnhancedPermissions(sender);
	}
	
	private double avgTick(long[] serverTickArray)
	{
		long sum = 0L;
		long[] svTicks = serverTickArray;
		int size = serverTickArray.length;
		
		for(int i = 0; i<size; i++)
			sum += svTicks[i];
		
		return (double)sum / (double) size;
	}
	
	private String textColor(double tps)
	{
		if (tps >= 15)
			return "\u00a72";
		else if(tps >= 10 && tps < 15)
			return "\u00a7e";
		else 
			return "\u00a74";
	}
}
