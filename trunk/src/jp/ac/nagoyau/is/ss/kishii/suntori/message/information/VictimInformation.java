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
 * 被災者の情報を表すクラスです．
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
	 * 
	 * @param time
	 *            ステップ数
	 * @param vicID
	 *            被災者のEntityID
	 * @param area
	 *            被災者の存在するエリアのEntityID
	 * @param hp
	 *            被災者の体力
	 * @param buriedness
	 *            被災者の埋没度
	 * @param damage
	 *            被災者のダメージ
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
	 * 
	 * @param time
	 *            ステップ数
	 * @param vicID
	 *            被災者のEntityID
	 * @param area
	 *            被災者の存在するエリアのEntityID
	 * @param hp
	 *            被災者の体力
	 * @param buriedness
	 *            被災者の埋没度
	 * @param damage
	 *            被災者のダメージ
	 * @param cor
	 *            被災者の位置座標
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
	 * コンバート時にシステムが使用するコンストラクタ
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
	 * 被災者のEntityIDを取得します．
	 * 
	 * @return 被災者のEntityID
	 */
	public EntityID getVictimID() {
		return super.getID(DataType.HUMAN, 0);
	}

	/**
	 * 被災者の体力を取得します．
	 * 
	 * @return 被災者の体力
	 */
	public int getHP() {
		return super.getHP(0);
	}

	/**
	 * 被災者の埋没度を取得します．
	 * 
	 * @return 被災者の埋没度
	 */
	public int getBuriedness() {
		return super.getBuriedness(0);
	}

	/**
	 * 被災者のダメージを取得します．
	 * 
	 * @return 被災者のダメージ
	 */
	public int getDamage() {
		return super.getDamage(0);
	}

	/**
	 * 被災者のいるエリアのEntityIDを取得します．
	 * 
	 * @return 被災者のいるエリアのEntityID
	 */
	public EntityID getAreaID() {
		return super.getID(DataType.AREA, 0);
	}

	/**
	 * 被災者の座標を取得します．<br>
	 * 設定されていない場合は，(-1,-1)が返されます．
	 * 
	 * @return 被災者の位置座標
	 */
	public Pair<Integer, Integer> getCoodinate() {
		return super.getCoodinate(0);
	}
}
