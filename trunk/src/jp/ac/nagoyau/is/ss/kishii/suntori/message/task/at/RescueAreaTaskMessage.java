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
 * そのエリア内の被災者を捜索して救助することを指令するためのメッセージクラスです．
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
	 * <li>atID:救急隊のEntityID</li>
	 * <li>areas:捜索対象となるエリアのEntityID列</li>
	 * </ul>
	 * 
	 * @param time
	 *            ステップ数
	 * @param atID
	 *            救助隊のEntityID
	 * @param areas
	 *            エリアのEntityID列
	 */
	public RescueAreaTaskMessage(int time, EntityID atID, EntityID... areas) {
		this(time, atID, Arrays.asList(areas));
	}

	/**
	 * コンストラクタ<br>
	 * 救急指令メッセージを生成します．<br>
	 * 含まれる情報は以下のものです．
	 * <ul>
	 * <li>time:情報生成時に設定した(情報が作成されたと考えられる)時間</li>
	 * <li>atID:救急隊のEntityID</li>
	 * <li>areas:捜索対象となるエリアのEntityID列</li>
	 * </ul>
	 * 
	 * @param time
	 *            ステップ数
	 * @param atID
	 *            救助隊のEntityID
	 * @param areas
	 *            エリアのEntityIDリスト
	 */
	public RescueAreaTaskMessage(int time, EntityID atID, List<EntityID> areas) {
		super(BaseMessageType.RESCUE_AREA, time, atID);
		this.setData(new EntityIDListData(DataType.AREA_LIST, areas));
	}

	/**
	 * コンバート時にシステムが使用するコンストラクタ
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
	 * 捜索対象エリアのリストを取得します．
	 * 
	 * @return 捜索エリアのリスト
	 */
	public List<EntityID> getTargetAreaList() {
		return super.getEntityIDList(DataType.AREA_LIST, 0);
	}

}
