package jp.ac.nagoyau.is.ss.kishii.suntori.message.task.pf;

import java.util.EnumMap;
import java.util.List;

import jp.ac.nagoyau.is.ss.kishii.suntori.message.BaseMessageType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.DataType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.EntityIDData;
import rescuecore2.worldmodel.EntityID;

/**
 * 経路解放指令メッセージクラスです．<br>
 * このクラスはエージェント(啓開隊:PF)に，あるエリアからあるエリアまでの経路を確保することを指令するためのメッセージクラスです．
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
	 * <li>pfID:啓開隊のEntityID</li>
	 * <li>departure:経路の出発地点</li>
	 * <li>destination:経路の目標地点</li>
	 * </ul>
	 * 
	 * 
	 * @param time
	 *            ステップ数
	 * @param pfID
	 *            啓開隊のEntityID
	 * @param departure
	 *            出発エリアのEntityiD
	 * @param destination
	 *            目標エリアのEntityID
	 */
	public ClearRouteTaskMessage(int time, EntityID pfID, EntityID departure,
			EntityID destination) {
		super(BaseMessageType.CLEAR_ROUTE, time, pfID);
		super.setData(new EntityIDData(DataType.AREA, departure), 0);
		super.setData(new EntityIDData(DataType.AREA, destination), 0);
	}

	/**
	 * コンバート時にシステムが使用するコンストラクタ
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
	 * 出発地点のEntityID
	 * 
	 * @return エリアのEntityID
	 */
	public EntityID getDepartureAreaID() {
		return getID(DataType.AREA, 0);
	}

	/**
	 * 目標地点のEntityID
	 * 
	 * @return エリアのEntityID
	 */
	public EntityID getDestinationAreaID() {
		return getID(DataType.AREA, 1);
	}
}
