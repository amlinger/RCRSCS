package jp.ac.nagoyau.is.ss.kishii.suntori.message.information;

import java.util.EnumMap;
import java.util.List;

import jp.ac.nagoyau.is.ss.kishii.suntori.message.BaseMessageType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.DataType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.EntityIDData;
import rescuecore2.misc.Pair;
import rescuecore2.worldmodel.EntityID;

/**
 * 救助隊の位置座標情報を表すクラスです．<br>
 * The class represent the location information of rescue agent.
 * 
 * @author takefumi
 * 
 */
public class PositionInformation extends WorldInformation {
	/**
	 * コンストラクタ<br>
	 * 救助隊の位置座標情報を生成します．<br>
	 * 含まれる情報は以下のものです．
	 * <ul>
	 * <li>time:情報生成時に設定した(情報が作成されたと考えられる)時間</li>
	 * <li>agentID:救助隊のEntityID</li>
	 * <li>coordinate:救助隊の現在位置座標</li>
	 * </ul>
	 * <h2>Constructor</h2> Create the information of the agent's location.<br>
	 * Included data are follow.
	 * <ul>
	 * <li>time:the time that the message is created.(int)</li>
	 * <li>agentID:EntityID of the rescue agent.</li>
	 * <li>coordinate:agent location.({@literal Pair<Integer,Integer>})</li>
	 * </ul>
	 * 
	 * @param time
	 *            ステップ数<br>
	 *            step num
	 * @param platoonID
	 *            救助隊のEntityID<br>
	 *            EntityID of the rescue agent
	 * @param cor
	 *            救助隊の位置座標<br>
	 *            agent location
	 */
	public PositionInformation(int time, EntityID platoonID,
			Pair<Integer, Integer> cor) {
		super(BaseMessageType.POSITION, time);
		super.setData(new EntityIDData(DataType.PLATOON_AGENT, platoonID));
		super.setCoorinate(cor);
	}

	/**
	 * コンバート時にシステムが使用するコンストラクタ<br>
	 * The method that the library use to convert the message.
	 * 
	 * @param bitList
	 * @param offset
	 * @param bitSizeMap
	 */
	public PositionInformation(List<Integer> bitList, int offset,
			EnumMap<DataType, Integer> bitSizeMap) {
		super(BaseMessageType.POSITION, bitList, offset, bitSizeMap);
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
	 * 救助隊の位置座標を取得します．<br>
	 * Return the coordinate of the agent.
	 * 
	 * @return 位置座標<br>
	 *         coordinate({@literal Pair<Integer,Integer>})
	 */
	public Pair<Integer, Integer> getCoordinate() {
		return super.getCoodinate(0);
	}

	@Override
	public EntityID getEntityID() {
		return this.getAgentID();
	}
}
