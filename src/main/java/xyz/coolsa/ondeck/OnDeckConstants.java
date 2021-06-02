package xyz.coolsa.ondeck;

import javax.sound.sampled.AudioFormat;

import net.minecraft.util.Identifier;

public class OnDeckConstants {
	
	/**
	 * The AudioFormat of DFPWM is 1024*4*8 Hz, with 8 bit samples and is in mono, Unsigned little endian.
	 */
	public static final AudioFormat DFPWM_DECODED_FORMAT = new AudioFormat(32768, 8, 1, false, false);
	
	/**
	 * Continued data packets of the DFPWM audio for a particular soundID. Format is:
	 * Integer: soundID
	 * Byte[]: Data
	 */
	public static final Identifier DFPWM_DATA_PACKET = new Identifier("ondeck:dfpwm_data_cont");
	
	//still not sure if i should include data with the original sent packets...
	/**
	 * Begin a new DFPWM player, tracking an entity that is playing. Format is:
	 * Integer: soundID
	 * Float: volume
	 * Float: pitch
	 * Integer: entityID
	 */
	public static final Identifier DFPWM_ENTITY_PLAY = new Identifier("ondeck:dfpwm_entity_play");
	
	/**
	 * Begin a new DFPWM player, tracking a group of blocks that are playing. Format is:
	 * Integer: soundID
	 * Float: volume
	 * Float: pitch
	 * Long[]: blockPosArray: List of all BlockPos's for sound sources.
	 */
	public static final Identifier DFPWM_BLOCK_PLAY = new Identifier("ondeck:dfpwm_block_play");
	
	/**
	 * Updates a DFPWM player, adding/removing playing blocks. Format is:
	 * Integer: soundID
	 * Long[]: blockPosArray: Blocks to toggle the audio for, needs further loop to create BlockPos.fromLong() on all longs
	 */
	public static final Identifier DFPWM_BLOCK_UPDATE = new Identifier("ondeck:dfpwm_block_update");
	
	/**
	 * Stops a DFPWM player. Format is:
	 * Integer: soundID
	 */
	public static final Identifier DFPWM_STOP_PLAY = new Identifier("ondeck:dfpwm_stop_play");
	
	/**
	 * Requests a DFPWM stream that is currently playing. Format is:
	 * Integer: soundID
	 */
	public static final Identifier DFPWM_STREAM_CONT = new Identifier("ondeck:dfpwm_stream_cont");
	
	/**
	 * Requests playing of a DFPWM stream from the GUI.
	 */
	public static final Identifier DFPWM_STREAM_START = new Identifier("ondeck:dfpwm_stream_start");
	
	/**
	 * Requests stopping a DFPWM stream from the GUI.
	 */
	public static final Identifier DFPWM_STREAM_STOP = new Identifier("ondeck:dfpwm_stream_stop");
}
