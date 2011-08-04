package jp.ac.nagoyau.is.ss.kishii.suntori.message;

import java.util.List;

import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.RCRSCSData;

public interface IMessage {
	/**
	 * メッセージに入っているデータのリストを取得します．<br>
	 * Return list of data registered on the message.
	 * 
	 * @return
	 */
	public List<RCRSCSData<?>> getData();

	/**
	 * メッセージにデータを追加する．<br>
	 * Register data on the message.
	 * 
	 * @param data
	 *            setted data
	 */
	public void setData(RCRSCSData<?> data);

	/**
	 * 指定されたインデックスの場所にメッセージにデータを追加する．<br>
	 * Register data on the message　using specified index.<br>
	 * If message have some DataType.AREA, we can assign where to be setted the
	 * adding data.
	 * 
	 * @param data
	 *            setted data
	 * @param index
	 */
	public void setData(RCRSCSData<?> data, int index);

}
