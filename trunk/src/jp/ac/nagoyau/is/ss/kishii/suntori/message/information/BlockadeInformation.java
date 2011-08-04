package jp.ac.nagoyau.is.ss.kishii.suntori.message.information;

import java.util.EnumMap;
import java.util.List;

import jp.ac.nagoyau.is.ss.kishii.suntori.message.BaseMessageType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.DataType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.EntityIDData;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.ValueData;
import rescuecore2.misc.Pair;
import rescuecore2.worldmodel.EntityID;

/**
 * 閉塞の情報を表すクラスです．<br>
 * The BlockadeInformation represent the Information of Blockade.
 * 
 * @author takefumi
 * 
 */
public class BlockadeInformation extends WorldInformation {

	/**
	 * コンストラクタ<br>
	 * 閉塞情報を生成します．<br>
	 * 含まれる情報は以下のものです．
	 * <ul>
	 * <li>time:情報生成時に設定した(情報が作成されたと考えられる)時間</li>
	 * <li>blockadeID:閉塞のEntityID</li>
	 * <li>roadID:閉塞が属する道路EntityID</li>
	 * <li>repairCost:閉塞のリペアコスト</li>
	 * </ul>
	 * 閉塞情報は閉塞の重心座標を送信することもでき，座標情報がある場合は座標(x,y)を，座標情報が含まれていない場合は(-1,-1)をPair<
	 * Integer,Integer>として取得することができます．<br>
	 * 
	 * <h2>Constructor</h2> Create the blockade information.<br>
	 * Included data are follow.<br>
	 * <ul>
	 * <li>time:the time that the message is created.(int)</li>
	 * <li>blockadeID:EntityID of the blockade</li>
	 * <li>roadID:EntityID of road that the blockade exist</li>
	 * <li>repairCost:repair cost of the blockade</li>
	 * </ul>
	 * Additionally, This message can send the barycentric coodinate of the
	 * blockade, if coodinate information doesn't exist, we get coodinate
	 * information as (-1,-1).
	 * 
	 * @param time
	 *            現在のステップ <br>
	 *            step num
	 * @param blockadeID
	 *            閉塞のID<br>
	 *            EntityID of the blockade
	 * @param roadID
	 *            閉塞が属する道路のID <br>
	 *            EntityID of road that the blockade exists
	 * @param repairCost
	 *            開通コスト <br>
	 *            repair cost of the blockade
	 */
	public BlockadeInformation(int time, EntityID blockadeID, EntityID roadID,
			int repairCost) {
		super(BaseMessageType.BLOCKADE, time);
		// this.setData(new ValueData(DataType.BLOCKADE, id.getValue()));
		this.setData(new EntityIDData(DataType.BLOCKADE, blockadeID));
		// this.setData(new ValueData(DataType.ROAD, road.getValue()));
		this.setData(new EntityIDData(DataType.ROAD, roadID));
		this.setData(new ValueData(DataType.REPAIR_COST, repairCost));
		this.setCoorinate(new Pair<Integer, Integer>(-1, -1));
	}

	/**
	 * コンストラクタ<br>
	 * 閉塞情報を生成します．<br>
	 * 含まれる情報は以下のものです．
	 * <ul>
	 * <li>
	 * time:情報生成時に設定した(情報が作成されたと考えられる)時間</li>
	 * <li>
	 * blockadeID:閉塞のID</li>
	 * <li>
	 * roadID:閉塞が属する道路ID</li>
	 * <li>
	 * repairCost:閉塞のリペアコスト</li>
	 * <li>
	 * blockadeCor:閉塞の重心座標</li>
	 * </ul>
	 * 
	 * <h2>Constructor</h2> Create the blockade information.<br>
	 * Included data are follow.<br>
	 * <ul>
	 * <li>time:the time that the message is created.(int)</li>
	 * <li>blockadeID:EntityID of the blockade</li>
	 * <li>roadID:EntityID of road that the blockade exist</li>
	 * <li>repairCost:repair cost of the blockade</li>
	 * <li>blockadeCor:barycentric coordinate of the blockade.</li>
	 * </ul>
	 * 
	 * @param time
	 *            現在のステップ <br>
	 *            step num
	 * @param blockadeID
	 *            閉塞のID<br>
	 *            EntityID of the blockade
	 * @param roadID
	 *            閉塞が属する道路のID<br>
	 *            EntityiD of road that the blockade exists
	 * @param repairCost
	 *            開通コスト<br>
	 *            repair cost of the blockade
	 * @param blockadeCor
	 *            閉塞の重心座標<br>
	 *            barycentric coordinate of the blockade
	 */
	public BlockadeInformation(int time, EntityID blockadeID, EntityID roadID,
			int repairCost, Pair<Integer, Integer> blockadeCor) {
		// this(time, blockadeID, roadID, repairCost);
		super(BaseMessageType.BLOCKADE_WITH_COORDINATE, time);
		// this.setData(new ValueData(DataType.BLOCKADE, id.getValue()));
		this.setData(new EntityIDData(DataType.BLOCKADE, blockadeID));
		// this.setData(new ValueData(DataType.ROAD, road.getValue()));
		this.setData(new EntityIDData(DataType.ROAD, roadID));
		this.setData(new ValueData(DataType.REPAIR_COST, repairCost));
		super.setCoorinate(blockadeCor);
	}

	/**
	 * メッセージをコンバートするためにシステムが使用するコンストラクタ<br>
	 * The method that the library use to convert the message.
	 * 
	 * @param type
	 * @param bitList
	 * @param offset
	 * @param bitSizeMap
	 */
	public BlockadeInformation(BaseMessageType type, List<Integer> bitList,
			int offset, EnumMap<DataType, Integer> bitSizeMap) {
		super(type, bitList, offset, bitSizeMap);
	}

	/**
	 * 閉塞のIDを取得する．<br>
	 * Return EntityID of the blockade.
	 * 
	 * @return 閉塞のEntityID<br>
	 *         EntityID
	 */
	public EntityID getBlockadeID() {
		return super.getID(DataType.BLOCKADE, 0);
	}

	/**
	 * 閉塞が所属している道のIDを取得する．<br>
	 * Return EntityID of road that the blockade exist.
	 * 
	 * @return 道路のEntityID<br>
	 *         EntityID
	 */
	public EntityID getRoadID() {
		return super.getID(DataType.ROAD, 0);
	}

	/**
	 * 閉塞のRepair Costを取得する． <br>
	 * Return repair cost of the blockade.
	 * 
	 * @return 閉塞の開通コスト(int)<br>
	 *         repair cost
	 */
	public int getRepairCost() {
		return super.getRepairCost(0);
	}

	/**
	 * 閉塞の座標を取得する．<br>
	 * Return the coordinate of the blockade.
	 * 
	 * @return 閉塞の重心座標<br>
	 *         barycentric coordinate({@literal Pair<Integer,Integer>})
	 * 
	 */
	public Pair<Integer, Integer> getCoodinate() {
		return super.getCoodinate(0);
	}

	@Override
	public EntityID getEntityID() {
		return this.getBlockadeID();
	}
}
