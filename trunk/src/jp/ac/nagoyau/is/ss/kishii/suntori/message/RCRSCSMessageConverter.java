package jp.ac.nagoyau.is.ss.kishii.suntori.message;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;

import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.DataType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.EntityIDData;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.EntityIDListData;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.RCRSCSData;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.ValueData;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.information.AmbulanceTeamInformation;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.information.BlockadeInformation;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.information.BuildingInformation;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.information.FireBrigadeInformation;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.information.PoliceForceInformation;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.information.PositionInformation;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.information.TransferInformation;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.information.VictimInformation;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.task.MoveTaskMessage;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.task.MoveWithStagingPostTaskMessage;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.task.RestAtRefugeTaskMessage;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.task.RestTaskMessage;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.task.at.RescueAreaTaskMessage;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.task.fb.ExtinguishAreaTaskMessage;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.task.pf.ClearRouteTaskMessage;
import rescuecore2.config.Config;
import rescuecore2.standard.entities.StandardEntity;
import rescuecore2.standard.entities.StandardEntityURN;
import rescuecore2.standard.entities.StandardWorldModel;
import rescuecore2.standard.entities.StandardEntityConstants.Fieryness;
import rescuecore2.worldmodel.EntityID;

/**
 * メッセージ変換クラスです．
 * 
 * @author takefumi
 * 
 */
public class RCRSCSMessageConverter {
	private List<EntityID> buildingList;
	private List<EntityID> roadList;
	private List<EntityID> refugeList;
	private List<EntityID> areaList;

	private List<EntityID> policeForceList;
	private List<EntityID> ambulanceTeamList;
	private List<EntityID> fireBrigadeList;
	private List<EntityID> platoonAgentList;

	private final int messageKind;
	private EnumMap<DataType, Integer> dataBitSizeMap;
	private EnumMap<BaseMessageType, Integer> messageMininumSizeMap;

	public final int EXTINGUISHABLE_POWER;
	public final int EXTINGUISHABLE_DISTANCE;
	public final int TANK_MAX;

	public final int HP_PERCEPTION_LOS;
	public final int DAMAGE_PERCEPTION_LOS;
	public final int RANGE_OF_VIEW;

	private static final boolean debug = false;

	/**
	 * コンストラクタ<br>
	 * ユーザが定義した各Entityのリストを使用してコンバートするときはこちらを使用します．<br>
	 * <p>
	 * ただ，こちらを使ったとき，必要な情報が足りない場合，コンバート時にExceptionを吐く可能性があります．
	 * </p>
	 * 
	 * @param config
	 *            rescuecore2.config.Configクラス
	 * @param buildingList
	 *            建物のリスト<br>
	 *            このリストには災害空間上のすべての建物のEntityIDが含まれているものとしています．
	 * @param roadList
	 *            道路のリスト
	 * @param refugeList
	 *            避難所のリスト
	 * @param areaList
	 *            災害空間上のすべてのエリアのリスト
	 * @param policeForceList
	 *            啓開隊のリスト
	 * @param ambulanceTeamList
	 *            救急隊のリスト
	 * @param fireBrigadeList
	 *            消防隊のリスト
	 * @param platoonAgentList
	 *            救助隊のリスト(啓開，救急，消防)
	 */
	public RCRSCSMessageConverter(Config config, List<EntityID> buildingList,
			List<EntityID> roadList, List<EntityID> refugeList,
			List<EntityID> areaList, List<EntityID> policeForceList,
			List<EntityID> ambulanceTeamList, List<EntityID> fireBrigadeList,
			List<EntityID> platoonAgentList) {
		super();
		this.buildingList = buildingList;
		this.roadList = roadList;
		this.refugeList = refugeList;
		this.areaList = areaList;
		this.policeForceList = policeForceList;
		this.ambulanceTeamList = ambulanceTeamList;
		this.fireBrigadeList = fireBrigadeList;
		this.platoonAgentList = platoonAgentList;

		this.EXTINGUISHABLE_POWER = config.getIntValue(
				"fire.extinguish.max-sum", 500);
		this.EXTINGUISHABLE_DISTANCE = config.getIntValue(
				"fire.extinguish.max-distance", 50000);
		this.TANK_MAX = config.getIntValue("fire.tank.maximum", 7500);
		this.HP_PERCEPTION_LOS = config.getIntValue(
				"perception.los.precision.hp", 1000);
		this.DAMAGE_PERCEPTION_LOS = config.getIntValue(
				"perception.los.precision.damage", 100);
		this.RANGE_OF_VIEW = config.getIntValue("perception.los.max-distance",
				30000);

		this.messageKind = this
				.calculateBitSize(BaseMessageType.values().length);
		initBitSizeMap(this.dataBitSizeMap);
		initMessageMinimunSizeMap();
	}

	/**
	 * コンストラクタ<br>
	 * 通常使用するにはこちらのコンストラクタを使用してください．
	 * 
	 * @param model
	 * @param config
	 */
	public RCRSCSMessageConverter(StandardWorldModel model, Config config) {
		EntityIDComparator comp = new EntityIDComparator();
		this.buildingList = getIDList(model, comp, StandardEntityURN.BUILDING,
				StandardEntityURN.REFUGE, StandardEntityURN.AMBULANCE_CENTRE,
				StandardEntityURN.FIRE_STATION, StandardEntityURN.POLICE_OFFICE);
		this.roadList = getIDList(model, comp, StandardEntityURN.ROAD);
		this.refugeList = getIDList(model, comp, StandardEntityURN.REFUGE);
		this.areaList = getIDList(model, comp, StandardEntityURN.ROAD,
				StandardEntityURN.BUILDING, StandardEntityURN.AMBULANCE_CENTRE,
				StandardEntityURN.FIRE_STATION,
				StandardEntityURN.POLICE_OFFICE, StandardEntityURN.REFUGE);
		this.policeForceList = getIDList(model, comp,
				StandardEntityURN.POLICE_FORCE);
		this.ambulanceTeamList = getIDList(model, comp,
				StandardEntityURN.AMBULANCE_TEAM);
		this.fireBrigadeList = getIDList(model, comp,
				StandardEntityURN.FIRE_BRIGADE);
		this.platoonAgentList = getIDList(model, comp,
				StandardEntityURN.POLICE_FORCE,
				StandardEntityURN.AMBULANCE_TEAM,
				StandardEntityURN.FIRE_BRIGADE);

		this.EXTINGUISHABLE_POWER = config.getIntValue(
				"fire.extinguish.max-sum", 500);
		this.EXTINGUISHABLE_DISTANCE = config.getIntValue(
				"fire.extinguish.max-distance", 50000);
		this.TANK_MAX = config.getIntValue("fire.tank.maximum", 7500);
		this.HP_PERCEPTION_LOS = config.getIntValue(
				"perception.los.precision.hp", 1000);
		this.DAMAGE_PERCEPTION_LOS = config.getIntValue(
				"perception.los.precision.damage", 100);
		this.RANGE_OF_VIEW = config.getIntValue("perception.los.max-distance",
				30000);

		this.messageKind = calculateBitSize(BaseMessageType.values().length);
		initBitSizeMap(this.dataBitSizeMap);
		initMessageMinimunSizeMap();

		if (debug) {
			for (DataType type : this.dataBitSizeMap.keySet()) {
				System.out.println(type + " :" + this.dataBitSizeMap.get(type));
			}
			System.out.println("refuge size :"
					+ model.getEntitiesOfType(StandardEntityURN.REFUGE).size());
		}
	}

	private void initMessageMinimunSizeMap() {
		this.messageMininumSizeMap = new EnumMap<BaseMessageType, Integer>(
				BaseMessageType.class);
		for (BaseMessageType type : BaseMessageType.values()) {
			this.messageMininumSizeMap.put(type, messageMinimumBitSize(type));
		}
	}

	private int messageMinimumBitSize(BaseMessageType m) {
		int res = 0;
		for (DataType dt : RCRSCSMessage.COMMON_DATA_TYPE) {
			res += this.dataBitSizeMap.get(dt);
		}
		for (DataType dt : m.getDataType()) {
			if (dt == DataType.ID_LIST || dt == DataType.AREA_LIST) {
				res += 32;
			} else {
				res += this.dataBitSizeMap.get(dt);
			}
		}
		return res;
	}

	private void initBitSizeMap(EnumMap<DataType, Integer> map) {
		this.dataBitSizeMap = new EnumMap<DataType, Integer>(DataType.class);
		// time
		this.dataBitSizeMap.put(DataType.TIME, calculateBitSize(1000));
		// coordinate
		this.dataBitSizeMap.put(DataType.X_COORDINATE, 32);
		this.dataBitSizeMap.put(DataType.Y_COORDINATE, 32);
		// objects
		this.dataBitSizeMap.put(DataType.AREA, calculateBitSize(this.areaList
				.size()));
		this.dataBitSizeMap.put(DataType.ROAD, calculateBitSize(this.roadList
				.size()));
		this.dataBitSizeMap.put(DataType.BUILDING,
				calculateBitSize(this.buildingList.size()));
		this.dataBitSizeMap.put(DataType.REFUGE,
				calculateBitSize(this.refugeList.size()));
		this.dataBitSizeMap.put(DataType.BLOCKADE, 32);
		// agents
		this.dataBitSizeMap.put(DataType.HUMAN, 32);
		this.dataBitSizeMap.put(DataType.FIRE_BRIGADE,
				calculateBitSize(this.fireBrigadeList.size()));
		this.dataBitSizeMap.put(DataType.AMBULANCE_TEAM,
				calculateBitSize(this.ambulanceTeamList.size()));
		this.dataBitSizeMap.put(DataType.POLICE_FORCE,
				calculateBitSize(this.policeForceList.size()));
		this.dataBitSizeMap.put(DataType.PLATOON_AGENT,
				calculateBitSize(this.platoonAgentList.size()));
		// value
		this.dataBitSizeMap.put(DataType.HP,
				calculateBitSize((10000 / this.HP_PERCEPTION_LOS) + 1));
		this.dataBitSizeMap.put(DataType.DAMAGE,
				calculateBitSize((10000 / this.DAMAGE_PERCEPTION_LOS) + 1));
		this.dataBitSizeMap.put(DataType.BURIEDNESS, calculateBitSize(200));
		this.dataBitSizeMap.put(DataType.BROKENNESS, calculateBitSize(200));
		this.dataBitSizeMap.put(DataType.FIERYNESS, calculateBitSize(Fieryness
				.values().length));
		this.dataBitSizeMap.put(DataType.REPAIR_COST, calculateBitSize(1000));
		this.dataBitSizeMap.put(DataType.SUPPLY_QUANTITY,
				calculateBitSize(this.TANK_MAX + 1));
		this.dataBitSizeMap.put(DataType.WATER_POWER,
				calculateBitSize(this.EXTINGUISHABLE_POWER));
		this.dataBitSizeMap.put(DataType.WATER, calculateBitSize(7500));
	}

	private List<EntityID> getIDList(StandardWorldModel model,
			EntityIDComparator comp, StandardEntityURN urn) {
		return toIDList(model.getEntitiesOfType(urn), comp);
	}

	private List<EntityID> getIDList(StandardWorldModel model,
			EntityIDComparator comp, StandardEntityURN... urns) {
		return toIDList(model.getEntitiesOfType(urns), comp);
	}

	private List<EntityID> toIDList(Collection<StandardEntity> col,
			EntityIDComparator comp) {
		List<EntityID> res = new ArrayList<EntityID>();
		for (StandardEntity se : col) {
			res.add(se.getID());
		}
		Collections.sort(res, comp);
		return res;
	}

	/**
	 * 受信したバイト列をメッセージのリストに変換します．<br>
	 * 変換に失敗した場合，失敗したメッセージ以前のメッセージのリストを取得します．
	 * 
	 * @param bytes
	 *            変換するバイト列
	 * @return
	 */
	public List<RCRSCSMessage> bytesToMessageList(byte[] bytes) {
		List<Integer> bitList = toBit(bytes);
		return bitToMessages(bitList);
	}

	private List<RCRSCSMessage> bitToMessages(List<Integer> bitList) {
		List<RCRSCSMessage> res = new ArrayList<RCRSCSMessage>();
		if (debug) {
			System.out.println("-----------byte to message---------------");
		}
		for (int offset = 0; offset < bitList.size();) {
			if (debug) {
				System.out.println(res.size());
			}
			try {
				if (bitList.size() <= offset + 7) {
					break;
				}
				// get message type
				if (bitList.size() - offset < this.messageKind) {
					break;
				}
				BaseMessageType mType = null;
				int mTypeValue = bitToInt(bitList, offset, this.messageKind);
				if (debug) {
					System.out.println("offset" + offset + ";" + mTypeValue);
				}
				offset += this.messageKind;
				mType = BaseMessageType.values()[mTypeValue];
				if (bitList.size() < offset + this.messageMinimumBitSize(mType)) {
					break;
				}
				RCRSCSMessage message = null;
				switch (mType) {
				case BUILDING:
					message = new BuildingInformation(bitList, offset,
							this.dataBitSizeMap);
					break;
				case BLOCKADE:
				case BLOCKADE_WITH_COORDINATE:
					message = new BlockadeInformation(mType, bitList, offset,
							this.dataBitSizeMap);
					break;
				case VICTIM:
				case VICTIM_WITH_COORDINATE:
					message = new VictimInformation(mType, bitList, offset,
							this.dataBitSizeMap);
					break;
				case POSITION:
					message = new PositionInformation(bitList, offset,
							this.dataBitSizeMap);
					break;
				case TRANSFER_PATHWAY:
					message = new TransferInformation(bitList, offset,
							this.dataBitSizeMap);
					break;
				case FIRE_BRIGADE:
					message = new FireBrigadeInformation(bitList, offset,
							this.dataBitSizeMap);
					break;
				case POLICE_FORCE:
					message = new PoliceForceInformation(bitList, offset,
							this.dataBitSizeMap);
					break;
				case AMBULANCE_TEAM:
					message = new AmbulanceTeamInformation(bitList, offset,
							this.dataBitSizeMap);
					break;
				// case CLEAR_BLOCKADE_REQUEST:
				// message = new ClearBlockadeRequest(bitList, offset,
				// this.dataBitSizeMap);
				// break;
				// case EXTINGUISH_REQUEST:
				// message = new ExtinguishRequest(bitList, offset,
				// this.dataBitSizeMap);
				// break;
				// case RESCUE_REQUEST:
				// message = new RescueRequest(bitList, offset,
				// this.dataBitSizeMap);
				// break;
				case REST_TASK:
					message = new RestTaskMessage(bitList, offset,
							this.dataBitSizeMap);
					break;
				case REST_AT_REFUGE_TASK:
					message = new RestAtRefugeTaskMessage(bitList, offset,
							this.dataBitSizeMap);
					break;
				case MOVE_TASK:
					message = new MoveTaskMessage(bitList, offset,
							this.dataBitSizeMap);
					break;
				case MOVE_WITH_STAGING_POST_TASK:
					message = new MoveWithStagingPostTaskMessage(bitList,
							offset, this.dataBitSizeMap);
					break;
				// case CLEAR_BLOCKADE_TASK:
				// message = new ClearBlockadeTaskMessage(bitList, offset,
				// this.dataBitSizeMap);
				// break;
				// case CLEAR_AREA_TASK:
				// message = new ClearAreaTaskMessage(bitList, offset,
				// this.dataBitSizeMap);
				// break;
				// case RESCUE_TASK:
				// message = new RescueTaskMessage(bitList, offset,
				// this.dataBitSizeMap);
				// break;
				// case EXTINGUISH_TASK:
				// message = new ExtinguishTaskMessage(bitList, offset,
				// this.dataBitSizeMap);
				// break;
				// case WATER_SUPPLY_TASK:
				// message = new WaterSupplyTaskMessage(bitList, offset,
				// this.dataBitSizeMap);
				// break;
				case CLEAR_ROUTE:
					message = new ClearRouteTaskMessage(bitList, offset,
							this.dataBitSizeMap);
					break;
				case RESCUE_AREA:
					message = new RescueAreaTaskMessage(bitList, offset,
							this.dataBitSizeMap);
					break;
				case EXTINGUISH_AREA:
					message = new ExtinguishAreaTaskMessage(bitList, offset,
							this.dataBitSizeMap);
					break;
				default:
					throw new Exception("undefined message type " + mType
							+ "\n" + "decode was stopped on the way...");
				}
				offset += message.getMessageBitSize(this.dataBitSizeMap);
				if (debug) {
					System.out.println("messagebyte "
							+ message.getMessageBitSize(this.dataBitSizeMap)
							+ " " + this.messageKind + " offset :" + offset);
				}
				EnumMap<DataType, Integer> counter = new EnumMap<DataType, Integer>(
						DataType.class);
				for (DataType dt : message.getDataTypeArray()) {
					int i = getDataTypeIndex(counter, dt, 1);
					RCRSCSData<?> messageData = message.getData(dt, i);
					convertToRealData(messageData);
				}
				res.add(message);
			} catch (Exception e) {
				e.printStackTrace();
				System.err
						.println("This exception caused by message decoding step");
				break;
			}
		}
		if (debug) {
			System.out.println("-----------------------------------------");
		}
		return res;
	}

	private void convertToRealData(RCRSCSData<?> messageData) {
		if (messageData instanceof EntityIDListData) {
			List<EntityID> ids = new ArrayList<EntityID>();
			switch (messageData.getType()) {
			case AREA_LIST:
				for (EntityID id : ((EntityIDListData) messageData).getData()) {
					ids.add(this.areaList.get(id.getValue()));
				}
				((EntityIDListData) messageData).setData(ids);
				break;
			}
		} else {
			switch (messageData.getType()) {
			case AMBULANCE_TEAM:
				((EntityIDData) messageData).setData(this.ambulanceTeamList
						.get(((EntityID) messageData.getData()).getValue()));
				break;
			case FIRE_BRIGADE:
				((EntityIDData) messageData).setData(this.fireBrigadeList
						.get(((EntityID) messageData.getData()).getValue()));
				break;
			case POLICE_FORCE:
				((EntityIDData) messageData).setData(this.policeForceList
						.get(((EntityID) messageData.getData()).getValue()));
				break;
			case PLATOON_AGENT:
				((EntityIDData) messageData).setData(this.platoonAgentList
						.get(((EntityID) messageData.getData()).getValue()));
				break;
			case AREA:
				((EntityIDData) messageData).setData(this.areaList
						.get(((EntityID) messageData.getData()).getValue()));
				break;
			case ROAD:
				((EntityIDData) messageData).setData(this.roadList
						.get(((EntityID) messageData.getData()).getValue()));
				break;
			case BUILDING:
				((EntityIDData) messageData).setData(this.buildingList
						.get(((EntityID) messageData.getData()).getValue()));
				break;
			case REFUGE:
				((EntityIDData) messageData).setData(this.refugeList
						.get(((EntityID) messageData.getData()).getValue()));
				break;
			case HP:
				((ValueData) messageData).setData(((ValueData) messageData)
						.getData()
						* this.HP_PERCEPTION_LOS);
				break;
			case DAMAGE:
				((ValueData) messageData).setData(((ValueData) messageData)
						.getData()
						* this.DAMAGE_PERCEPTION_LOS);
				break;
			case BLOCKADE:
			case HUMAN:
			case TIME:
			case FIERYNESS:
			case BURIEDNESS:
			case BROKENNESS:
			case X_COORDINATE:
			case Y_COORDINATE:
			case WATER_POWER:
			case SUPPLY_QUANTITY:
			case REPAIR_COST:
			case WATER:
				break;
			default:
				System.err.println("undefined data type:"
						+ messageData.getType());
			}
		}
	}

	public static int bitToInt(List<Integer> list, int index, int length) {
		int res = 0;
		try {
			for (int i = index; i < index + length; i++) {
				res = (res << 1) | list.get(i);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(list.size() + " " + index + " " + length);
			System.exit(-1);
		}
		return res;
	}

	private static List<Integer> toBit(byte[] bytes) {
		List<Integer> res = new ArrayList<Integer>();
		for (byte b : bytes) {
			res.addAll(toBit(b, 8));
		}
		return res;
	}

	/**
	 * メッセージをバイト列に変換します．<br>
	 * 変換に失敗したメッセージは送信データには含まれません．
	 * 
	 * @param messages
	 *            送信するメッセージのリスト
	 * @return
	 */
	public byte[] messageToBytes(List<RCRSCSMessage> messages) {
		if (debug) {
			System.out.println(messages.size());
		}
		byte[] res = null;
		if (messages.size() > 0) {
			List<Integer> bitList = new ArrayList<Integer>();
			for (RCRSCSMessage m : messages) {
				bitList.addAll(messageToBit(m));
			}
			messages.clear();
			res = getBytes(bitList);
			if (debug) {
				System.out.println("bitList size :" + bitList.size());
			}
		}
		return res;
	}

	private byte[] getBytes(List<Integer> bitList) {
		int index = 0;
		int offset = 7;
		byte[] res = new byte[(bitList.size() - 1) / 8 + 1];
		for (int bit : bitList) {
			res[index] |= bit << offset;
			offset = (offset + 7) % 8;
			if (offset == 7) {
				index++;
			}
		}
		return res;
	}

	private List<Integer> messageToBit(RCRSCSMessage message) {
		List<Integer> res = new ArrayList<Integer>();
		EnumMap<DataType, Integer> counter = new EnumMap<DataType, Integer>(
				DataType.class);
		try {
			// add message type
			res.addAll(toBit(message.getMessageType().ordinal(),
					this.messageKind));
			// add message data
			for (DataType dt : message.getDataTypeArray()) {
				int index = getDataTypeIndex(counter, dt, 1);
				RCRSCSData<?> data = message.getData(dt, index);
				if (data instanceof EntityIDListData) {
					List<Integer> values = convertToMessageValue((EntityIDListData) data);
					int bitLength = 32;
					if (dt == DataType.AREA_LIST) {
						bitLength = this.dataBitSizeMap.get(DataType.AREA);
					}
					// add list size
					res.addAll(toBit(values.get(0), 32));
					for (int i = 1; i < values.size(); i++) {
						res.addAll(toBit(values.get(i), bitLength));
					}
				} else {
					if (debug) {
						System.out.println(data.getType());
					}
					res.addAll(toBit(convertToMessageValue(data),
							this.dataBitSizeMap.get(dt)));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("This message will not be sended."
					+ message.getMessageType());
			res.clear();
		}
		return res;
	}

	private static List<Integer> toBit(int value, int length) {
		List<Integer> res = new ArrayList<Integer>();
		for (int i = 0; i < length; i++) {
			res.add((value >> i) & 0x01);
		}
		Collections.reverse(res);
		return res;
	}

	private List<Integer> convertToMessageValue(EntityIDListData data)
			throws Exception {
		List<Integer> res = new ArrayList<Integer>();
		try {
			List<EntityID> ids = (List<EntityID>) data.getData();
			if (ids.size() > 0) {
				res.add(ids.size());
				switch (data.getType()) {
				case ID_LIST:
					for (EntityID id : ids) {
						int val = id.getValue();
						res.add(val);
					}
					break;
				case AREA_LIST:
					for (EntityID id : ids) {
						int index = this.areaList.indexOf(id);
						res.add(index);
					}
					break;
				}
			}
		} catch (Exception e) {
			throw new Exception();
		}
		return res;
	}

	private int convertToMessageValue(RCRSCSData<?> data) throws Exception {
		int res = -1;
		try {
			switch (data.getType()) {
			case AMBULANCE_TEAM:
				res = this.ambulanceTeamList.indexOf((EntityID) data.getData());
				break;
			case FIRE_BRIGADE:
				res = this.fireBrigadeList.indexOf((EntityID) data.getData());
				break;
			case POLICE_FORCE:
				res = this.policeForceList.indexOf((EntityID) data.getData());
				break;
			case PLATOON_AGENT:
				res = this.platoonAgentList.indexOf((EntityID) data.getData());
				break;
			case AREA:
				res = this.areaList.indexOf((EntityID) data.getData());
				break;
			case ROAD:
				res = this.roadList.indexOf((EntityID) data.getData());
				break;
			case BUILDING:
				res = this.buildingList.indexOf((EntityID) data.getData());
				if (debug) {
					System.out.println("building index:" + res
							+ "   buildingID:" + (EntityID) data.getData());
				}
				break;
			case REFUGE:
				res = this.refugeList.indexOf((EntityID) data.getData());
				break;
			case HP:
				res = (Integer) data.getData() / this.HP_PERCEPTION_LOS;
				break;
			case DAMAGE:
				res = (Integer) data.getData() / this.DAMAGE_PERCEPTION_LOS;
				break;
			case BLOCKADE:
			case HUMAN:
				res = ((EntityID) data.getData()).getValue();
				break;
			case TIME:
			case FIERYNESS:
			case BURIEDNESS:
			case BROKENNESS:
			case X_COORDINATE:
			case Y_COORDINATE:
			case WATER_POWER:
			case SUPPLY_QUANTITY:
			case REPAIR_COST:
			case WATER:
				res = (Integer) data.getData();
				break;
			default:
				System.err.println("undefined data type:" + data.getType());
			}
		} catch (Exception e) {
			System.err.println(data.getType());
			// e.printStackTrace();
			throw e;
		}
		return res;
	}

	public static int getDataTypeIndex(EnumMap<DataType, Integer> map,
			DataType dType, Integer i) {
		Integer val = map.get(dType);
		if (val == null) {
			val = 0;
		} else {
			val += i;
		}
		map.put(dType, val);
		return val;
	}

	private int calculateBitSize(int value) {
		int res = 0;
		if (value == 1) {
			res = 1;
		} else if (value > 1) {
			res = (int) Math.ceil(Math.log10(value) / Math.log10(2.0d));
		}
		return res;
	}

}
