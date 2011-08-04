package jp.ac.nagoyau.is.ss.kishii.suntori.message.information;

import java.util.EnumMap;
import java.util.List;

import jp.ac.nagoyau.is.ss.kishii.suntori.message.BaseMessageType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.DataType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.EntityIDData;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.ValueData;
import rescuecore2.worldmodel.EntityID;

/**
 * 消防隊の情報を表すクラスです．<br>
 * This class show the Fire Brigade information.
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
	 * <h2>Constructor</h2> Create the information of fire brigade<br>
	 * Included data are follow.
	 * <ul>
	 * <li>time:the time that the message is created.(int)</li>
	 * <li>fbID:EntityID of the fire brigade</li>
	 * <li>hp:hp of the fb</li>
	 * <li>damage:damage of the fb</li>
	 * <li>buriedness:buriedness of the fb</li>
	 * <li>water:amount of left water</li>
	 * <li>areaID:EntityID of area that the fb is standing</li>
	 * </ul>
	 * 
	 * @param time
	 *            ステップ数<br>
	 *            step num
	 * @param fbID
	 *            EntityID
	 * @param hp
	 *            体力<br>
	 *            hp of the fb
	 * @param damage
	 *            ダメージ<br>
	 *            damage of the fb
	 * @param buriedness
	 *            埋没度<br>
	 *            buriedness of the fb
	 * @param water
	 *            残存水量<br>
	 *            amount of left water
	 * @param areaID
	 *            エリアのEntityID<br>
	 *            EntityID of area
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
	 * コンバート時にシステムが使用するコンストラクタ<br>
	 * The method that the library use to convert the message.
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
	 * 消防隊のEntityIDを取得します．<br>
	 * Return EntityID of the fb.
	 * 
	 * @return 消防隊のID<br>
	 *         EntityID
	 */
	public EntityID getFireBrigadeID() {
		return super.getID(DataType.FIRE_BRIGADE, 0);
	}

	/**
	 * 消防隊の体力を取得します．<br>
	 * Return hp of the fb.
	 * 
	 * @return 体力<br>
	 *         hp of the fb
	 */
	public int getHP() {
		return super.getHP(0);
	}

	/**
	 * 消防隊のダメージを取得します．<br>
	 * Return damage of the fb.
	 * 
	 * @return ダメージ<br>
	 *         damage
	 */
	public int getDamage() {
		return super.getDamage(0);
	}

	/**
	 * 消防隊の埋没度を取得します．<br>
	 * Return buriedness of the fb.
	 * 
	 * @return 埋没度<br>
	 *         buriedness
	 */
	public int getBuriedness() {
		return super.getBuriedness(0);
	}

	/**
	 * 消防隊の保有している水量を取得します．<br>
	 * Return amount of left water.
	 * 
	 * @return 残存水量 <br>
	 *         quantity.
	 */
	public int getWater() {
		return super.getWater(0);
	}

	/**
	 * 消防隊が現在存在しているエリアのEntityIDを取得します．<br>
	 * Return EntityID of area that the fb is standing.
	 * 
	 * @return エリアのEntityID<br>
	 *         EntityID of area
	 */
	public EntityID getPositionID() {
		return super.getID(DataType.AREA, 0);
	}

	@Override
	public EntityID getEntityID() {
		return this.getFireBrigadeID();
	}
}
