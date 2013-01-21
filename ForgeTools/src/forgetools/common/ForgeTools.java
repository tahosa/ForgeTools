package forgetools.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.minecraft.command.ServerCommandManager;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Mod.ServerStarting;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;


@Mod(modid="ForgeTools_0_4a", name="Forge Tools", version="0.4a")
@NetworkMod(clientSideRequired=false, serverSideRequired=false)

public class ForgeTools
{
	
	public static MinecraftServer server;
	public static ArrayList<String> advancedUsers;
	public static Configuration config;
	
	@Init
	public void Init(FMLInitializationEvent event)
	{
	}
	
	@PreInit
	public void PreInit(FMLPreInitializationEvent event)
	{
		config = new Configuration(event.getSuggestedConfigurationFile());
		reloadConfig();
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
		manager.registerCommand(new ForgeToolsCommand());
		System.out.println("ForgeTools: Registered 8 commands");
		System.out.println("ForgeTools: Registered " + advancedUsers.size() + " advanced users");
	}
	
	public static void reloadConfig()
	{
		config.load();
		
		String toParse = config.get(config.CATEGORY_GENERAL, "advancedUsers", "").value;
		advancedUsers = new ArrayList<String>();
		advancedUsers.addAll(Arrays.asList(toParse.split(",\\s*")));
		
		config.save();
	}
	
	public static void saveConfigUpdates()
	{
		String users = "";
		for(String s : advancedUsers)
		{
			users += s +", ";
		}
		users = users.substring(0, users.length()-2);
		config.get(config.CATEGORY_GENERAL, "advancedUsers", "").value = users;
		config.save();
	}
}
