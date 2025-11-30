package eu.tricht;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class CannonballStallThievingPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(CannonballStallThievingPlugin.class);
		RuneLite.main(args);
	}
}