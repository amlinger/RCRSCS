package jp.ac.nagoyau.is.ss.kishii.ob.team.center.fs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

import jp.ac.nagoyau.is.ss.kishii.ob.primitive.SuntoriBuilding;
import jp.ac.nagoyau.is.ss.kishii.ob.team.center.AbstractOBCenter;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.task.fb.ExtinguishAreaTaskMessage;
import rescuecore2.messages.Command;
import rescuecore2.standard.entities.FireStation;
import rescuecore2.standard.entities.StandardEntityURN;
import rescuecore2.worldmodel.ChangeSet;
import rescuecore2.worldmodel.EntityID;

public class OBFireStation extends AbstractOBCenter<FireStation> {
	List<EntityID> chargeFBList;

	public OBFireStation() {
		super();
		this.chargeFBList = new ArrayList<EntityID>();
	}

	@Override
	protected void postConnect() {
		super.postConnect();
		int myIndex = this.fireStationList.indexOf(this.myID);
		for (int i = 0; i < this.fireBrigadeList.size(); i++) {
			int index = i % this.fireStationList.size();
			if (myIndex == index) {
				this.chargeFBList.add(this.fireBrigadeList.get(i));
			}
		}
	}

	@Override
	protected void thinking2(int time, ChangeSet changed,
			Collection<Command> heard) {
		List<EntityID> areas = new ArrayList<EntityID>();
		for (SuntoriBuilding sb : this.sbList) {
			if (sb.isBurning()) {
				areas.add(sb.getID());
			}
		}
		if (areas.size() > 0) {
			for (EntityID agentID : this.chargeFBList) {
				this.addMessage(new ExtinguishAreaTaskMessage(time, this.myID,
						agentID, areas));
			}
		}
	}

	@Override
	protected EnumSet<StandardEntityURN> getRequestedEntityURNsEnum() {
		return EnumSet.of(StandardEntityURN.FIRE_STATION);
	}

}
