package jp.ac.nagoyau.is.ss.kishii.suntori.message;

import java.util.List;

import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.RCRSCSData;

public interface IMessage {
	public List<RCRSCSData<?>> getData();

	public void setData(RCRSCSData<?> data);

	public void setData(RCRSCSData<?> data, int index);

}
