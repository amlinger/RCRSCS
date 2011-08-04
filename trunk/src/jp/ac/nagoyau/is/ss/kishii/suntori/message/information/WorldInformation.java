package jp.ac.nagoyau.is.ss.kishii.suntori.message.information;

import java.util.EnumMap;
import java.util.List;

import jp.ac.nagoyau.is.ss.kishii.suntori.message.BaseMessageType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.RCRSCSMessage;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.DataType;
import rescuecore2.worldmodel.EntityID;

/**
 * 災害空間における情報を表現するためのabstract classです．<br>
 * The　abstract class represent the information that can obtain from the
 * disaster space.
 * 
 * @author takefumi
 * 
 */
public abstract class WorldInformation extends RCRSCSMessage {
	/**
	 * コンストラクタ<br>
	 * <h2>Constructor</h2>
	 * 
	 * @param type
	 * @param time
	 */
	public WorldInformation(BaseMessageType type, int time) {
		super(type, time);
	}

	/**
	 * コンバート時にシステムが使用するコンストラクタ<br>
	 * The method that the library use to convert the message.
	 * 
	 * @param type
	 * @param bitList
	 * @param offset
	 * @param bitSizeMap
	 */
	public WorldInformation(BaseMessageType type, List<Integer> bitList,
			int offset, EnumMap<DataType, Integer> bitSizeMap) {
		super(type, bitList, offset, bitSizeMap);
	}

	/**
	 * この情報の実体のEntityIDを取得します.
	 * 
	 * @return
	 */
	public abstract EntityID getEntityID();
}
