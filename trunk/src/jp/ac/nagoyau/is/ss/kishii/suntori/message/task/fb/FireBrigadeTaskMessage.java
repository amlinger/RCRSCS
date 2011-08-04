package jp.ac.nagoyau.is.ss.kishii.suntori.message.task.fb;

import java.util.EnumMap;
import java.util.List;

import jp.ac.nagoyau.is.ss.kishii.suntori.message.BaseMessageType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.DataType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.EntityIDData;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.task.TaskMessage;
import rescuecore2.worldmodel.EntityID;

/**
 * 消防隊専用タスクを表現するためのメッセージクラスです． <br>
 * The class represent the Task for Fire Brigade agent.
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
	 * <li>ownerID:メッセージ送信者のID</li>
	 * <li>fbID:消防隊のEntityID</li>
	 * </ul>
	 * <h2>Constructor</h2> Create the message to give instructions to FB.<br>
	 * Included data are follow.<br>
	 * <ul>
	 * <li>time:the time that the message is created.(int)</li>
	 * <li>ownerID:EntityID of the agent that sent this message.</li>
	 * <li>fbID:EntityID of FB that is ordered to do.</li>
	 * </ul>
	 * 
	 * @param type
	 *            メッセージの種類 <br>
	 *            message type
	 * @param time
	 *            ステップ数<br>
	 *            step num
	 * @param fbID
	 *            救急隊のEntityID<br>
	 *            EntityID of FB
	 */
	public FireBrigadeTaskMessage(BaseMessageType type, int time,
			EntityID ownerID, EntityID fbID) {
		super(type, time, ownerID);
		// this.setData(new ValueData(DataType.POLICE_FORCE, fbID.getValue()));
		this.setData(new EntityIDData(DataType.FIRE_BRIGADE, fbID));
	}

	/**
	 * コンバート時システムが使用するコンストラクタ <br>
	 * The method that the library use to convert the message.
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
	 * このタスクを実行すべき消防隊のEntityIDを取得します．<br>
	 * Return EntityID of FB that have to execute this task.
	 * 
	 * @return 消防隊のEntityID<br>
	 *         EntityID of FB
	 */
	public EntityID getAssignedAgentID() {
		return super.getID(DataType.FIRE_BRIGADE, 0);
	}

}
