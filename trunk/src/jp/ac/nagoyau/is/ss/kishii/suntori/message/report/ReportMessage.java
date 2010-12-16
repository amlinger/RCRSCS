package jp.ac.nagoyau.is.ss.kishii.suntori.message.report;

import java.util.EnumMap;
import java.util.List;

import jp.ac.nagoyau.is.ss.kishii.suntori.message.BaseMessageType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.RCRSCSMessage;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.DataType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.EntityIDData;
import rescuecore2.worldmodel.EntityID;

/**
 * 報告メッセージを表現するためのメッセージクラスです．
 * 
 * @author takefumi
 * 
 */
public abstract class ReportMessage extends RCRSCSMessage {
	public ReportMessage(BaseMessageType type, int time, EntityID platoonID) {
		super(type, time);
		super.setData(new EntityIDData(DataType.PLATOON_AGENT, platoonID));
	}

	/**
	 * コンバート時にシステムが使用するコンストラクタ
	 * 
	 * @param type
	 * @param bitList
	 * @param offset
	 * @param bitSizeMap
	 */
	public ReportMessage(BaseMessageType type, List<Integer> bitList,
			int offset, EnumMap<DataType, Integer> bitSizeMap) {
		super(type, bitList, offset, bitSizeMap);
	}

	/**
	 * メッセージを送信したエージェント(救助隊)のEntityIDを取得します．
	 * 
	 * @return 救助隊のEntityID
	 */
	public EntityID getAssignedAgentID() {
		return super.getID(DataType.PLATOON_AGENT, 0);
	}
}
