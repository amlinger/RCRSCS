package jp.ac.nagoyau.is.ss.kishii.suntori.message.information;

import java.util.EnumMap;
import java.util.List;

import jp.ac.nagoyau.is.ss.kishii.suntori.message.BaseMessageType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.DataType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.EntityIDData;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.ValueData;
import rescuecore2.worldmodel.EntityID;

/**
 * 救急隊の情報を表すクラスです．
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
	 * 
	 * @param time
	 *            ステップ数
	 * @param atID
	 *            救急隊のID
	 * @param hp
	 *            体力
	 * @param damage
	 *            ダメージ
	 * @param buriedness
	 *            埋没度
	 * @param areaID
	 *            現在いるエリア
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
	 * このコンストラクタはシステムが使用するためのメソッドです．
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
	 * ATのIDを取得します．
	 * 
	 * @return ATのID
	 */
	public EntityID getAmbulanceTeamID() {
		return super.getID(DataType.AMBULANCE_TEAM, 0);
	}

	/**
	 * ATの体力を取得します．
	 * 
	 * @return 体力
	 */
	public int getHP() {
		return super.getHP(0);
	}

	/**
	 * ATのダメージを取得します．
	 * 
	 * @return ダメージ
	 */
	public int getDatage() {
		return super.getDamage(0);
	}

	/**
	 * ATの埋没度を取得します．
	 * 
	 * @return 埋没度
	 */
	public int getBuriedness() {
		return super.getBuriedness(0);
	}

	/**
	 * ATが現在いるエリアのEntityID
	 * 
	 * @return エリアのEntityID
	 */
	public EntityID getPositionID() {
		return super.getID(DataType.AREA, 0);
	}
}
