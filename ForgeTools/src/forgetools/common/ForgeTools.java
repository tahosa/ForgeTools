package forgetools.common;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.ServerCommandManager;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.ServerStarting;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;


@Mod(modid="ForgeTools_0_1", name="Forge Tools", version="0.1")
@NetworkMod(clientSideRequired=false, serverSideRequired=false)

public class ForgeTools
{
	
	public static MinecraftServer server;
	
	@Init
	public void Init(FMLInitializationEvent event)
	{
	}
	
	@ServerStarting
	public void serverStarting(FMLServerStartingEvent event)
	{
		server = FMLCommonHandler.instance().getMinecraftServerInstance();
		ServerCommandManager manager = (ServerCommandManager)server.getCommandManager();
		manager.registerCommand(new LagCommand());
		manager.registerCommand(new DropsCommand());
		manager.registerCommand(new LoadedChunksCommand());
		manager.registerCommand(new DimIDCommand());
		manager.registerCommand(new MobsCommand());
		manager.registerCommand(new SmiteCommand());
		manager.registerCommand(new HealCommand());
		System.out.println("ForgeTools: Registered 7 commands");
	}
}
