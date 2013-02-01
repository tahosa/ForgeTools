package forgetools.common;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.src.ModLoader;

public class InventoryCommand extends CommandBase {

	public String getCommandName() {

		return "inventory";
	}
	
	public String getCommandUsage(ICommandSender par1ICommandSender)
    {
    	return "/inventory [username] [dropall/list/find] [item]";
    }

	public void processCommand(ICommandSender sender, String[] args) {
		if(!FMLCommonHandler.instance().getEffectiveSide().isServer()) return;
		
		ServerConfigurationManager serverConfig = ModLoader.getMinecraftServerInstance().getConfigurationManager();
		MinecraftServer server = ForgeTools.server;
		
		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
				
		boolean drop = false, list = false, find = false;
		
		if (args.length == 2 && args[1].equals("dropall"))
			drop = true;
		else if (args.length == 2 && args[1].equals("list"))
			list = true;
		else if (args.length == 3 && args[1].equals("find"))
			find = true;
		else 
			throw new WrongUsageException(getCommandUsage(sender));			
		
		String players[] = serverConfig.getAllUsernames();	// Get an array of all usernames
		boolean found = false;
		for (String s: players) {							// Search for the targeted username
			if (s.equals(args[0]))
				 found = true;
		}
		
		if (found) {
			EntityPlayerMP target = serverConfig.getPlayerForUsername(args[0]);
			if (drop) {
				// No better way to do this
				int temp;
		        for (temp = 0; temp < target.inventory.mainInventory.length; ++temp)
		        {
		            if (target.inventory.mainInventory[temp] != null)
		            {
		            	target.inventory.player.dropPlayerItemWithRandomChoice(target.inventory.mainInventory[temp], false);
		            	target.inventory.mainInventory[temp] = null;
		            }
		        }

		        for (temp = 0; temp < target.inventory.armorInventory.length; ++temp)
		        {
		            if (target.inventory.armorInventory[temp] != null)
		            {
		            	target.inventory.player.dropPlayerItemWithRandomChoice(target.inventory.armorInventory[temp], false);
		            	target.inventory.armorInventory[temp] = null;
		            }
		        }
				
				sender.sendChatToPlayer("\u00a77Dropping " + args[0] + "'s items");
			} else if (list) {
				int temp = 0;
				for (temp = 0; temp < target.inventory.getSizeInventory(); temp++) {
					ItemStack tempItem = target.inventory.getStackInSlot(temp);
						if (tempItem != null) {
							String tempString = Item.itemsList[tempItem.getItem().itemID].getLocalItemName(tempItem);
							sender.sendChatToPlayer(args[0] + " has " + tempItem.stackSize + " of " + parseName(tempString));
						}
				}
			} else if (find) {
				int temp = 0;
				boolean itemFound = false;
				for (temp = 0; temp < target.inventory.getSizeInventory(); temp++) {
					String searchTerm = args[2].toLowerCase();
					ItemStack tempItem = target.inventory.getStackInSlot(temp);
					if (tempItem != null) {
						String tempString = Item.itemsList[tempItem.getItem().itemID].getLocalItemName(tempItem);
						if (tempString.toLowerCase().contains(searchTerm)) {
							sender.sendChatToPlayer("\u00a7c" + args[0] + " has " + tempItem.stackSize + " of " + parseName(tempString));
							itemFound = true;
						}
					}
				}
				
				if (!itemFound) {
					sender.sendChatToPlayer("\u00a77" + args[0] + " does not have any items with " + args[2] + " in the name");
				}
			}
		} else
			sender.sendChatToPlayer("\u00a7c" + args[0] + " cannot be found");
	}
	
	String parseName (String s) {
		String tokens[] = s.split("\\.");
		
		return tokens[1];
	}
	
	public boolean canCommandSenderUseCommand(ICommandSender sender)
	{
		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
		if (!player.username.equalsIgnoreCase("Server") && !ModLoader.getMinecraftServerInstance().getConfigurationManager().getOps().contains(player.username.trim().toLowerCase()))
			return false;
		return true;
	}
}
