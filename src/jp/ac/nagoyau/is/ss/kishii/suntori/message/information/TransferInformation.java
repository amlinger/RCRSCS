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
 * 救助隊の移動経路情報を表すクラスです．<br>
 * This class represent the information of pathway of the rescue angets.
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
	 * <h2>Constructor</h2> Create the information of the agent's pathway.<br>
	 * Included data are follow.
	 * <ul>
	 * <li>time:the time that the message is created.(int)</li>
	 * <li>platoonID:EntityID of the rescue agent(pf,at,fb).</li>
	 * <li>areas:agent's pathway</li>
	 * </ul>
	 * 
	 * @param time
	 *            ステップ数<br>
	 *            step num
	 * @param platoonID
	 *            救助隊のEntityID <br>
	 *            EntityID of the rescue agent
	 * @param areas
	 *            移動エリアのEntityID列<br>
	 *            areas that the agent passed
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
	 * <li>areas:救助隊の移動経路(エリアのEntityID列)</li>
	 * </ul>
	 * <h2>Constructor</h2> Create the information of the agent's pathway.<br>
	 * Included data are follow.
	 * <ul>
	 * <li>time:the time that the message is created.(int)</li>
	 * <li>platoonID:EntityID of the rescue agent(pf,at,fb).</li>
	 * <li>areas:agent's pathway({@literal List<EntityID})</li>
	 * </ul>
	 * 
	 * @param time
	 *            ステップ数<br>
	 *            step num
	 * @param platoonID
	 *            救助隊のEntityID <br>
	 *            EntityID of the rescue agent
	 * @param areas
	 *            移動エリアのEntityID列<br>
	 *            List of the agent's pathway({@literal List<EntityID})
	 */
	public TransferInformation(int time, EntityID platoonID,
			List<EntityID> areas) {
		// this(time, areas.toArray(new EntityID[areas.size()]));
		super(BaseMessageType.TRANSFER_PATHWAY, time);
		this.setData(new EntityIDData(DataType.PLATOON_AGENT, platoonID));
		this.setData(new EntityIDListData(DataType.AREA_LIST, areas));
	}

	/**
	 * コンバート時にシステムが使用するコンストラクタ <br>
	 * The method that the library use to convert the message.
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
	 * 救助隊のEntityIDを取得します．<br>
	 * Return EntityID of the rescue agent.
	 * 
	 * @return 救助隊のEntityID<br>
	 *         EntityID
	 */
	public EntityID getAgentID() {
		return super.getID(DataType.PLATOON_AGENT, 0);
	}

	/**
	 * 救助隊の移動経路を取得します．<br>
	 * Return the rescue agent's pathway
	 * 
	 * @return エリアのEntityIDのリスト<br>
	 *         EntityID list of areas
	 */
	public List<EntityID> getPathway() {
		return super.getEntityIDList(DataType.AREA_LIST, 0);
	}

	@Override
	public EntityID getEntityID() {
		return this.getAgentID();
	}

}
