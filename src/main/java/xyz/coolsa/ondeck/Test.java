package xyz.coolsa.ondeck;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.AudioStream;
import xyz.coolsa.ondeck.audio.DfpwmAudioStream;

@Environment(EnvType.CLIENT)
public class Test {
	private Map<Integer, AudioStream> playingStreams;
	public Test() {
			playingStreams = new HashMap<Integer, AudioStream>();
	}
	
	public DfpwmAudioStream getDfpwmStream(int streamNo) {
		AudioStream stream = playingStreams.get(streamNo);
		if(stream == null) {
			try {
				stream = new DfpwmAudioStream(new FileInputStream("/home/cloud/Code/Minecraft/computercraft/DFPWM/wbtest.dfpwm"));
				playingStreams.put(streamNo, stream);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return (DfpwmAudioStream) stream;
	}
}
