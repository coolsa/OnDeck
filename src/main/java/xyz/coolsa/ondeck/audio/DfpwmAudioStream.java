package xyz.coolsa.ondeck.audio;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Queue;

import javax.sound.sampled.AudioFormat;

import org.jetbrains.annotations.Nullable;
import org.lwjgl.BufferUtils;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.AudioStream;
import xyz.coolsa.ondeck.OnDeckConstants;

/**
 * This class creates a DFPWM audio stream, which when created can be extended
 * with more audio. Once the audio runs out, then it should auto-close, but new
 * audio can be added before this by calling the "add data" function
 * 
 * @author coolsa
 *
 */
@Environment(EnvType.CLIENT)
public class DfpwmAudioStream implements AudioStream {
	boolean open = true;
	InputStream input;
	AudioStream test;
	DFPWM dfpwm;
	ByteBuffer buffer;
	Queue<ByteBuffer> audioQueue;

	/**
	 * Create a new dfpwm audio stream. Currently the inputstream is for debugging,
	 * but may not exist.
	 */
	public DfpwmAudioStream(@Nullable InputStream input) {
		try {
			this.audioQueue = new LinkedList<ByteBuffer>();
			this.input = input;
			;
			this.dfpwm = new DFPWM(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	int x = 0;

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
//		MemoryUtil.memFree((Buffer) this.buffer);
		if (input != null)
			this.input.close();
		this.open = false;
//		input3.close();
	}

	/**
	 * This function will return a buffer of bytes, which will be decoded PCM audio.
	 * This is not accessed by normal means, but instead by the games code. the size
	 * is the sample rate in hertz, which in DFPWM's case is 2^15. 1 byte per
	 * sample, so 1 bit makes 1 byte. times 8 yo!
	 */
	@Override
	public ByteBuffer getBuffer(int size) throws IOException {
		// Instantiate our buffer of a given size, which should be our sample rate.
		// a bit more for safety tho.
		buffer = BufferUtils.createByteBuffer(size + 8192);
		// we will read this amount of bytes before decoding.
		int sampleSize = 128;
		// enter this loop, which will run 4 times, which is enough to decode 1/4 a
		// second in dfpwm audio (at 1x speed), or until we hit the end of the data
		// receieved.
		while (!audioQueue.isEmpty() && buffer.position() < size) {
			// access the byteBuffer we will read.
			ByteBuffer reading = audioQueue.peek();
			// create the array we will read into.
			byte[] cmpdata = new byte[sampleSize];
			// amount of read bits.
			int read = 0;
			try {
				// now attempt to read exactly array size into array from buffer.
				reading.get(cmpdata);
				read = sampleSize;
			} catch (BufferUnderflowException e) {
				// if that fails, just read the rest of the buffer and remove the current one.
				read = reading.limit() - reading.position();
				reading.get(cmpdata, 0, read);
				audioQueue.poll();
			}
			byte[] pcmout = new byte[read * 8];
			dfpwm.decompress(pcmout, cmpdata, 0, 0, read);
			for (int j = 0; j < pcmout.length; j++) {
				pcmout[j] ^= (byte) 0x80;
			}
			buffer.put(pcmout);
		}
		buffer.flip();
		return buffer;
	}

	/**
	 * 
	 * @param recieved The raw DFPWM packet, to be added to queue.
	 */
	public void addPacket(byte[] recieved, int read) {
		ByteBuffer toAdd = BufferUtils.createByteBuffer(read);
		toAdd.put(recieved, 0, read);
		// end write and make readable.
		toAdd.flip();
		this.audioQueue.add(toAdd);
	}

	@Override
	public AudioFormat getFormat() {
		return OnDeckConstants.DFPWM_DECODED_FORMAT;
	}

	public boolean isOpen() {
		return this.open;
	}

}
