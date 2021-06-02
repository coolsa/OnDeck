package xyz.coolsa.ondeck;

import net.fabricmc.api.ClientModInitializer;
import xyz.coolsa.ondeck.audio.ClientAudioPacketHandler;

public class OnDeckClient implements ClientModInitializer {
	private ClientAudioPacketHandler clientAudioPacketHandler;
	/**
	 * Startup and defer network events.
	 */
	@Override
	public void onInitializeClient() {
		//init the client audio packet stuffs.
		this.clientAudioPacketHandler = new ClientAudioPacketHandler();
	}


}
