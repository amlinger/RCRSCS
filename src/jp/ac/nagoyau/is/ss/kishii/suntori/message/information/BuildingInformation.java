package jp.ac.nagoyau.is.ss.kishii.suntori.message.information;

import java.util.EnumMap;
import java.util.List;

import jp.ac.nagoyau.is.ss.kishii.suntori.message.BaseMessageType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.DataType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.EntityIDData;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.ValueData;
import rescuecore2.worldmodel.EntityID;

/**
 * 建物の情報を表すクラスです．<br>
 * The BuildingInformation show the building information.
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
	 * <h2>Constructor</h2> Create the inforamtion of building.<br>
	 * Included data are follow.<br>
	 * <ul>
	 * <li>time:the time that the message is created.(int)</li>
	 * <li>buildingID:EntityID of the building</li>
	 * <li>fieryness:fieryness of the building</li>
	 * <li>brokenness:brokenness of the building</li>
	 * </ul>
	 * 
	 * @param time
	 *            step num
	 * @param buildingId
	 *            EntityiD of the building
	 * @param fieryness
	 *            fieryness of the building
	 * @param brokenness
	 *            blockenness of the building
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
	 * コンバート時システムが使用するコンストラクタ <br>
	 * The method that the library use to convert the message.
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
	 * 建物のEntityIDを取得します．<br>
	 * Return EntityID of the building.
	 * 
	 * @return 建物のID<br>
	 *         EntityID
	 */
	public EntityID getBuildingID() {
		return super.getID(DataType.BUILDING, 0);
	}

	/**
	 * 建物の倒壊度を取得します．<br>
	 * Return brokenness of the building.
	 * 
	 * @return 倒壊度<br>
	 *         brokenness
	 */
	public int getBrokenness() {
		return super.getBrokenness(0);
	}

	/**
	 * 燃焼度を取得します．<br>
	 * Return fieryness of the building.
	 * 
	 * @return 燃焼度<br>
	 *         fieryness
	 */
	public int getFieryness() {
		return super.getFieryness(0);
	}

	@Override
	public EntityID getEntityID() {
		return this.getBuildingID();
	}
}
