package jp.ac.nagoyau.is.ss.kishii.suntori.message;

import java.util.List;

import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.RCRSCSData;

public interface IMessage {
	/**
	 * Return list of data registered on the message.
	 * 
	 * @return
	 */
	public List<RCRSCSData<?>> getData();

	/**
	 * Register data on the message.
	 * 
	 * @param data
	 *            setted data
	 */
	public void setData(RCRSCSData<?> data);

	/**
	 * Register data on the message using specified index.<br>
	 * If message have some DataType.AREA, we can assign where to be setted the
	 * adding data.
	 * 
	 * @param data
	 *            setted data
	 * @param index
	 */
	public void setData(RCRSCSData<?> data, int index);

}
