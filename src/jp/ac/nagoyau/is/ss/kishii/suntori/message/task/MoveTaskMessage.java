package jp.ac.nagoyau.is.ss.kishii.suntori.message.task;

import java.util.EnumMap;
import java.util.List;

import jp.ac.nagoyau.is.ss.kishii.suntori.message.BaseMessageType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.DataType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.EntityIDData;
import rescuecore2.worldmodel.EntityID;

@Deprecated
/**
 * @author takefumi
 * 
 */
public class MoveTaskMessage extends TaskMessage {
	public MoveTaskMessage(int time, EntityID ownerID, EntityID targetAgentID,
			EntityID destAreaID) {
		super(BaseMessageType.MOVE_TASK, time, ownerID);
		this.setData(new EntityIDData(DataType.PLATOON_AGENT, targetAgentID));
		// this.setData(new ValueData(DataType.AREA, destAreaID.getValue()));
		this.setData(new EntityIDData(DataType.AREA, destAreaID));
	}

	public MoveTaskMessage(List<Integer> bitList, int offset,
			EnumMap<DataType, Integer> bitSizeMap) {
		super(BaseMessageType.MOVE_TASK, bitList, offset, bitSizeMap);
	}

	public EntityID getDestinationAreaID() {
		return super.getID(DataType.AREA, 0);
	}

}
