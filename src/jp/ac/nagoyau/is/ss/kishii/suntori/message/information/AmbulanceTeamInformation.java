package jp.ac.nagoyau.is.ss.kishii.suntori.message.information;

import java.util.EnumMap;
import java.util.List;

import jp.ac.nagoyau.is.ss.kishii.suntori.message.BaseMessageType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.DataType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.EntityIDData;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.ValueData;
import rescuecore2.worldmodel.EntityID;

/**
 * 救急隊の情報を表すクラスです． <br>
 * This class represent the information of Ambulance Team.
 * 
 * @author takefumi
 * 
 */
public class AmbulanceTeamInformation extends WorldInformation {
	/**
	 * コンストラクタ<br>
	 * 救急隊情報を生成します．<br>
	 * 含まれる情報は以下のものです．
	 * <ul>
	 * <li>time:情報生成時に設定した(情報が作成されたと考えられる)時間</li>
	 * <li>atID:救急隊のEntityID</li>
	 * <li>hp:救急隊の体力</li>
	 * <li>damage:ダメージ</li>
	 * <li>buriedness:救急隊の埋没度</li>
	 * <li>areaID:救急隊が現在存在するエリアのEntityID</li>
	 * </ul>
	 * <br>
	 * <h2>Constructor</h2> Create the Ambulance Team Information.<br>
	 * Included data are follow.<br>
	 * <ul>
	 * <li>time:the time that the message is created.(int)</li>
	 * <li>atID:EntityID of Ambulance Team Agent.</li>
	 * <li>hp:HP of the at.</li>
	 * <li>damage: Damage of the at.</li>
	 * <li>buriedness:buriedness of at.</li>
	 * <li>areaID:EntityID of area that the at is standing.</li>
	 * </ul>
	 * 
	 * @param time
	 *            ステップ数<br>
	 *            step num.
	 * @param atID
	 *            救急隊のID<br>
	 *            EntityID of at.
	 * @param hp
	 *            体力 <br>
	 *            hp of the at.
	 * @param damage
	 *            ダメージ<br>
	 *            damage of the at.
	 * @param buriedness
	 *            埋没度 <br>
	 *            buriedness of the at.
	 * @param areaID
	 *            現在いるエリア <br>
	 *            EntityID of area that the at is standing.
	 */
	public AmbulanceTeamInformation(int time, EntityID atID, int hp,
			int damage, int buriedness, EntityID areaID) {
		super(BaseMessageType.AMBULANCE_TEAM, time);
		this.setData(new EntityIDData(DataType.AMBULANCE_TEAM, atID));
		this.setData(new ValueData(DataType.HP, hp));
		this.setData(new ValueData(DataType.DAMAGE, damage));
		this.setData(new ValueData(DataType.BURIEDNESS, buriedness));
		this.setData(new EntityIDData(DataType.AREA, areaID));
	}

	/**
	 * コンストラクタ<br>
	 * 与えられたビット列からこのクラスを生成します．<br>
	 * このコンストラクタはシステムが使用するためのメソッドです．<br>
	 * <h2>Constructor</h2> Create the instance of this class from bit sequence.<br>
	 * This method is defined for this library.
	 * 
	 * @param bitList
	 * @param offset
	 * @param bitSizeMap
	 */
	public AmbulanceTeamInformation(List<Integer> bitList, int offset,
			EnumMap<DataType, Integer> bitSizeMap) {
		super(BaseMessageType.AMBULANCE_TEAM, bitList, offset, bitSizeMap);
	}

	/**
	 * ATのIDを取得します．<br>
	 * Return EntityID of at.
	 * 
	 * @return ATのID<br>
	 *         EntityID of at.
	 */
	public EntityID getAmbulanceTeamID() {
		return super.getID(DataType.AMBULANCE_TEAM, 0);
	}

	/**
	 * ATの体力を取得します．<br>
	 * Return hp of the at.
	 * 
	 * @return 体力<br>
	 *         hp
	 */
	public int getHP() {
		return super.getHP(0);
	}

	/**
	 * ATのダメージを取得します．<br>
	 * Return damage of the at.
	 * 
	 * @return ダメージ <br>
	 *         damage
	 */
	public int getDamage() {
		return super.getDamage(0);
	}

	/**
	 * ATの埋没度を取得します．<br>
	 * Return buriedness of the at.
	 * 
	 * @return 埋没度<br>
	 *         buriedness
	 */
	public int getBuriedness() {
		return super.getBuriedness(0);
	}

	/**
	 * ATが現在いるエリアのEntityID<br>
	 * Return EntityID of area that the at is standing.
	 * 
	 * @return エリアのEntityID<br>
	 *         EntityID of area
	 */
	public EntityID getPositionID() {
		return super.getID(DataType.AREA, 0);
	}

	@Override
	public EntityID getEntityID() {
		return this.getAmbulanceTeamID();
	}
}
