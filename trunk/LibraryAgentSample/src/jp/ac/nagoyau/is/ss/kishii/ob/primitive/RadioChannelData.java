package jp.ac.nagoyau.is.ss.kishii.ob.primitive;

/**
 * 無線のチャンネルに関する設定データ
 * 
 * @author takefumi
 * 
 */
public class RadioChannelData extends ChannelData {
	private boolean useFailure;
	private double failureProbability;

	public RadioChannelData(int channel, int size) {
		this(channel, size, 0, 0);
	}

	public RadioChannelData(int channel, int size, double dropProbability,
			double failureProbability) {
		super(channel, size, dropProbability);
		if (failureProbability > 0) {
			this.useFailure = true;
			this.failureProbability = failureProbability;
		} else {
			this.useFailure = false;
			this.failureProbability = 0;
		}
	}

	public boolean useFailure() {
		return this.useFailure;
	}

	public double getFailureProbability() {
		return this.failureProbability;
	}
}
