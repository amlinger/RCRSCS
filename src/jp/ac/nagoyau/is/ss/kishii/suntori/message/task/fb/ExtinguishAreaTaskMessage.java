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
 * このクラスはエージェント(消防隊:FB)に，ある地域(Areaの集合)を指定し， そのエリア内の消火活動を指令するためのメッセージクラスです． <br>
 * The ExtinguishAreaTaskMessage represent the message that command to
 * extinguish.<br>
 * This class inform some areas (set of area) to FB and order to search and
 * fight fires in this area.
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
	 * <h2>Constructor</h2> Create the message to order to fight fires to FB. <br>
	 * Included data are follow.<br>
	 * <ul>
	 * <li>time:the time that the message is created.(int)</li>
	 * <li>ownerID:EntityID of the agent that sent this message.</li>
	 * <li>fbID:EntityID of FB that is ordered to do.</li>
	 * <li>areas:EntityIDs of area that are search target</li>
	 * </ul>
	 * 
	 * @param time
	 *            ステップ数<br>
	 *            step num
	 * @param ownerID
	 *            メッセージ送信者のEntityID<br>
	 *            EntityID of the agent that sent this message.
	 * @param fbID
	 *            救助隊のEntityID<br>
	 *            EntityID of FB.
	 * @param areas
	 *            エリアのEntityID列<br>
	 *            EntityIDs of area
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
	 * <h2>Constructor</h2> Create the message to order fight fires to FB. <br>
	 * Included data are follow.<br>
	 * <ul>
	 * <li>time:the time that the message is created.(int)</li>
	 * <li>ownerID:EntityID of the agent that sent this message.</li>
	 * <li>fbID:EntityID of FB that is ordered to do.</li>
	 * <li>areas:List of EntityIDs of area that are search target</li>
	 * </ul>
	 * 
	 * @param time
	 *            ステップ数<br>
	 *            step num
	 * @param ownerID
	 *            メッセージ送信者のEntityID<br>
	 *            EntityID of the agent that sent this message
	 * @param fbID
	 *            救助隊のEntityID<br>
	 *            EntityID of the FB
	 * @param areas
	 *            エリアのEntityIDリスト<br>
	 *            List of EntityID
	 */
	public ExtinguishAreaTaskMessage(int time, EntityID ownerID, EntityID fbID,
			List<EntityID> areas) {
		super(BaseMessageType.EXTINGUISH_AREA, time, ownerID, fbID);
		super.setData(new EntityIDListData(DataType.AREA_LIST, areas));
	}

	/**
	 * コンバート時システムが使用するコンストラクタ <br>
	 * The method that the library use to convert the message.
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
	 * 消火活動対象エリアのリストを取得します．<br>
	 * Return list of EntityID of areas that are target of fire fight
	 * 
	 * @return 消火活動エリアのリスト<br>
	 *         List of fire fight targets({@literal List<EntityID>})
	 */
	public List<EntityID> getTargetAreaList() {
		return super.getEntityIDList(DataType.AREA_LIST, 0);
	}
}
