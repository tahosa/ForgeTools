package forgetools.common;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.src.ModLoader;

public abstract class ForgeToolsGenericCommand extends CommandBase {
	
	protected String cmdName;
	protected String[] cmdAliases;
	
	public ForgeToolsGenericCommand(String cmds)
	{
		super();
		
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
	
	protected boolean hasOpPermissions(ICommandSender sender)
	{
		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
		if (!player.username.equalsIgnoreCase("Server") && !ModLoader.getMinecraftServerInstance().getConfigurationManager().getOps().contains(player.username.trim().toLowerCase()))
			return false;
		return true;
	}
	
	protected boolean hasEnhancedPermissions(ICommandSender sender) {
		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
		if (player.username.equalsIgnoreCase("Server") 
				|| ModLoader.getMinecraftServerInstance().getConfigurationManager().getOps().contains(player.username.trim().toLowerCase()) 
				|| ForgeTools.advancedUsers.contains(player.username.trim().toLowerCase()))
			return true;
		return false;
	}

}
