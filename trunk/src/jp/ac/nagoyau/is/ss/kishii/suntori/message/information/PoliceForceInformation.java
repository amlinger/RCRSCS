package jp.ac.nagoyau.is.ss.kishii.suntori.message.information;

import java.util.EnumMap;
import java.util.List;

import jp.ac.nagoyau.is.ss.kishii.suntori.message.BaseMessageType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.DataType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.EntityIDData;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.ValueData;
import rescuecore2.worldmodel.EntityID;

/**
 * The class represent the police force information
 * 
 * @author takefumi
 * 
 */
public class PoliceForceInformation extends WorldInformation {

	/**
	 * コンストラクタ<br>
	 * 啓開隊情報を生成します．<br>
	 * 含まれる情報は以下のものです．
	 * <ul>
	 * <li>time:情報生成時に設定した(情報が作成されたと考えられる)時間</li>
	 * <li>pfID:啓開隊のEntityID</li>
	 * <li>hp:啓開隊の体力</li>
	 * <li>damage:ダメージ</li>
	 * <li>buriedness:啓開隊の埋没度</li>
	 * <li>areaID:啓開隊が現在存在するエリアのEntityID</li>
	 * </ul>
	 * <h2>Constructor</h2> Create the information of the police force.<br>
	 * Included data are follow.
	 * <ul>
	 * <li>time:the time that the message is created.(int)</li>
	 * <li>pfID:EntityID of the pf</li>
	 * <li>hp:hp of the pf</li>
	 * <li>damage:damage of the pf</li>
	 * <li>buriedness:buriedness of the pf</li>
	 * <li>areaID:EntityID of area that the pf is standing.</li>
	 * </ul>
	 * 
	 * @param time
	 *            ステップ数<br>
	 *            step num
	 * @param pfID
	 *            啓開隊のID<br>
	 *            EntityID of the pf
	 * @param hp
	 *            体力<br>
	 *            hp of the pf
	 * @param damage
	 *            ダメージ<br>
	 *            damage of the pf
	 * @param buriedness
	 *            埋没度<br>
	 *            buriedness of the pf
	 * @param areaID
	 *            現在いるエリア<br>
	 *            EntityID of area
	 */
	public PoliceForceInformation(int time, EntityID fbID, int hp, int damage,
			int buriedness, EntityID areaID) {
		super(BaseMessageType.POLICE_FORCE, time);
		this.setData(new EntityIDData(DataType.POLICE_FORCE, fbID));
		this.setData(new ValueData(DataType.HP, hp));
		this.setData(new ValueData(DataType.DAMAGE, damage));
		this.setData(new ValueData(DataType.BURIEDNESS, buriedness));
		this.setData(new EntityIDData(DataType.AREA, areaID));
	}

	/**
	 * コンバート時にシステムが使用するコンストラクタ<br>
	 * The method that the library use to convert the message.
	 * 
	 * @param bitList
	 * @param offset
	 * @param bitSizeMap
	 */
	public PoliceForceInformation(List<Integer> bitList, int offset,
			EnumMap<DataType, Integer> bitSizeMap) {
		super(BaseMessageType.POLICE_FORCE, bitList, offset, bitSizeMap);
	}

	/**
	 * PFのEntityIDを取得します．<br>
	 * Return EntityID of the pf.
	 * 
	 * @return 啓開隊のEntityID<br>
	 *         EntityID
	 */
	public EntityID getPoliceForceID() {
		return super.getID(DataType.POLICE_FORCE, 0);
	}

	/**
	 * PFの体力を取得します．<br>
	 * Return hp of the pf.
	 * 
	 * @rketurn 体力<br>
	 *          hp
	 */
	public int getHP() {
		return super.getHP(0);
	}

	/**
	 * ダメージを取得します．<br>
	 * Return damege of the pf.
	 * 
	 * @return ダメージ<br>
	 *         damage
	 */
	public int getDamage() {
		return super.getDamage(0);
	}

	/**
	 * PFの埋没度を取得します．<br>
	 * Return buryedness of the pf.
	 * 
	 * @return 埋没度<br>
	 *         buryedness
	 */
	public int getBuriedness() {
		return super.getBuriedness(0);
	}

	/**
	 * PFが現在いるエリアのEntityID<br>
	 * Return EntityID of area that the pf is standing.
	 * 
	 * @return エリアのEntityID<br>
	 *         EntityID
	 */
	public EntityID getPositionID() {
		return super.getID(DataType.AREA, 0);
	}

	@Override
	public EntityID getEntityID() {
		return this.getPoliceForceID();
	}

}
