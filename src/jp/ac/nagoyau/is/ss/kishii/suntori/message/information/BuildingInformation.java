package jp.ac.nagoyau.is.ss.kishii.suntori.message.information;

import java.util.EnumMap;
import java.util.List;

import jp.ac.nagoyau.is.ss.kishii.suntori.message.BaseMessageType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.DataType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.EntityIDData;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.ValueData;
import rescuecore2.worldmodel.EntityID;

/**
 * 建物の情報を表すクラスです．
 * 
 * @author takefumi
 * 
 */
public class BuildingInformation extends WorldInformation {

	/**
	 * コンストラクタ<br>
	 * 建物の情報を生成します．<br>
	 * 含まれる情報は以下のものです．
	 * <ul>
	 * <li>time:情報生成時に設定した(情報が作成されたと考えられる)時間</li>
	 * <li>buildingID:建物のEntityID</li>
	 * <li>fieryness:建物の燃焼度</li>
	 * <li>brokenness:建物の倒壊度</li>
	 * </ul>
	 */
	public BuildingInformation(int time, EntityID buildingId, int fieryness,
			int brokenness) {
		super(BaseMessageType.BUILDING, time);
		// this.setData(new ValueData(DataType.BUILDING, id.getValue()));
		this.setData(new EntityIDData(DataType.BUILDING, buildingId));
		this.setData(new ValueData(DataType.FIERYNESS, fieryness));
		this.setData(new ValueData(DataType.BROKENNESS, brokenness));
	}

	/**
	 * コンバート時システムが使用するコンストラクタ
	 * 
	 * @param bitList
	 * @param offset
	 * @param bitSizeMap
	 */
	public BuildingInformation(List<Integer> bitList, int offset,
			EnumMap<DataType, Integer> bitSizeMap) {
		super(BaseMessageType.BUILDING, bitList, offset, bitSizeMap);
	}

	/**
	 * 建物のEntityIDを取得します．
	 * 
	 * @return 建物のID
	 */
	public EntityID getBuildingID() {
		return super.getID(DataType.BUILDING, 0);
	}

	/**
	 * 建物の倒壊度を取得します．
	 * 
	 * @return 倒壊度
	 */
	public int getBrokenness() {
		return super.getBrokenness(0);
	}

	/**
	 * 燃焼度を取得します．
	 * 
	 * @return 燃焼度
	 */
	public int getFieryness() {
		return super.getFieryness(0);
	}
}
