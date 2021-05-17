package xyz.coolsa.ondeck;

import java.io.Console;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.zip.GZIPInputStream;

import javax.sound.sampled.AudioFormat;

import net.minecraft.client.sound.AudioStream;
import net.minecraft.client.sound.OggAudioStream;

public class AudioStreamTest implements AudioStream {
	int asdf = 0;
	private static final AudioFormat DFPWM_DECODED_FORMAT = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 32768, 8,
			1, 1, 32768, false);
	GZIPInputStream input;
	FileInputStream input2;
	FileInputStream input3;
	AudioStream test;
	int offset = 0;
	DFPWM dfpwm;
	private ByteBuffer buffer;

	public AudioStreamTest() {
		try {
//			System.out.println(System.getProperty("user.dir"));
			input = new GZIPInputStream(new FileInputStream(
					"/home/cloud/Code/Java/OnDeck/src/main/resources/assets/Another Day on Altair 5.dfpwm.gz"));
			input2 = new FileInputStream(
					"/home/cloud/Code/Java/OnDeck/src/main/resources/assets/Adventure_Time-NY_Bacon_Pancakes.dfpwm");
			input3 = new FileInputStream(
					"/home/cloud/Code/Java/OnDeck/src/main/resources/assets/cc-by-nc-sa kfaraday i am the princess of puddingland 232081548.ogg");
			test = new OggAudioStream(input3);
			System.out.println(
					"/home/cloud/Code/Java/OnDeck/src/main/resources/assets/Adventure_Time-NY_Bacon_Pancakes.dfpwm");
			dfpwm = new DFPWM();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		input.close();
		input2.close();
	}

	@Override
	public ByteBuffer getBuffer(int size) throws IOException {
		// TODO: Read from the DFPWM input file, then return it as a buffer stream.
//		System.out.println("Attempting to read from dfpwm");
		return test.getBuffer(size);
//		if (asdf < 10) {
////			size = 1024;
//			byte[] buf = new byte[size / 8];
//			int len = input2.read(buf);
//			input2.skip(1024);
////			System.out.println(buf);
//			if (len > 0) {
//				offset += size;
////			System.out.println(len);
//				byte[] buf2 = new byte[len * 8];
//				dfpwm.decompress(buf2, buf, 0, 0, len);
//				ByteBuffer test = ByteBuffer.allocate(len * 8);
////				System.out.println(test);
//				test.put(buf2);
//				test.flip();
//				asdf++;
//				return test;
//			}
//			return null;
//		}
//		return null;
	}

	@Override
	public AudioFormat getFormat() {
		return test.getFormat();
//		return DFPWM_DECODED_FORMAT;
	}

}
