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
 * 閉塞の情報を表すクラスです．
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
	 * <li>
	 * time:情報生成時に設定した(情報が作成されたと考えられる)時間</li>
	 * <li>
	 * blockadeID:閉塞のEntityID</li>
	 * <li>
	 * roadID:閉塞が属する道路EntityID</li>
	 * <li>
	 * repairCost:閉塞のリペアコスト</li>
	 * </ul>
	 * 閉塞情報は閉塞の重心座標を送信することもでき，座標情報がある場合は座標(x,y)を，座標情報が含まれていない場合は(-1,-1)をPair<
	 * Integer,Integer>として取得することができます．
	 * 
	 * @param time
	 *            現在のステップ
	 * @param blockadeID
	 *            閉塞のID
	 * @param roadID
	 *            閉塞が属する道路のID
	 * @param repairCost
	 *            開通コスト
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
	 * 閉塞の重心座標</li>
	 * </ul>
	 * 
	 * @param time
	 *            現在のステップ
	 * @param blockadeID
	 *            閉塞のID
	 * @param roadID
	 *            閉塞が属する道路のID
	 * @param repairCost
	 *            開通コスト
	 * @param blockadeCor
	 *            閉塞の重心座標
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
	 * メッセージをコンバートするためにシステムが使用するコンストラクタ
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
	 * 閉塞のIDを取得する．
	 * 
	 * @return 閉塞のEntityID
	 */
	public EntityID getBlockadeID() {
		return super.getID(DataType.BLOCKADE, 0);
	}

	/**
	 * 閉塞が所属している道のIDを取得する．
	 * 
	 * @return 道路のEntityID
	 */
	public EntityID getRoadID() {
		return super.getID(DataType.ROAD, 0);
	}

	/**
	 * 閉塞のRepair Costを取得する．
	 * 
	 * @return 閉塞の開通コスト(int)
	 */
	public int getRepairCost() {
		return super.getRepairCost(0);
	}

	/**
	 * 閉塞の座標を取得する．
	 * 
	 * @return 閉塞の重心座標
	 */
	public Pair<Integer, Integer> getCoodinate() {
		return super.getCoodinate(0);
	}
}
