package forgetools.common;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.src.ModLoader;
import cpw.mods.fml.common.FMLCommonHandler;

public class HealCommand extends ForgeToolsGenericCommand
{

	public HealCommand(String cmds)
	{
		super(cmds);
	}
	
	public String getCommandUsage(ICommandSender par1ICommandSender)
    {
    	return "/" + cmdName + " [username] [hp | hunger] [amount]";
    }

	public void processCommand(ICommandSender sender, String[] args)
	{
		if(!FMLCommonHandler.instance().getEffectiveSide().isServer()) return;
		
		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
		ServerConfigurationManager serverConfig = ModLoader.getMinecraftServerInstance().getConfigurationManager();
		MinecraftServer server = ForgeTools.server;
		
		boolean full = false, hp = false, food = false;
		int amt = 20;
		
		// Only accept the command with one or three arguments
		if (args.length == 1)
			full = true;
		else if (args.length == 2)
		{ 
			if (args[1].equalsIgnoreCase("hp")) 
			{
				hp = true;
				full = true;
			}
			else if (args[1].equalsIgnoreCase("hunger")) 
			{
				food = true;
				full = true;
			}
			else
				throw new WrongUsageException(getCommandUsage(sender));
		}
		else if (args.length == 3)
		{
			if (args[1].equalsIgnoreCase("hp"))
				hp = true;
			else if (args[1].equalsIgnoreCase("hunger"))
				food = true;
			else
				throw new WrongUsageException(getCommandUsage(sender));
			
			double temp = 0;
			try
			{
				temp = Double.parseDouble(args[2]);
				amt = Math.min((int) (temp * 2), 20);
			}
			catch (NumberFormatException e)
			{
				sender.sendChatToPlayer("\u00a7cPlease enter a valid number for the heal amount.");
				return;
			}
			
			if (amt < 0)
			{
				sender.sendChatToPlayer("\u00a7cYou cannot heal for a negative amount.");
				return;
			}
		}
		else 
			throw new WrongUsageException(getCommandUsage(sender));
		
		String players[] = serverConfig.getAllUsernames();	// Get an array of all usernames
		boolean found = false;
		for (String s : players)
		{							// Search for the targeted username
			if (s.toLowerCase().equals(args[0].toLowerCase()))
				 found = true;
		}
		
		if (found)
		{
			EntityPlayerMP target = serverConfig.getPlayerForUsername(args[0]);
			
			if (full && !hp && !food)
			{
				sender.sendChatToPlayer("\u00a7aHealing " + args[0] + "'s HP and hunger to full.");
				target.heal(20);
				target.getFoodStats().addStats(20, 20);
				target.sendChatToPlayer("\u00a7a" + player.username + " has healed your HP and hunger to full.");
			} 
			else
			{
				sender.sendChatToPlayer("\u00a7aHealing " + args[0] + "'s " + ((hp == true) ? "hp" : "hunger") + " by " + (double)(amt) / 2 + ".");
				if (hp)
				{
					target.heal(amt);
					
					target.sendChatToPlayer("\u00a7a" + player.username + " has healed your HP by " + (double)(amt) / 2 + " hearts.");
				}
				else
				{
					target.getFoodStats().addStats(amt, 20);
					
					target.sendChatToPlayer("\u00a7a" + player.username + " has healed your hunger by " + (double)(amt) / 2 + " drumsticks.");
				}
			}
		}
		else
			sender.sendChatToPlayer("\u00a7c" + args[0] + " cannot be found.");
	}

	public boolean canCommandSenderUseCommand(ICommandSender sender)
	{
		return hasOpPermissions(sender);
	}
	

}