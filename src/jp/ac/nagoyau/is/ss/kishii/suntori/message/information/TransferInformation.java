package jp.ac.nagoyau.is.ss.kishii.suntori.message.information;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import jp.ac.nagoyau.is.ss.kishii.suntori.message.BaseMessageType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.DataType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.EntityIDData;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.EntityIDListData;
import rescuecore2.worldmodel.EntityID;

/**
 * 救助隊の移動経路情報を表すクラスです．
 * 
 * @author takefumi
 * 
 */
public class TransferInformation extends WorldInformation {
	/**
	 * コンストラクタ<br>
	 * 救助隊の移動経路情報を生成します．<br>
	 * 含まれる情報は以下のものです．
	 * <ul>
	 * <li>time:情報生成時に設定した(情報が作成されたと考えられる)時間</li>
	 * <li>platoonID:救助隊のEntityID</li>
	 * <li>areas:救助隊の移動経路(エリアのEntityID列)</li>
	 * </ul>
	 * 
	 * @param time
	 *            ステップ数
	 * @param platoonID
	 *            救助隊のEntityID
	 * @param areas
	 *            移動エリアのEntityID列
	 */
	public TransferInformation(int time, EntityID platoonID, EntityID... areas) {
		super(BaseMessageType.TRANSFER_PATHWAY, time);
		// List<Integer> ids = new ArrayList<Integer>();
		// for (EntityID id : areas) {
		// ids.add(id.getValue());
		// }
		// this.setData(new ListData(DataType.AREA_LIST, ids));
		this.setData(new EntityIDData(DataType.PLATOON_AGENT, platoonID));
		List<EntityID> areaList = new ArrayList<EntityID>();
		for (EntityID id : areas) {
			areaList.add(id);
		}
		this.setData(new EntityIDListData(DataType.AREA_LIST, areaList));
	}

	/**
	 * コンストラクタ<br>
	 * 救助隊の移動経路情報を生成します．<br>
	 * 含まれる情報は以下のものです．
	 * <ul>
	 * <li>time:情報生成時に設定した(情報が作成されたと考えられる)時間</li>
	 * <li>platoonID:救助隊のEntityID</li>
	 * <li>areas:救助隊の移動経路(エリアのEntityIDリスト)</li>
	 * </ul>
	 * 
	 * @param time
	 *            ステップ数
	 * @param platoonID
	 *            救助隊のEntityID
	 * @param areas
	 *            移動エリアのEntityIDリスト
	 */
	public TransferInformation(int time, EntityID platoonID,
			List<EntityID> areas) {
		// this(time, areas.toArray(new EntityID[areas.size()]));
		super(BaseMessageType.TRANSFER_PATHWAY, time);
		this.setData(new EntityIDData(DataType.PLATOON_AGENT, platoonID));
		this.setData(new EntityIDListData(DataType.AREA_LIST, areas));
	}

	/**
	 * コンバート時にシステムが使用するコンストラクタ
	 * 
	 * @param bitList
	 * @param offset
	 * @param bitSizeMap
	 */
	public TransferInformation(List<Integer> bitList, int offset,
			EnumMap<DataType, Integer> bitSizeMap) {
		super(BaseMessageType.TRANSFER_PATHWAY, bitList, offset, bitSizeMap);
	}

	/**
	 * 救助隊のEntityIDを取得します．
	 * 
	 * @return 救助隊のEntityID
	 */
	public EntityID getAgentID() {
		return super.getID(DataType.PLATOON_AGENT, 0);
	}

	/**
	 * 救助隊の移動経路を取得します．
	 * 
	 * @return エリアのEntityIDのリスト
	 */
	public List<EntityID> getPathway() {
		return super.getEntityIDList(DataType.AREA_LIST, 0);
	}
}
