package forgetools.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import net.minecraft.command.ServerCommandManager;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Mod.ServerStarting;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;


@Mod(modid="ForgeTools_0_5", name="Forge Tools", version="0.5")
@NetworkMod(clientSideRequired=false, serverSideRequired=false)

public class ForgeTools
{
	private static final String dropsDName = "drops";
	private static final String dimIDDName = "dimid";
	private static final String forgeToolsDName = "forgeTools";
	private static final String healDName = "heal";
	private static final String inventoryDName = "inventory";
	private static final String lagDName = "lag";
	private static final String loadedChunksDName = "loadedChunks";
	private static final String mobsDName = "mobs";
	private static final String smiteDName = "smite";
	
	public static MinecraftServer server;
	public static ArrayList<String> advancedUsers;
	public static HashMap<String, String> commandsToLoad;
	public static boolean regexMatch;
	public static Configuration config;
	public static float killRadius;
	public static int timeout;
	
	@Init
	public void Init(FMLInitializationEvent event)
	{
	}
	
	@PreInit
	public void PreInit(FMLPreInitializationEvent event)
	{
		commandsToLoad = new HashMap<String, String>();
		config = new Configuration(event.getSuggestedConfigurationFile());
		reloadConfig();
	}
	
	@ServerStarting
	public void serverStarting(FMLServerStartingEvent event)
	{
		server = FMLCommonHandler.instance().getMinecraftServerInstance();
		ServerCommandManager manager = (ServerCommandManager)server.getCommandManager();
		
		if(commandsToLoad.containsKey(dropsDName))
			manager.registerCommand(new DropsCommand(commandsToLoad.get(dropsDName)));
		if(commandsToLoad.containsKey(dimIDDName))
			manager.registerCommand(new DimIDCommand(commandsToLoad.get(dimIDDName)));
		if(commandsToLoad.containsKey(forgeToolsDName))
			manager.registerCommand(new ForgeToolsCommand(commandsToLoad.get(forgeToolsDName)));
		if(commandsToLoad.containsKey(healDName))
			manager.registerCommand(new HealCommand(commandsToLoad.get(healDName)));
		if(commandsToLoad.containsKey(inventoryDName))
			manager.registerCommand(new InventoryCommand(commandsToLoad.get(inventoryDName)));
		if(commandsToLoad.containsKey(lagDName))
			manager.registerCommand(new LagCommand(commandsToLoad.get(lagDName)));
		if(commandsToLoad.containsKey(loadedChunksDName))
			manager.registerCommand(new LoadedChunksCommand(commandsToLoad.get(loadedChunksDName)));
		if(commandsToLoad.containsKey(mobsDName))
			manager.registerCommand(new MobsCommand(commandsToLoad.get(mobsDName)));
		if(commandsToLoad.containsKey(smiteDName))
			manager.registerCommand(new SmiteCommand(commandsToLoad.get(smiteDName)));
		
		System.out.println("ForgeTools: Registered " + commandsToLoad.keySet().size() + " commands");
		System.out.println("ForgeTools: Registered " + advancedUsers.size() + " advanced users");
	}
	
	public static void reloadConfig()
	{
		config.load();
		
		String advUsersRaw = config.get(config.CATEGORY_GENERAL, "advancedUsers", "").getString();
		advancedUsers = new ArrayList<String>();
		advancedUsers.addAll(Arrays.asList(advUsersRaw.split(",\\s*")));
		
		regexMatch = config.get(config.CATEGORY_GENERAL, "regexSearch", false).getBoolean(false);
		
		ArrayList<Property> cmdData = new ArrayList<Property>();
			
		killRadius = (float)config.get(config.CATEGORY_GENERAL,  "killRadius", 32d).getDouble(32d);
		timeout = config.get(config.CATEGORY_GENERAL, "timeout", 10).getInt(10);
		
		cmdData.add(config.get("commands", dimIDDName, "dimid", "Commands should be a comma separated list of values.\n" +
				 																 "The first in the list will be used as the default, while the rest of the list will be aliases for that command.\n" +
				 																 "Leave the value blank if you wish to disable the command."));
		cmdData.add(config.get("commands", dropsDName, "drops"));
		cmdData.add(config.get("commands", forgeToolsDName, "forgetools,ft"));
		cmdData.add(config.get("commands", healDName, "heal"));
		cmdData.add(config.get("commands", inventoryDName, "inventory,inv"));
		cmdData.add(config.get("commands", lagDName, "lag,tps"));
		cmdData.add(config.get("commands", loadedChunksDName, "loadedchunks,lc"));
		cmdData.add(config.get("commands", mobsDName, "mobs"));
		cmdData.add(config.get("commands", smiteDName, "smite"));
		
		for(Property cmd : cmdData)
		{
			if(cmd.wasRead() && !cmd.getString().isEmpty())
				commandsToLoad.put(cmd.getName(), cmd.getString());
		}
		
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
		config.get(config.CATEGORY_GENERAL, "advancedUsers",  "").set(users);
		config.save();
	}
}
