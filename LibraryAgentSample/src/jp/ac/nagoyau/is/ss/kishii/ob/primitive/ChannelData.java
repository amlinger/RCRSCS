package jp.ac.nagoyau.is.ss.kishii.ob.primitive;

public abstract class ChannelData {
	protected int channel;

	/**
	 * メッセージ最大容量(byte)
	 */
	protected int size;

	/**
	 * メッセージの欠損が起こるかどうか(送られたこと自体は知覚できる)
	 */
	private boolean useDrop;
	/**
	 * メッセージ欠損確率
	 */
	private double dropProbability;

	public ChannelData(int channel, int size, double dropProbability) {
		this.channel = channel;
		this.size = size;
		if (dropProbability > 0) {
			this.useDrop = true;
			this.dropProbability = dropProbability;
		} else {
			this.useDrop = false;
			this.dropProbability = 0;
		}
	}

	public ChannelData(int channel, int size) {
		this(channel, size, 0);
	}

	public int getChannelNumber() {
		return this.channel;
	}

	public int getMaxMessageSize() {
		return this.size;
	}

	public boolean useDrop() {
		return this.useDrop;
	}

	public double getDropProbability() {
		return this.dropProbability;
	}

}
