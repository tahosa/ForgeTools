package net.randallcrock.forgetools.commands;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.ChatComponentText;
import cpw.mods.fml.common.FMLCommonHandler;
import net.randallcrock.forgetools.ForgeTools;

public class InventoryCommand extends ForgeToolsGenericCommand
{

	public InventoryCommand(String cmds)
	{
		super(cmds);
	}
	
	public String getCommandUsage(ICommandSender par1ICommandSender)
    {
    	return "/" + cmdName + " <username> <dropall|list|find> [item]";
    }

	public void processCommand(ICommandSender sender, String[] args)
	{
		if(!FMLCommonHandler.instance().getEffectiveSide().isServer()) return;
		
		ServerConfigurationManager serverConfig = ForgeTools.server.getConfigurationManager();
		MinecraftServer server = ForgeTools.server;
				
		boolean drop = false, list = false, find = false;
		
		if (args.length == 2 && args[1].equalsIgnoreCase("dropall"))
			drop = true;
		else if (args.length == 2 && args[1].equalsIgnoreCase("list"))
			list = true;
		else if (args.length == 3 && args[1].equalsIgnoreCase("find"))
			find = true;
		else 
			throw new WrongUsageException(getCommandUsage(sender));			
		
		String players[] = serverConfig.getAllUsernames();	// Get an array of all usernames
		boolean found = false;
		for (String s : players) 
		{
			// Search for the targeted username
			if (s.toLowerCase().equals(args[0].toLowerCase()))
				 found = true;
		}
		
		if (found) 
		{
			EntityPlayerMP target = serverConfig.func_152612_a(args[0]);
			if (drop) 
			{
				// No better way to do this
		        for (int temp = 0; temp < target.inventory.mainInventory.length; ++temp)
		        {
		            if (target.inventory.mainInventory[temp] != null)
		            {
		            	target.inventory.player.dropPlayerItemWithRandomChoice(target.inventory.mainInventory[temp], false);
		            	target.inventory.mainInventory[temp] = null;
		            }
		        }

		        for (int temp = 0; temp < target.inventory.armorInventory.length; ++temp)
		        {
		            if (target.inventory.armorInventory[temp] != null)
		            {
		            	target.inventory.player.dropPlayerItemWithRandomChoice(target.inventory.armorInventory[temp], false);
		            	target.inventory.armorInventory[temp] = null;
		            }
		        }
				
				sender.addChatMessage(new ChatComponentText("\u00a77Dropping " + args[0] + "'s items"));
			} 
			else if (list) 
			{
				boolean empty = true;
				for (int temp = 0; temp < target.inventory.getSizeInventory(); temp++)
				{
					ItemStack tempItem = target.inventory.getStackInSlot(temp);
					
					if (tempItem != null)
					{
						empty = false;
						String tempString = tempItem.getDisplayName();
						sender.addChatMessage(new ChatComponentText(tempItem.stackSize + " of " + parseName(tempString)));
					}		
				}
				if(empty)
					sender.addChatMessage(new ChatComponentText(args[0] + "\'s inventory is empty."));
			} 
			else if (find) 
			{
				int temp = 0;
				boolean itemFound = false;
				for (temp = 0; temp < target.inventory.getSizeInventory(); temp++)
				{
					String searchTerm = parseSearchString(args[2].toLowerCase());
					ItemStack tempItem = target.inventory.getStackInSlot(temp);
					if (tempItem != null)
					{
						String tempString = tempItem.getDisplayName();
						if (searchString(searchTerm, tempString.toLowerCase()))
						{
							sender.addChatMessage(new ChatComponentText("\u00a7c" + args[0] + " has " + tempItem.stackSize + " of " + parseName(tempString)));
							itemFound = true;
						}
					}
				}
				
				if (!itemFound) 
				{
					sender.addChatMessage(new ChatComponentText("\u00a77" + args[0] + " does not have any items with " + args[2] + " in the name"));
				}
			}
		} 
		else
			sender.addChatMessage(new ChatComponentText("\u00a7c" + args[0] + " cannot be found"));
	}
	
	String parseName (String s)
	{
		String tokens[] = s.split("\\.");
		
		return tokens[tokens.length - 1];
	}
	
	String parseSearchString(String s)
	{
		if (ForgeTools.regexMatch)
		{
			//s.replaceAll("\\", "\\\\");
		}
		else
		{
			s.replaceAll("\\*", ".*");
			s.replaceAll("\\?", ".");
			//s.replaceAll("\\", "\\\\");
		}
		return s;
	}
	
	boolean searchString(String searchTerm, String target)
	{
		return target.matches(searchTerm);
	}
	
	public boolean canCommandSenderUseCommand(ICommandSender sender)
	{
		return hasOpPermissions(sender);
	}
}
