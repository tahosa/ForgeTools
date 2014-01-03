package forgetools.commands;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.src.ModLoader;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.world.WorldServer;
import cpw.mods.fml.common.FMLCommonHandler;
import forgetools.ForgeTools;

public class SmiteCommand extends ForgeToolsGenericCommand
{

	public SmiteCommand(String cmds)
	{
		super(cmds);
	}
	
	public String getCommandUsage(ICommandSender par1ICommandSender)
    {
    	return "/" + cmdName + " [username] [announce]";
    }

	public void processCommand(ICommandSender sender, String[] args)
	{
		if(!FMLCommonHandler.instance().getEffectiveSide().isServer()) return;
		
		MinecraftServer server = ForgeTools.server;
		ServerConfigurationManager serverConfig = server.getConfigurationManager(); 
				
		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
				
		boolean announce = false;
		
		// Only accept the command with one or two arguments
		if (args.length == 2)
		{
			if (args[1].equalsIgnoreCase("announce"))
				announce = true;
			else 
				throw new WrongUsageException(getCommandUsage(sender));
		}
		else if (args.length != 1)
			throw new WrongUsageException(getCommandUsage(sender));
		
		String players[] = serverConfig.getAllUsernames();	// Get an array of all usernames
		boolean found = false;
		for (String s: players)
		{							// Search for the targeted username
			if (s.toLowerCase().equals(args[0].toLowerCase()))
				 found = true;
		}
		
		if (found)
		{
			sender.sendChatToPlayer(ChatMessageComponent.createFromText("\u00a77Smiting " + args[0]));
			
			EntityPlayerMP target = serverConfig.getPlayerForUsername(args[0]);
			WorldServer targetWorld = null;
			
			for(WorldServer s : server.worldServers)
			{	
				// Find the world the player is in
				if (s.getWorldInfo().equals(target.worldObj.getWorldInfo()))
				{
					targetWorld = s;
					break;
				}
			}
			
			// Create the lightning bolt targeted at the players position and then spawn it in the world
			EntityLightningBolt bolt = new EntityLightningBolt(targetWorld, target.posX, target.posY, target.posZ);
			targetWorld.spawnEntityInWorld(bolt);
			
			target.setHealth(0);	// Set the target's health to 0
			
			// Announce the death
			if (announce)
			{
				for (String s: players)
				{
					EntityPlayerMP temp = serverConfig.getPlayerForUsername(s);
					temp.sendChatToPlayer(ChatMessageComponent.createFromText(target.username + " has been smited by " + player.username));
				}
			}
			else
			{
				for (String s: players)
				{
					EntityPlayerMP temp = serverConfig.getPlayerForUsername(s);
					temp.sendChatToPlayer(ChatMessageComponent.createFromText(target.username + " died"));
				}
			}
		}
		else
			sender.sendChatToPlayer(ChatMessageComponent.createFromText("\u00a7c" + args[0] + " cannot be found"));
	}
	
	public boolean canCommandSenderUseCommand(ICommandSender sender)
	{
		return hasOpPermissions(sender);
	}

}