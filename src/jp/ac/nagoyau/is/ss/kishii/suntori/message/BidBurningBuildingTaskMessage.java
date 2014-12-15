package jp.ac.nagoyau.is.ss.kishii.suntori.message;

import java.util.EnumMap;
import java.util.List;

import rescuecore2.worldmodel.EntityID;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.DataType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.ValueData;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.task.TaskMessage;

public class BidBurningBuildingTaskMessage extends TaskMessage {
	
	public BidBurningBuildingTaskMessage(int time, EntityID ownerID, int cost) {
		super(BaseMessageType.BID_BURNING_BUILDINGS, time, ownerID);
		this.setData(new ValueData(DataType.REPAIR_COST, cost));
		
	}

	public BidBurningBuildingTaskMessage(List<Integer> bitList, int offset,
			EnumMap<DataType, Integer> bitSizeMap) {
		super(BaseMessageType.BID_BURNING_BUILDINGS, bitList, offset, bitSizeMap);
	}

	
	public int getCost() {
		return super.getRepairCost(0);
	}
}
