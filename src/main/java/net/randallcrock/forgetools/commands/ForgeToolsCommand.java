package net.randallcrock.forgetools.commands;

import java.util.Arrays;
import java.util.List;

import net.randallcrock.forgetools.ForgeTools;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

public class ForgeToolsCommand extends ForgeToolsGenericCommand {
	
	public ForgeToolsCommand(String cmds)
	{
		super(cmds);
	}
	
	public String getCommandUsage(ICommandSender sender)
	{
		return "/forgetools [adduser <Player> | removeuser <Player> | list | reload]";
	}

	public void processCommand(ICommandSender sender, String[] args) {
		ForgeTools.reloadConfig();
		if (args.length > 2) throw new WrongUsageException(getCommandUsage(sender));
		else if (args.length == 2)
		{
			if(args[0].equalsIgnoreCase("adduser") || args[0].equalsIgnoreCase("au"))
			{
				ForgeTools.advancedUsers.add(args[1].toLowerCase());
				sender.addChatMessage(new ChatComponentText("Added \""+args[1]+"\" to the ForgeTools user list"));
				
			}
			else if (args[0].equalsIgnoreCase("removeuser") || args[0].equalsIgnoreCase("ru"))
			{
				if(!ForgeTools.advancedUsers.remove(args[1].toLowerCase()))
					sender.addChatMessage(new ChatComponentText("No user exists with the name \""+args[1]+"\" in the ForgeTools user list"));
				else
					sender.addChatMessage(new ChatComponentText("Removed \""+args[1]+"\" from the ForgeTools user list"));
			}
			else throw new WrongUsageException(getCommandUsage(sender));
		}
		else if (args.length == 1)
		{
			if (args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("l"))
			{
				String userList = "";
				for (String s : ForgeTools.advancedUsers)
					userList += s + ", ";
				userList = userList.substring(0, userList.length()-2);
				if(userList.length() < 1)
					sender.addChatMessage(new ChatComponentText("ForgeTools user list is empty."));
				else
					sender.addChatMessage(new ChatComponentText("ForgeTools user list: " + userList));
			}
			else if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("r"))
				sender.addChatMessage(new ChatComponentText("ForgeTools user list reloaded"));
			else throw new WrongUsageException(getCommandUsage(sender));
		}
		else throw new WrongUsageException(getCommandUsage(sender));
		
		ForgeTools.saveConfigUpdates();
	}

	public boolean canCommandSenderUseCommand(ICommandSender sender)
	{
		return hasOpPermissions(sender);
	}

}
