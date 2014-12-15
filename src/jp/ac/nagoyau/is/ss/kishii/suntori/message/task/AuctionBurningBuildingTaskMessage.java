package jp.ac.nagoyau.is.ss.kishii.suntori.message.task;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

import rescuecore2.worldmodel.EntityID;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.BaseMessageType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.DataType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.EntityIDData;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.EntityIDListData;

public class AuctionBurningBuildingTaskMessage extends TaskMessage {
	
	public AuctionBurningBuildingTaskMessage(int time, EntityID ownerID, EntityID... areas) {
		this(time, ownerID, Arrays.asList(areas));
	}
	
	public AuctionBurningBuildingTaskMessage(int time, EntityID ownerID, List<EntityID> areas) {
		super(BaseMessageType.ACTION_BURNING_BUILDINGS, time, ownerID);
		this.setData(new EntityIDListData(DataType.AREA_LIST, areas));
	}
	
	public AuctionBurningBuildingTaskMessage(List<Integer> bitList, int offset,
			EnumMap<DataType, Integer> bitSizeMap) {
		super(BaseMessageType.ACTION_BURNING_BUILDINGS, bitList, offset, bitSizeMap);
	}

	/**
	 * Return list of EntityID of areas that are target of scout
	 * 
	 * @return 
	 *         List of fire fight targets({@literal List<EntityID>})
	 */
	public List<EntityID> getTargetAreaList() {
		return super.getEntityIDList(DataType.AREA_LIST, 0);
	}	
}
