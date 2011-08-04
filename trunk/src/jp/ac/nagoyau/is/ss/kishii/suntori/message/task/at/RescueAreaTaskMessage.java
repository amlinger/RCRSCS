package jp.ac.nagoyau.is.ss.kishii.suntori.message.task.at;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

import jp.ac.nagoyau.is.ss.kishii.suntori.message.BaseMessageType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.DataType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.EntityIDListData;
import rescuecore2.worldmodel.EntityID;

/**
 * 救急指令メッセージクラスです．<br>
 * このクラスはエージェント(救急隊：AT)に，ある地域(Areaの集合)を指定し，
 * そのエリア内の被災者を捜索して救助することを指令するためのメッセージクラスです．<br>
 * The RescueAreaTaskMessage show the message that command to rescue.<br>
 * This class inform some areas (set of area) and order to search and rescue
 * victim in this area.
 * 
 * 
 * @author takefumi
 * 
 */
public class RescueAreaTaskMessage extends AmbulanceTeamTaskMessage {
	/**
	 * コンストラクタ<br>
	 * 救急指令メッセージを生成します．<br>
	 * 含まれる情報は以下のものです．
	 * <ul>
	 * <li>time:情報生成時に設定した(情報が作成されたと考えられる)時間</li>
	 * <li>ownerID:メッセージ送信者のID</li>
	 * <li>atID:救急隊のEntityID</li>
	 * <li>areas:捜索対象となるエリアのEntityID列</li>
	 * </ul>
	 * <h2>Constructor</h2> Create the message to order to rescue to AT. <br>
	 * Included data are follow.<br>
	 * <ul>
	 * <li>time:the time that the message is created.(int)</li>
	 * <li>ownerID:EntityID of the agent that sent this message.</li>
	 * <li>atID:EntityID of AT that is ordered to do.</li>
	 * <li>areas:EntityIDs of area that are search target</li>
	 * </ul>
	 * 
	 * @param time
	 *            ステップ数<br>
	 *            step num
	 * @param ownerID
	 *            メッセージ送信者のEntityID<br>
	 *            EntityID of the agent that sent this message.
	 * @param atID
	 *            救助隊のEntityID<br>
	 *            EntityID of AT.
	 * @param areas
	 *            エリアのEntityID列<br>
	 *            EntityIDs of area
	 */
	public RescueAreaTaskMessage(int time, EntityID ownerID, EntityID atID,
			EntityID... areas) {
		this(time, ownerID, atID, Arrays.asList(areas));
	}

	/**
	 * コンストラクタ<br>
	 * 救急指令メッセージを生成します．<br>
	 * 含まれる情報は以下のものです．
	 * <ul>
	 * <li>time:情報生成時に設定した(情報が作成されたと考えられる)時間</li>
	 * <li>ownerID:メッセージ送信者のID</li>
	 * <li>atID:救急隊のEntityID</li>
	 * <li>areas:捜索対象となるエリアのEntityID列</li>
	 * </ul>
	 * <h2>Constructor</h2> Create the message to order rescue to AT. <br>
	 * Included data are follow.<br>
	 * <ul>
	 * <li>time:the time that the message is created.(int)</li>
	 * <li>ownerID:EntityID of the agent that sent this message.</li>
	 * <li>atID:EntityID of AT that is ordered to do.</li>
	 * <li>areas:List of EntityIDs of area that are search target</li>
	 * </ul>
	 * 
	 * @param time
	 *            ステップ数<br>
	 *            step num
	 * @param ownerID
	 *            メッセージ送信者のEntityID<br>
	 *            EntityID of the agent that sent this message
	 * @param atID
	 *            救助隊のEntityID<br>
	 *            EntityID of the AT
	 * @param areas
	 *            エリアのEntityIDリスト<br>
	 *            List of EntityID
	 */
	public RescueAreaTaskMessage(int time, EntityID ownerID, EntityID atID,
			List<EntityID> areas) {
		super(BaseMessageType.RESCUE_AREA, time, ownerID, atID);
		this.setData(new EntityIDListData(DataType.AREA_LIST, areas));
	}

	/**
	 * コンバート時システムが使用するコンストラクタ <br>
	 * The method that the library use to convert the message.
	 * 
	 * @param bitList
	 * @param offset
	 * @param bitSizeMap
	 */
	public RescueAreaTaskMessage(List<Integer> bitList, int offset,
			EnumMap<DataType, Integer> bitSizeMap) {
		super(BaseMessageType.RESCUE_AREA, bitList, offset, bitSizeMap);
	}

	/**
	 * 捜索対象エリアのリストを取得します．<br>
	 * Return the list of areas that are target of the search.
	 * 
	 * @return 捜索エリアのリスト<br>
	 *         Return list of areas({@literal List<EntityID>})
	 */
	public List<EntityID> getTargetAreaList() {
		return super.getEntityIDList(DataType.AREA_LIST, 0);
	}

}
