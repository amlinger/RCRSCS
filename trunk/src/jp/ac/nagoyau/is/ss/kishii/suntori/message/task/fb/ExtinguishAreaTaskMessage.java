package jp.ac.nagoyau.is.ss.kishii.suntori.message.task.fb;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

import jp.ac.nagoyau.is.ss.kishii.suntori.message.BaseMessageType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.DataType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.EntityIDListData;
import rescuecore2.worldmodel.EntityID;

/**
 * 消火指令メッセージクラスです．<br>
 * このクラスはエージェント(消防隊:FB)に，ある地域(Areaの集合)を指定し， そのエリア内の消火活動を指令するためのメッセージクラスです．
 * 
 * @author takefumi
 * 
 */
public class ExtinguishAreaTaskMessage extends FireBrigadeTaskMessage {
	/**
	 * コンストラクタ<br>
	 * 消火指令メッセージを生成します．<br>
	 * 含まれる情報は以下のものです．
	 * <ul>
	 * <li>time:情報生成時に設定した(情報が作成されたと考えられる)時間</li>
	 * <li>ownerID:メッセージ送信者のID</li>
	 * <li>fbID:消防隊のEntityID</li>
	 * <li>areas:消火活動対象となるエリアのEntityID列</li>
	 * </ul>
	 * 
	 * @param time
	 *            ステップ数
	 * @param ownerID
	 *            メッセージ送信者のEntityID
	 * @param fbID
	 *            消防隊のEntityID
	 * @param areas
	 *            エリアのEntityID列
	 * 
	 * @param time
	 *            ステップ数
	 * @param fbID
	 *            消防隊のEntityID
	 * @param areas
	 *            消火活動対象となるエリアのEntityID列
	 */
	public ExtinguishAreaTaskMessage(int time, EntityID ownerID, EntityID fbID,
			EntityID... areas) {
		this(time, ownerID, fbID, Arrays.asList(areas));
	}

	/**
	 * コンストラクタ<br>
	 * 消火指令メッセージを生成します．<br>
	 * 含まれる情報は以下のものです．
	 * <ul>
	 * <li>time:情報生成時に設定した(情報が作成されたと考えられる)時間</li>
	 * <li>ownerID:メッセージ送信者のID</li>
	 * <li>fbID:消防隊のEntityID</li>
	 * <li>areas:消火活動対象となるエリアのEntityID列</li>
	 * </ul>
	 * 
	 * @param time
	 *            ステップ数
	 * @param ownerID
	 *            メッセージ送信者のEntityID
	 * @param fbID
	 *            消防隊のEntityID
	 * @param areas
	 *            エリアのEntityID列
	 * 
	 * 
	 * @param time
	 *            ステップ数
	 * @param fbID
	 *            消防隊のEntityID
	 * @param areas
	 *            消火活動対象となるエリアのEntityIDリスト
	 */
	public ExtinguishAreaTaskMessage(int time, EntityID ownerID, EntityID fbID,
			List<EntityID> areas) {
		super(BaseMessageType.EXTINGUISH_AREA, time, ownerID, fbID);
		super.setData(new EntityIDListData(DataType.AREA_LIST, areas));
	}

	/**
	 * コンバート時にシステムが使用するコンストラクタ
	 * 
	 * @param bitList
	 * @param offset
	 * @param bitSizeMap
	 */
	public ExtinguishAreaTaskMessage(List<Integer> bitList, int offset,
			EnumMap<DataType, Integer> bitSizeMap) {
		super(BaseMessageType.EXTINGUISH_AREA, bitList, offset, bitSizeMap);
	}

	/**
	 * 消火活動対象エリアのリストを取得します．
	 * 
	 * @return 消火活動エリアのリスト
	 */
	public List<EntityID> getTargetAreaList() {
		return super.getEntityIDList(DataType.AREA_LIST, 0);
	}
}
