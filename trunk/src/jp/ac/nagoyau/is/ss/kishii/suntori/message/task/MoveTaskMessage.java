package jp.ac.nagoyau.is.ss.kishii.suntori.message.task;

import java.util.EnumMap;
import java.util.List;

import jp.ac.nagoyau.is.ss.kishii.suntori.message.BaseMessageType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.DataType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.EntityIDData;
import rescuecore2.worldmodel.EntityID;

/**
 * 移動タスクメッセージクラスです．<br>
 * <p>
 * このタスクは一時的に確保してあるもので，今後削除される可能性があります．<br>
 * 使用はお勧めしません．
 * </p>
 * 
 * @author takefumi
 * 
 */
public class MoveTaskMessage extends TaskMessage {
	/**
	 * コンストラクタ<br>
	 * 移動タスクメッセージを生成します．<br>
	 * 含まれる情報は以下のものです．
	 * <ul>
	 * <li>time:情報生成時に設定した(情報が作成されたと考えられる)時間</li>
	 * <li>targetAgentID:救助隊のEntityID</li>
	 * <li>destAreaID:目的エリアのEntityID</li>
	 * </ul>
	 * 
	 * @param time
	 *            ステップ数
	 * @param targetAgentID
	 *            救助隊のEntityID
	 * @param destAreaID
	 *            目的エリアのEntityID
	 */
	public MoveTaskMessage(int time, EntityID targetAgentID, EntityID destAreaID) {
		super(BaseMessageType.MOVE_TASK, time);
		this.setData(new EntityIDData(DataType.PLATOON_AGENT, targetAgentID));
		// this.setData(new ValueData(DataType.AREA, destAreaID.getValue()));
		this.setData(new EntityIDData(DataType.AREA, destAreaID));
	}

	/**
	 * コンバート時にシステムが使用するコンストラクタ
	 * 
	 * @param bitList
	 * @param offset
	 * @param bitSizeMap
	 */
	public MoveTaskMessage(List<Integer> bitList, int offset,
			EnumMap<DataType, Integer> bitSizeMap) {
		super(BaseMessageType.MOVE_TASK, bitList, offset, bitSizeMap);
	}

	/**
	 * 目的地点のEntityIDを取得します．
	 * 
	 * @return エリアのEntityID
	 */
	public EntityID getDestinationAreaID() {
		return super.getID(DataType.AREA, 0);
	}

}
