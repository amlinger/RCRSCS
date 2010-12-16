package jp.ac.nagoyau.is.ss.kishii.suntori.message.information;

import java.util.EnumMap;
import java.util.List;

import jp.ac.nagoyau.is.ss.kishii.suntori.message.BaseMessageType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.DataType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.EntityIDData;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.ValueData;
import rescuecore2.worldmodel.EntityID;

/**
 * 消防隊の情報を表すクラスです．
 * 
 * @author takefumi
 * 
 */
public class FireBrigadeInformation extends WorldInformation {

	/**
	 * コンストラクタ<br>
	 * 消防隊情報を生成します．<br>
	 * 含まれる情報は以下のものです．
	 * <ul>
	 * <li>time:情報生成時に設定した(情報が作成されたと考えられる)時間</li>
	 * <li>fbID:消防隊のEntityID</li>
	 * <li>hp:消防隊の体力</li>
	 * <li>damage:ダメージ</li>
	 * <li>buriedness:消防隊の埋没度</li>
	 * <li>water:タンクの残存水量</li>
	 * <li>areaID:消防隊が現在存在するエリアのEntityID</li>
	 * </ul>
	 * 
	 * @param time
	 *            ステップ数
	 * @param fbID
	 *            EntityID
	 * @param hp
	 *            体力
	 * @param damage
	 *            ダメージ
	 * @param buriedness
	 *            埋没度
	 * @param water
	 *            残存水量
	 * @param areaID
	 *            エリアのEntityID
	 */
	public FireBrigadeInformation(int time, EntityID fbID, int hp, int damage,
			int buriedness, int water, EntityID areaID) {
		super(BaseMessageType.FIRE_BRIGADE, time);
		this.setData(new EntityIDData(DataType.FIRE_BRIGADE, fbID));
		this.setData(new ValueData(DataType.HP, hp));
		this.setData(new ValueData(DataType.DAMAGE, damage));
		this.setData(new ValueData(DataType.BURIEDNESS, buriedness));
		this.setData(new ValueData(DataType.WATER, water));
		this.setData(new EntityIDData(DataType.AREA, areaID));
	}

	/**
	 * コンバート時にシステムが使用するコンストラクタ
	 * 
	 * @param bitList
	 * @param offset
	 * @param bitSizeMap
	 */
	public FireBrigadeInformation(List<Integer> bitList, int offset,
			EnumMap<DataType, Integer> bitSizeMap) {
		super(BaseMessageType.FIRE_BRIGADE, bitList, offset, bitSizeMap);
	}

	/**
	 * 消防隊のEntityIDを取得します．
	 * 
	 * @return 消防隊のID
	 */
	public EntityID getFireBrigadeID() {
		return super.getID(DataType.FIRE_BRIGADE, 0);
	}

	/**
	 * 消防隊の体力を取得します．
	 * 
	 * @return 体力
	 */
	public int getHP() {
		return super.getHP(0);
	}

	/**
	 * 消防隊のダメージを取得します．
	 * 
	 * @return ダメージ
	 */
	public int getDatage() {
		return super.getDamage(0);
	}

	/**
	 * 消防隊の埋没度を取得します．
	 * 
	 * @return 埋没度
	 */
	public int getBuriedness() {
		return super.getBuriedness(0);
	}

	/**
	 * 消防隊の保有している水量を取得します．
	 * 
	 * @return 残存水量
	 */
	public int getWater() {
		return super.getWater(0);
	}

	/**
	 * 消防隊が現在存在しているエリアのEntityIDを取得します．
	 * 
	 * @return エリアのEntityID
	 */
	public EntityID getPositionID() {
		return super.getID(DataType.AREA, 0);
	}
}
