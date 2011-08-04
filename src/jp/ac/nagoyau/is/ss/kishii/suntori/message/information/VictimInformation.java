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
 * 被災者の情報を表すクラスです．<br>
 * The class show the information of victim.
 * 
 * @author takefumi
 * 
 */
public class VictimInformation extends WorldInformation {
	/**
	 * コンストラクタ<br>
	 * 被災者情報を生成します．<br>
	 * 含まれる情報は以下のものです．
	 * <ul>
	 * <li>time:情報生成時に設定した(情報が作成されたと考えられる)時間</li>
	 * <li>vicID:被災者のEntityID</li>
	 * <li>area:被災者が存在しているエリアのEntityID</li>
	 * <li>hp:被災者の体力</li>
	 * <li>buriedness:被災者の埋没度</li>
	 * <li>damage:被災者のダメージ</li>
	 * </ul>
	 * 被災者情報は被災者の位置座標を送信することもでき，座標情報がある場合は座標(x,y)を，座標情報が含まれていない場合は(-1,-1)をPair<
	 * Integer,Integer>として取得することができます．
	 * <p>
	 * また，vicIDにはCivilianのEntityIDだけではなく，救助隊(platoon)のEntityIDを指定することもできます．
	 * </p>
	 * <h2>Constructor</h2> Create the victim information. Included data are
	 * follow.
	 * <ul>
	 * <li>time:the time that the message is created.(int)</li>
	 * <li>vicID:EntityID of the victim</li>
	 * <li>area:EntityID of area that the victim exist</li>
	 * <li>hp:hp of the victim</li>
	 * <li>buriedness:buriedness of the victim</li>
	 * <li>damage:damage of the victim</li>
	 * </ul>
	 * Additionally, This message can send the location data of the victim(
	 * {@literal Pair<Integer,Integer>} ), if location information doesn't
	 * exist, we get this as (-1,-1).<br>
	 * And not only EntityID of the civilian, but also EntityID of the platoon
	 * is assignable as vicID.
	 * 
	 * @param time
	 *            ステップ数<br>
	 *            step num
	 * @param vicID
	 *            被災者のEntityID <br>
	 *            EntityID of the victim
	 * @param area
	 *            被災者の存在するエリアのEntityID <br>
	 *            EntityID of area that the victim exist
	 * @param hp
	 *            被災者の体力<br>
	 *            hp of the victim
	 * @param buriedness
	 *            被災者の埋没度<br>
	 *            buriedness of the victim
	 * @param damage
	 *            被災者のダメージ<br>
	 *            damage of the victim
	 */
	public VictimInformation(int time, EntityID vicID, EntityID area, int hp,
			int buriedness, int damage) {
		super(BaseMessageType.VICTIM, time);
		// this.setData(new ValueData(DataType.HUMAN, vicID.getValue()));
		this.setData(new EntityIDData(DataType.HUMAN, vicID));
		// this.setData(new ValueData(DataType.AREA, area.getValue()));
		this.setData(new EntityIDData(DataType.AREA, area));
		this.setData(new ValueData(DataType.HP, hp));
		this.setData(new ValueData(DataType.BURIEDNESS, buriedness));
		this.setData(new ValueData(DataType.DAMAGE, damage));
	}

	/**
	 * コンストラクタ<br>
	 * 被災者情報を生成します．<br>
	 * 含まれる情報は以下のものです．
	 * <ul>
	 * <li>time:情報生成時に設定した(情報が作成されたと考えられる)時間</li>
	 * <li>vicID:被災者のEntityID</li>
	 * <li>area:被災者が存在しているエリアのEntityID</li>
	 * <li>hp:被災者の体力</li>
	 * <li>buriedness:被災者の埋没度</li>
	 * <li>damage:被災者のダメージ</li>
	 * <li>cor:被災者の位置座標</li>
	 * </ul>
	 * <p>
	 * また，vicIDにはCivilianのEntityIDだけではなく，救助隊(platoon)のEntityIDを指定することもできます．
	 * </p>
	 * <h2>Constructor</h2> Create the victim information. Included data are
	 * follow.
	 * <ul>
	 * <li>time:the time that the message is created.(int)</li>
	 * <li>vicID:EntityID of the victim</li>
	 * <li>area:EntityID of area that the victim exist</li>
	 * <li>hp:hp of the victim</li>
	 * <li>buriedness:buriedness of the victim</li>
	 * <li>damage:damage of the victim</li>
	 * <li>cor:location coordinate of the victim({@literal
	 * Pair<Integer,Integer>})
	 * </ul>
	 * Additionally, This message can send the location data of the victim(
	 * {@literal Pair<Integer,Integer>} ), if location information doesn't
	 * exist, we get this as (-1,-1).<br>
	 * And not only EntityID of the civilian, but also EntityID of the platoon
	 * is assignable as vicID.
	 * 
	 * @param time
	 *            ステップ数<br>
	 *            step num
	 * @param vicID
	 *            被災者のEntityID <br>
	 *            EntityID of the victim
	 * @param area
	 *            被災者の存在するエリアのEntityID <br>
	 *            EntityID of area that the victim exist
	 * @param hp
	 *            被災者の体力<br>
	 *            hp of the victim
	 * @param buriedness
	 *            被災者の埋没度<br>
	 *            buriedness of the victim
	 * @param damage
	 *            被災者のダメージ<br>
	 *            damage of the victim
	 * @param cor
	 *            被災者の位置座標<br>
	 *            coordinate of the victim
	 */
	public VictimInformation(int time, EntityID vicID, EntityID area, int hp,
			int buriedness, int damage, Pair<Integer, Integer> cor) {
		// this(time, vicID, area, hp, buriedness, damage);
		super(BaseMessageType.VICTIM_WITH_COORDINATE, time);
		// this.setData(new ValueData(DataType.HUMAN, vicID.getValue()));
		this.setData(new EntityIDData(DataType.HUMAN, vicID));
		// this.setData(new ValueData(DataType.AREA, area.getValue()));
		this.setData(new EntityIDData(DataType.HUMAN, area));
		this.setData(new ValueData(DataType.HP, hp));
		this.setData(new ValueData(DataType.BURIEDNESS, buriedness));
		this.setData(new ValueData(DataType.DAMAGE, damage));
		super.setCoorinate(cor);
	}

	/**
	 * コンバート時にシステムが使用するコンストラクタ<br>
	 * The method that the library use to convert the message.
	 * 
	 * @param type
	 * @param bitList
	 * @param offset
	 * @param bitSizeMap
	 */
	public VictimInformation(BaseMessageType type, List<Integer> bitList,
			int offset, EnumMap<DataType, Integer> bitSizeMap) {
		super(type, bitList, offset, bitSizeMap);
	}

	/**
	 * 被災者のEntityIDを取得します．<br>
	 * Return EntityID of the victim.
	 * 
	 * @return 被災者のEntityID<br>
	 *         EntityID
	 */
	public EntityID getVictimID() {
		return super.getID(DataType.HUMAN, 0);
	}

	/**
	 * 被災者の体力を取得します．<br>
	 * Return hp of the victim
	 * 
	 * @return 被災者の体力<br>
	 *         hp of the victim
	 */
	public int getHP() {
		return super.getHP(0);
	}

	/**
	 * 被災者の埋没度を取得します．<br>
	 * Return buriedness of the victim
	 * 
	 * @return 被災者の埋没度<br>
	 *         buriedness
	 */
	public int getBuriedness() {
		return super.getBuriedness(0);
	}

	/**
	 * 被災者のダメージを取得します．<br>
	 * Return damage of the victim
	 * 
	 * @return 被災者のダメージ<br>
	 *         damage of the victim
	 */
	public int getDamage() {
		return super.getDamage(0);
	}

	/**
	 * 被災者のいるエリアのEntityIDを取得します． <br>
	 * Return EntityID of area that the victim exist
	 * 
	 * @return 被災者のいるエリアのEntityID<br>
	 *         EntityID of area
	 */
	public EntityID getAreaID() {
		return super.getID(DataType.AREA, 0);
	}

	/**
	 * 被災者の座標を取得します．<br>
	 * 設定されていない場合は，(-1,-1)が返されます．<br>
	 * Return coordinate of the victim({@literal Pair<Integer,Integer>})<br>
	 * If not be setted, returned (-1,-1).
	 * 
	 * @return 被災者の位置座標<br>
	 *         coordinate of thSe victim
	 */
	public Pair<Integer, Integer> getCoodinate() {
		return super.getCoodinate(0);
	}

	@Override
	public EntityID getEntityID() {
		return this.getVictimID();
	}
}
