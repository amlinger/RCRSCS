package jp.ac.nagoyau.is.ss.kishii.suntori.message.data;

import rescuecore2.worldmodel.EntityID;

public enum DataType {
	// value type
	/**
	 * time that simulation is started.
	 */
	TIME,

	/**
	 * x coordinate of the entities.
	 */
	X_COORDINATE,
	/**
	 * y coordinate of the entities.
	 */
	Y_COORDINATE,
	/**
	 * area id.
	 */
	AREA,
	/**
	 * road id.
	 */
	ROAD,
	/**
	 * building id.
	 */
	BUILDING,
	/**
	 * refuge id.
	 */
	REFUGE,
	/**
	 * blockade id.
	 */
	BLOCKADE,

	HUMAN, FIRE_BRIGADE, AMBULANCE_TEAM, POLICE_FORCE,

	PLATOON_AGENT,

	FIRE_STATION, AMBULANCE_CENTER, POLICE_OFFICE,

	CENTER_AGENT, RESCUE_AGENT,

	HP, DAMAGE, BURIEDNESS, FIERYNESS, WATER_POWER, SUPPLY_QUANTITY, REPAIR_COST, BROKENNESS, WATER,
	// list type
	ID_LIST, AREA_LIST;

	public static RCRSCSData<?> createData(DataType type, int value) {
		RCRSCSData<?> res = null;
		switch (type) {
		case TIME:
		case X_COORDINATE:
		case Y_COORDINATE:
		case HP:
		case DAMAGE:
		case BURIEDNESS:
		case FIERYNESS:
		case WATER_POWER:
		case SUPPLY_QUANTITY:
		case REPAIR_COST:
		case BROKENNESS:
		case WATER:
			res = new ValueData(type, value);
			break;
		case AREA:
		case ROAD:
		case BUILDING:
		case REFUGE:
		case BLOCKADE:
		case HUMAN:
		case FIRE_BRIGADE:
		case AMBULANCE_TEAM:
		case POLICE_FORCE:
		case PLATOON_AGENT:
		case CENTER_AGENT:
		case RESCUE_AGENT:
			res = new EntityIDData(type, new EntityID(value));
		default:
		}
		return res;
	}

	public static EntityIDListData createIDListData(DataType type) {
		EntityIDListData res = null;
		switch (type) {
		case ID_LIST:
		case AREA_LIST:
			res = new EntityIDListData(type);
			break;
		default:
		}
		return res;
	}
}
