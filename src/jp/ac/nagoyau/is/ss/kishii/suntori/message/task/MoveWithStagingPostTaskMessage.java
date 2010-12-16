package jp.ac.nagoyau.is.ss.kishii.suntori.message.task;

import java.util.EnumMap;
import java.util.List;

import jp.ac.nagoyau.is.ss.kishii.suntori.message.BaseMessageType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.DataType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.EntityIDData;
import rescuecore2.worldmodel.EntityID;

/**
 * 移動タスクメッセージクラスです．<br>
 * このクラスでは，メッセージに移動中継地点含めることができます．
 * <p>
 * このタスクは一時的に確保してあるもので，今後削除される可能性があります．<br>
 * 使用はお勧めしません．
 * </p>
 * 
 * @author takefumi
 * 
 */
public class MoveWithStagingPostTaskMessage extends TaskMessage {
	/**
	 * コンストラクタ<br>
	 * 移動タスクメッセージを生成します．<br>
	 * 含まれる情報は以下のものです．
	 * <ul>
	 * <li>time:情報生成時に設定した(情報が作成されたと考えられる)時間</li>
	 * <li>targetAgentID:救助隊のEntityID</li>
	 * <li>destAreaID:目的エリアのEntityID</li>
	 * <li>stagingPointList:中継エリアのEntityID列</li>
	 * </ul>
	 * 
	 * @param time
	 *            ステップ数
	 * @param targetAgentID
	 *            救助隊のEntityID
	 * @param destAreaID
	 *            目的エリアのEntityID
	 * @param stagingPointList
	 *            中継エリアのEntityIDリスト
	 */
	public MoveWithStagingPostTaskMessage(int time, EntityID targetAgentID,
			EntityID destAreaID, List<EntityID> stagingPointList) {
		super(BaseMessageType.MOVE_WITH_STAGING_POST_TASK, time);
		// this.setData(new ValueData(DataType.PLATOON_AGENT,
		// targetAgentID.getValue()));
		this.setData(new EntityIDData(DataType.PLATOON_AGENT, targetAgentID));
		// this.setData(new ValueData(DataType.AREA, destAreaID.getValue()));
		this.setData(new EntityIDData(DataType.AREA, destAreaID));
		this.setEntityIDListData(DataType.AREA_LIST, stagingPointList);
	}

	/**
	 * コンバート時にシステムが使用するコンストラクタ
	 * 
	 * @param bitList
	 * @param offset
	 * @param bitSizeMap
	 */
	public MoveWithStagingPostTaskMessage(List<Integer> bitList, int offset,
			EnumMap<DataType, Integer> bitSizeMap) {
		super(BaseMessageType.MOVE_WITH_STAGING_POST_TASK, bitList, offset,
				bitSizeMap);
	}

	/**
	 * 目的地点のEntityIDを取得します．
	 * 
	 * @return エリアのEntityID
	 */
	public EntityID getDestinationAreaID() {
		return super.getID(DataType.AREA, 0);
	}

	/**
	 * 中継地点のEntityIDリストを取得します．
	 * 
	 * @return エリアのEntityIDリスト
	 */
	public List<EntityID> getStagingAreaIDs() {
		return super.getEntityIDList(DataType.AREA_LIST, 0);
	}
}
