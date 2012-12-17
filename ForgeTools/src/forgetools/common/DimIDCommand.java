package forgetools.common;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.src.CommandBase;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ICommandSender;

public class DimIDCommand extends CommandBase
{

	@Override
	public String getCommandName()
	{
		return "dimid";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args)
	{
		if(!FMLCommonHandler.instance().getEffectiveSide().isServer()) return;
		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
		
		sender.sendChatToPlayer("You are currently in " + player.worldObj.getWorldInfo().getWorldName() + " " +
								player.worldObj.provider.getDimensionName() + " (dim id " + player.worldObj.provider.dimensionId + ")");
	}

}
