package forgetools.commands;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatMessageComponent;
import cpw.mods.fml.common.FMLCommonHandler;

public class DimIDCommand extends ForgeToolsGenericCommand
{
	public DimIDCommand(String cmds)
	{
		super(cmds);
	}
	
	public void processCommand(ICommandSender sender, String[] args)
	{
		if(sender.getCommandSenderName().equals("Server"))
		{
			sender.sendChatToPlayer(ChatMessageComponent.createFromText("The console cannot be in a dimension!"));
			return;
		}
		
		if(!FMLCommonHandler.instance().getEffectiveSide().isServer()) return;
		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
		
		sender.sendChatToPlayer(ChatMessageComponent.createFromText("You are currently in " + player.worldObj.getWorldInfo().getWorldName() + " " +
								player.worldObj.provider.getDimensionName() + " (dim id " + player.worldObj.provider.dimensionId + ")"));
	}
	
	public int getRequiredPermissionLevel()
	{
		return 0;
	}
	
	public boolean canCommandSenderUseCommand(ICommandSender sender)
	{
		return true;
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		// TODO Auto-generated method stub
		return "/dimid";
	}

}
