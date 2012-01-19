package jp.ac.nagoyau.is.ss.kishii.ob.primitive;

/**
 * 声によるチャンネルに関する設定データを表すクラス
 * 
 * @author takefumi
 * 
 */
public class VoiceChannelData extends ChannelData {
	/**
	 * 可聴範囲
	 */
	private int range;

	public VoiceChannelData(int channel, int range, int size) {
		super(channel, size);
		this.range = range;
	}

	public VoiceChannelData(int channel, int range, int size,
			double dropProbability) {
		super(channel, size, dropProbability);
		this.range = range;
	}

	public int getRange() {
		return this.range;
	}

}
