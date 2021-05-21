package xyz.coolsa.ondeck;

import java.io.Console;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.sound.sampled.AudioFormat;

import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryUtil;

import com.google.common.collect.Lists;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.AudioStream;
import net.minecraft.client.sound.OggAudioStream;

@Environment(EnvType.CLIENT)
public class AudioStreamTest implements AudioStream {
	int asdf = 0;
	private static final AudioFormat DFPWM_DECODED_FORMAT = new AudioFormat(32768, 8, 1, false, false);
	private AudioFormat dfpwm_format;
	GZIPInputStream input;
	FileInputStream input2;
	InputStream input3;
//	FileInputStream input3;
	AudioStream test;
	int offset = 0;
	DFPWM dfpwm;
	private ByteBuffer buffer;

	public AudioStreamTest() {
		try {
			input3 = new GZIPInputStream(new FileInputStream(
					"/home/cloud/Code/Java/OnDeck/src/main/resources/assets/Another Day on Altair 5.dfpwm.gz"));
			input2 = new FileInputStream("/home/cloud/Code/Minecraft/computercraft/DFPWM/wbtest.dfpwm");
			this.dfpwm = new DFPWM(true);
			dfpwm_format = new AudioFormat(32768, 8, 1, true, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	int x = 0;

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
//		MemoryUtil.memFree((Buffer) this.buffer);
		input3.close();
		input2.close();
//		input3.close();
	}

	@Override
	public ByteBuffer getBuffer(int size) throws IOException {
		// Instantiate our buffer of a given size, which should be our sample rate.
		buffer = BufferUtils.createByteBuffer(size);
		// then we go ahead and read this amount of bytes before decoding.
		int sampleSize = 128;
		boolean all_read = false;
		// enter this loop, which will run 4 times, which is enough to decode 1/4 a
		// second in dfpwm audio (at 1x speed), or until we hit the end of the data receieved.
		for (int i = 0; i < 4 && !all_read; i++) {
			// create the array we will read into.
			byte[] cmpdata = new byte[sampleSize];
			// and start a counter variable (taken from dfpwm code)
			int ctr = 0;
			// loop until 128 counted, or we reach the end of the file
			for (ctr = 0; ctr < sampleSize;) {
				int amt = input2.read(cmpdata, ctr, sampleSize - ctr);
				if (amt == -1) {
					all_read = true;
					break;
				}
				ctr += amt;
			}
			byte[] pcmout = new byte[cmpdata.length * 8];
			dfpwm.decompress(pcmout, cmpdata, 0, 0, cmpdata.length);
			for (int j = 0; j < cmpdata.length * 8 && buffer.hasRemaining(); j++) {
				pcmout[j] ^= (byte) 0x80;
				buffer.put(pcmout[j]);
			}
		}
		buffer.flip();
		return buffer;
	}

	private byte[] read(int size) throws IOException {
		byte[] read = new byte[size];
		int len = input2.read(read, 0, size);
		if (len == -1)
			return null;
		if (len == size)
			return read;
		else
			return Arrays.copyOf(read, len);
	}

	@Override
	public AudioFormat getFormat() {
//		return test.getFormat();
		return dfpwm_format;
	}

}
