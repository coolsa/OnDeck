package xyz.coolsa.ondeck.tape;

import java.util.UUID;

import net.minecraft.item.Item;

public class TapeItem extends Item {
	private final int maxBytes;
	/**
	 * Each tape has a UUID which points to a file, which can be read/written as a gzip file.
	 */
	private final UUID uuid;

	public TapeItem(Settings settings) {
		// tape defaults to 1 minute of audio.
		this(settings, 1024 * 4 * 8 * 60);
		// TODO Auto-generated constructor stub
	}

	public TapeItem(Settings settings, int maxBytes) {
		super(settings);
		this.maxBytes = maxBytes;
		this.uuid = UUID.randomUUID();
	}

}
