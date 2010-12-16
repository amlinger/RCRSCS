package jp.ac.nagoyau.is.ss.kishii.suntori.message.information;

import java.util.EnumMap;
import java.util.List;

import jp.ac.nagoyau.is.ss.kishii.suntori.message.BaseMessageType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.DataType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.EntityIDData;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.ValueData;
import rescuecore2.worldmodel.EntityID;

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
	 * 
	 * @param time
	 *            ステップ数
	 * @param pfID
	 *            啓開隊のID
	 * @param hp
	 *            体力
	 * @param damage
	 *            ダメージ
	 * @param buriedness
	 *            埋没度
	 * @param areaID
	 *            現在いるエリア
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
	 * コンバート時にシステムが使用するコンストラクタ
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
	 * PFのEntityIDを取得します．
	 * 
	 * @return 消防隊のEntityID
	 */
	public EntityID getPoliceForceID() {
		return super.getID(DataType.POLICE_FORCE, 0);
	}

	/**
	 * PFの体力を取得します．
	 * 
	 * @return 体力
	 */
	public int getHP() {
		return super.getHP(0);
	}

	/**
	 * ダメージを取得します．
	 * 
	 * @return ダメージ
	 */
	public int getDatage() {
		return super.getDamage(0);
	}

	/**
	 * PFの埋没度を取得します．
	 * 
	 * @return 埋没度
	 */
	public int getBuriedness() {
		return super.getBuriedness(0);
	}

	/**
	 * PFが現在いるエリアのEntityID
	 * 
	 * @return エリアのEntityID
	 */
	public EntityID getPositionID() {
		return super.getID(DataType.AREA, 0);
	}

}
