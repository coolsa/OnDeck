package xyz.coolsa.ondeck;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import xyz.coolsa.ondeck.audio.ServerAudioPacketHandler;

public class OnDeck implements ModInitializer {
	public static final Item FABRIC_ITEM = new ItemTest(new FabricItemSettings().group(ItemGroup.MISC));
	public static ServerAudioPacketHandler serverAudioPacketHandler;

	@Override
	public void onInitialize() {
		// In this class, we only need to init the create the interfaces for some mods,
		// such as the one for CC or TIS.
		// for temporary usage, we will also set up some information on the tapedrives,
		// but thats SUPER temporary.
		serverAudioPacketHandler = new ServerAudioPacketHandler();
		Registry.register(Registry.ITEM, new Identifier("ondeck", "test_item"), FABRIC_ITEM);
//		ServerPlayNetworking.registerReceiver(, channelName, channelHandler)
	}
}
