package jp.ac.nagoyau.is.ss.kishii.suntori.message.data;

public class ValueData extends RCRSCSData<Integer> {
	public ValueData(DataType type) {
		super(type);
		this.value = null;
	}

	public ValueData(DataType type, Integer value) {
		super(type);
		this.value = value;
	}

	@Override
	public void setData(Integer value) {
		this.value = value;
	}

}
