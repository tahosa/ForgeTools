package forgetools.common;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.CommandBase;
import net.minecraft.src.EntityChicken;
import net.minecraft.src.EntityLightningBolt;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.ModLoader;
import net.minecraft.src.ServerConfigurationManager;
import net.minecraft.src.WorldServer;
import net.minecraft.src.WrongUsageException;

public class HealCommand extends CommandBase{

	@Override
	public String getCommandName() {
		return "heal";
	}
	
	@Override
	public String getCommandUsage(ICommandSender par1ICommandSender)
    {
    	return "/heal [username] [hp | hunger] [amount]";
    }

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if(!FMLCommonHandler.instance().getEffectiveSide().isServer()) return;
		
		ServerConfigurationManager serverConfig = ModLoader.getMinecraftServerInstance().getConfigurationManager();
		MinecraftServer server = ForgeTools.server;
		
		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
		if (!player.username.equalsIgnoreCase("Server") && !serverConfig.getOps().contains(player.username.trim().toLowerCase()))
		{
			sender.sendChatToPlayer("\u00a74You do not have permission to use the /heal command.");
			return;
		}
		
		boolean full = false, hp = false, food = false;
		int amt = 20;
		
		// Only accept the command with one or three arguments
		if (args.length == 1)
			full = true;
		else if (args.length == 2) { 
			if (args[1].equals("hp")) {
				hp = true;
				full = true;
			}
			else if (args[1].equals("hunger")) {
				food = true;
				full = true;
			}
			else
				throw new WrongUsageException(getCommandUsage(sender));
		}
		else if (args.length == 3) {
			if (args[1].equals("hp"))
				hp = true;
			else if (args[1].equals("hunger"))
				food = true;
			else
				throw new WrongUsageException(getCommandUsage(sender));
			
			double temp = 0;
			try {
				temp = Double.parseDouble(args[2]);
				amt = Math.min((int) (temp * 2), 20);
			} catch (NumberFormatException e) {
				sender.sendChatToPlayer("\u00a7cPlease enter a valid number for the heal amount.");
				return;
			}
			
			if (amt < 0) {
				sender.sendChatToPlayer("\u00a7cYou cannot heal for a negative amount.");
				return;
			}
		}
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
			
			if (full && !hp && !food) {
				sender.sendChatToPlayer("\u00a7aHealing " + args[0] + "'s HP and hunger to full.");
				target.heal(20);
				target.getFoodStats().addStats(20, 20);
				target.sendChatToPlayer("\u00a7a" + player.username + " has healed your HP and hunger to full.");
			} else {
				sender.sendChatToPlayer("\u00a7aHealing " + args[0] + "'s " + ((hp == true) ? "hp" : "hunger") + " by " + (double)(amt) / 2 + ".");
				if (hp) {
					target.heal(amt);
					
					target.sendChatToPlayer("\u00a7a" + player.username + " has healed your HP by " + (double)(amt) / 2 + " hearts.");
				} else {
					target.getFoodStats().addStats(amt, 20);
					
					target.sendChatToPlayer("\u00a7a" + player.username + " has healed your hunger by " + (double)(amt) / 2 + " drumsticks.");
				}
			}
		} else
			sender.sendChatToPlayer("\u00a7c" + args[0] + " cannot be found.");
	}

}