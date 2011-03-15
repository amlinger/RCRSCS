package jp.ac.nagoyau.is.ss.kishii.suntori.message.task;

import java.util.EnumMap;
import java.util.List;

import jp.ac.nagoyau.is.ss.kishii.suntori.message.BaseMessageType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.DataType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.EntityIDData;
import rescuecore2.worldmodel.EntityID;

/**
 * 休憩タスクメッセージクラスです．<br>
 * このクラスはエージェントに避難所でrestさせるためのメッセージです．
 * <p>
 * このタスクは一時的に確保してあるもので，今後削除される可能性があります．<br>
 * 使用はお勧めしません．
 * </p>
 * 
 * @author takefumi
 * 
 */
public class RestAtRefugeTaskMessage extends TaskMessage {
	/**
	 * コンストラクタ<br>
	 * 休憩タスクメッセージを生成します．<br>
	 * 含まれる情報は以下のものです．
	 * <ul>
	 * <li>time:情報生成時に設定した(情報が作成されたと考えられる)時間</li>
	 * <li>ownerID:メッセージ送信者のID</li>
	 * <li>targetAgentID:救助隊のEntityID</li>
	 * <li>refugeID:避難所のEntityID</li>
	 * </ul>
	 * 
	 * @param time
	 *            ステップ数
	 * @param ownerID
	 *            メッセージ送信者のEntityID
	 * @param targetAgentID
	 *            救助隊のEntityID
	 * @param refugeID
	 *            避難所のEntityID
	 */
	public RestAtRefugeTaskMessage(int time, EntityID ownerID,
			EntityID targetAgentID, EntityID refugeID) {
		super(BaseMessageType.REST_AT_REFUGE_TASK, time, ownerID);
		// this.setData(new ValueData(DataType.PLATOON_AGENT,
		// targetAgentID.getValue()));
		this.setData(new EntityIDData(DataType.PLATOON_AGENT, targetAgentID));
		// this.setData(new ValueData(DataType.REFUGE, refugeID.getValue()));
		this.setData(new EntityIDData(DataType.REFUGE, refugeID));
	}

	/**
	 * コンバート時にシステムが使用するコンストラクタ
	 * 
	 * @param bitList
	 * @param offset
	 * @param bitSizeMap
	 */
	public RestAtRefugeTaskMessage(List<Integer> bitList, int offset,
			EnumMap<DataType, Integer> bitSizeMap) {
		super(BaseMessageType.REST_AT_REFUGE_TASK, bitList, offset, bitSizeMap);
	}

	/**
	 * 休憩する避難所のEntityIDを取得します．
	 * 
	 * @return 避難所のEntityID
	 */
	public EntityID getRefugeID() {
		return super.getID(DataType.REFUGE, 0);
	}
}
