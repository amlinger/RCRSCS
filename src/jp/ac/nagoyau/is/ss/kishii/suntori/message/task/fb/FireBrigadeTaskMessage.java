package jp.ac.nagoyau.is.ss.kishii.suntori.message.task.fb;

import java.util.EnumMap;
import java.util.List;

import jp.ac.nagoyau.is.ss.kishii.suntori.message.BaseMessageType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.DataType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.EntityIDData;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.task.TaskMessage;
import rescuecore2.worldmodel.EntityID;

/**
 * 消防隊専用タスクを表現するためのメッセージクラスです．
 * 
 * @author takefumi
 * 
 */
public abstract class FireBrigadeTaskMessage extends TaskMessage {
	/**
	 * コンストラクタ<br>
	 * 消防隊専用タスクメッセージを生成します．<br>
	 * 含まれる情報は以下のものです．
	 * <ul>
	 * <li>time:情報生成時に設定した(情報が作成されたと考えられる)時間</li>
	 * <li>fbID:消防隊のEntityID</li>
	 * </ul>
	 * 
	 * @param type
	 * @param time
	 * @param atID
	 */
	public FireBrigadeTaskMessage(BaseMessageType type, int time, EntityID fbID) {
		super(type, time);
		// this.setData(new ValueData(DataType.POLICE_FORCE, fbID.getValue()));
		this.setData(new EntityIDData(DataType.FIRE_BRIGADE, fbID));
	}

	/**
	 * コンバート時にシステムが使用するコンストラクタ
	 * 
	 * @param type
	 * @param bitList
	 * @param offset
	 * @param bitSizeMap
	 */
	public FireBrigadeTaskMessage(BaseMessageType type, List<Integer> bitList,
			int offset, EnumMap<DataType, Integer> bitSizeMap) {
		super(type, bitList, offset, bitSizeMap);
	}

	/**
	 * このタスクを実行すべき消防隊のEntityIDを取得します．
	 * 
	 * @return 消防隊のEntityID
	 */
	public EntityID getAssignedAgentID() {
		return super.getID(DataType.FIRE_BRIGADE, 0);
	}

}
