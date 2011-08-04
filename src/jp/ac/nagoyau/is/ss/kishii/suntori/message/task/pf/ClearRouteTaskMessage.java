package jp.ac.nagoyau.is.ss.kishii.suntori.message.task.pf;

import java.util.EnumMap;
import java.util.List;

import jp.ac.nagoyau.is.ss.kishii.suntori.message.BaseMessageType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.DataType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.EntityIDData;
import rescuecore2.worldmodel.EntityID;

/**
 * 経路解放指令メッセージクラスです．<br>
 * このクラスはエージェント(啓開隊:PF)に，あるエリアからあるエリアまでの経路を確保することを指令するためのメッセージクラスです． <br>
 * The ClearRouteTaskMessage represent the message that command to clear roads.<br>
 * This class instruct PF to secure a route from area A to area B.
 * 
 * @author takefumi
 * 
 */
public class ClearRouteTaskMessage extends PoliceForceTaskMessage {
	/**
	 * コンストラクタ<br>
	 * 経路解放指令メッセージを生成します．<br>
	 * 含まれる情報は以下のものです．
	 * <ul>
	 * <li>time:情報生成時に設定した(情報が作成されたと考えられる)時間</li>
	 * <li>ownerID:メッセージ送信者のID</li>
	 * <li>pfID:啓開隊のEntityID</li>
	 * <li>departure:経路の出発地点</li>
	 * <li>destination:経路の目標地点</li>
	 * </ul>
	 * <h2>Constructor</h2> Create the message to order to clear route. <br>
	 * Included data are follow.<br>
	 * <ul>
	 * <li>time:the time that the message is created.(int)</li>
	 * <li>ownerID:EntityID of the agent that sent this message.</li>
	 * <li>pfID:EntityID of PF that is ordered to do.</li>
	 * <h2>Constructor</h2> Create the message to order fight fires to FB. <br>
	 * Included data are follow.<br>
	 * <ul>
	 * <li>time:the time that the message is created.(int)</li>
	 * <li>ownerID:EntityID of the agent that sent this message.</li>
	 * <li>fbID:EntityID of FB that is ordered to do.</li>
	 * <li>departure:departure of the route</li>
	 * <li>destination:destination of the route</li>
	 * </ul>
	 * 
	 * @param time
	 *            ステップ数<br>
	 *            step num
	 * @param ownerID
	 *            メッセージ送信者のEntityID<br>
	 *            EntityID of the agent that sent this message.
	 * @param pfID
	 *            啓開隊のEntityID<br>
	 *            EntityID of PF
	 * @param departure
	 *            出発エリアのEntityiD<br>
	 *            EntityID of departure area
	 * @param destination
	 *            目標エリアのEntityID<br>
	 *            EntityID of the destination area
	 */
	public ClearRouteTaskMessage(int time, EntityID ownerID, EntityID pfID,
			EntityID departure, EntityID destination) {
		super(BaseMessageType.CLEAR_ROUTE, time, ownerID, pfID);
		super.setData(new EntityIDData(DataType.AREA, departure), 0);
		super.setData(new EntityIDData(DataType.AREA, destination), 1);
	}

	/**
	 * コンバート時システムが使用するコンストラクタ <br>
	 * The method that the library use to convert the message.
	 * 
	 * @param bitList
	 * @param offset
	 * @param bitSizeMap
	 */
	public ClearRouteTaskMessage(List<Integer> bitList, int offset,
			EnumMap<DataType, Integer> bitSizeMap) {
		super(BaseMessageType.CLEAR_ROUTE, bitList, offset, bitSizeMap);
	}

	/**
	 * 出発地点のEntityID<br>
	 * EntityID of daparture area
	 * 
	 * @return エリアのEntityID<br>
	 *         EntityID of area
	 */
	public EntityID getDepartureAreaID() {
		return getID(DataType.AREA, 0);
	}

	/**
	 * 目標地点のEntityID<br>
	 * EntityID of destination area
	 * 
	 * @return エリアのEntityID<br>
	 *         EntityID of area
	 */
	public EntityID getDestinationAreaID() {
		return getID(DataType.AREA, 1);
	}
}
