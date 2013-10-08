package forgetools.common;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.src.ModLoader;

/**
 * Base class for all ForgeTools commands. Has the framework for dynamic naming
 * @author rlcrock
 *
 */
public abstract class ForgeToolsGenericCommand extends CommandBase {
	
	protected String cmdName;		// Primary command name
	protected String[] cmdAliases;	// List of aliases for the command
	
	public ForgeToolsGenericCommand(String cmds)
	{
		super();
		
		// Parse the cmds list. Comma separated string value
		String[] tmp = cmds.split("\\s*,\\s*");
		
		cmdName = tmp[0];
		if(tmp.length > 1)
			cmdAliases = Arrays.copyOfRange(tmp, 1,  tmp.length-1);
		else
			cmdAliases = new String[] {};
	}

	public String getCommandName()
	{
		return cmdName;
	}
	
	public List getCommandAliases()
	{
		return Arrays.asList(cmdAliases);
	}
	
	/**
	 * Checks to see if the given player has OP level permissions. Used in commands which should only be allowed to OPs.
	 * @param sender Object sending the request to run the command
	 * @return True if they are the server or OP, false othewise.
	 */
	protected boolean hasOpPermissions(ICommandSender sender)
	{
		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
		if (!player.username.equalsIgnoreCase("Server") && !ModLoader.getMinecraftServerInstance().getConfigurationManager().getOps().contains(player.username.trim().toLowerCase()))
			return false;
		return true;
	}
	
	/**
	 * Checks to see if the given player has advanced permissions (non-op). Used in commands which can be used by non-op players who need more rights
	 * @param sender Object sending the request to run the command
	 * @return True if they are the server, OP, or on the list of advanced users in the config file. False otherwise.
	 */
	protected boolean hasEnhancedPermissions(ICommandSender sender) {
		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
		if (player.username.equalsIgnoreCase("Server") 
				|| ModLoader.getMinecraftServerInstance().getConfigurationManager().getOps().contains(player.username.trim().toLowerCase()) 
				|| ForgeTools.advancedUsers.contains(player.username.trim().toLowerCase()))
			return true;
		return false;
	}
}
