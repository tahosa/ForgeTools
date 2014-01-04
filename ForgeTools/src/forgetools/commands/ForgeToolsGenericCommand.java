package forgetools.commands;

import java.util.Arrays;
import java.util.List;

import forgetools.ForgeTools;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

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
			cmdAliases = Arrays.copyOfRange(tmp, 1,  tmp.length);
		else
			cmdAliases = new String[] {};
	}

	@Override
	public String getCommandName()
	{
		return cmdName;
	}
	
	@Override
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
		if (sender.getCommandSenderName().equals("Server") || (ForgeTools.server.getConfigurationManager().getOps().contains(sender.getCommandSenderName().trim().toLowerCase())))
			return true;
		return false;
	}
	
	/**
	 * Checks to see if the given player has advanced permissions (non-op). Used in commands which can be used by non-op players who need more rights
	 * @param sender Object sending the request to run the command
	 * @return True if they are the server, OP, or on the list of advanced users in the config file. False otherwise.
	 */
	protected boolean hasEnhancedPermissions(ICommandSender sender) {
		if(hasOpPermissions(sender)
				|| ForgeTools.advancedUsers.contains(sender.getCommandSenderName().trim().toLowerCase()))
			return true;
		return false;
	}
}
