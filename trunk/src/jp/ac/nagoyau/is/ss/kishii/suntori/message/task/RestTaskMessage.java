package jp.ac.nagoyau.is.ss.kishii.suntori.message.task;

import java.util.EnumMap;
import java.util.List;

import jp.ac.nagoyau.is.ss.kishii.suntori.message.BaseMessageType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.DataType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.EntityIDData;
import rescuecore2.worldmodel.EntityID;

/**
 * 休憩タスクメッセージクラスです．<br>
 * <p>
 * このタスクは一時的に確保してあるもので，今後削除される可能性があります．<br>
 * 使用はお勧めしません．
 * </p>
 * 
 * @author takefumi
 * 
 */
public class RestTaskMessage extends TaskMessage {
	/**
	 * コンストラクタ<br>
	 * 休憩タスクメッセージを生成します．<br>
	 * 含まれる情報は以下のものです．
	 * <ul>
	 * <li>time:情報生成時に設定した(情報が作成されたと考えられる)時間</li>
	 * <li>targetAgentID:救助隊のEntityID</li>
	 * </ul>
	 * 
	 * @param time
	 *            ステップ数
	 * @param targetAgentID
	 *            救助隊のEntityID
	 */
	public RestTaskMessage(int time, EntityID targetAgentID) {
		super(BaseMessageType.REST_TASK, time);
		// this.setData(new ValueData(DataType.PLATOON_AGENT,
		// targetAgentID.getValue()));
		this.setData(new EntityIDData(DataType.PLATOON_AGENT, targetAgentID));
	}

	/**
	 * コンバート時にシステムが使用するコンストラクタ
	 * 
	 * @param bitList
	 * @param offset
	 * @param bitSizeMap
	 */
	public RestTaskMessage(List<Integer> bitList, int offset,
			EnumMap<DataType, Integer> bitSizeMap) {
		super(BaseMessageType.REST_TASK, bitList, offset, bitSizeMap);
	}

}