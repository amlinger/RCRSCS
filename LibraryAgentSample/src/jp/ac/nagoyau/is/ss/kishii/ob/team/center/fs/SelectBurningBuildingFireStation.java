package jp.ac.nagoyau.is.ss.kishii.ob.team.center.fs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;

import jp.ac.nagoyau.is.ss.kishii.ob.primitive.SuntoriBuilding;
import jp.ac.nagoyau.is.ss.kishii.ob.team.center.AbstractOBCenter;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.task.fb.ExtinguishAreaTaskMessage;
import rescuecore2.messages.Command;
import rescuecore2.standard.entities.FireStation;
import rescuecore2.standard.entities.StandardEntityURN;
import rescuecore2.worldmodel.ChangeSet;
import rescuecore2.worldmodel.EntityID;

public class SelectBurningBuildingFireStation extends
		AbstractOBCenter<FireStation> {
	Random rand;
	List<EntityID> chargeFBList;

	public SelectBurningBuildingFireStation() {
		super();
		this.rand = new Random();
		this.rand.nextBoolean();
		this.chargeFBList = new ArrayList<EntityID>();

	}

	@Override
	protected void postConnect() {
		super.postConnect();
		int myIndex = this.fireStationList.indexOf(this.myID);
		// for (int i = 0; i < this.fireBrigadeList.size(); i++) {
		// int index = i % this.fireStationList.size();
		// if (myIndex == index) {
		// this.chargeFBList.add(this.fireBrigadeList.get(index));
		// }
		// }
		this.chargeFBList = new ArrayList<EntityID>(this.fireBrigadeList);
	}

	@Override
	protected void thinking2(int time, ChangeSet changed,
			Collection<Command> heard) {
		List<EntityID> candidateList = new ArrayList<EntityID>();
		for (SuntoriBuilding sb : this.sbList) {
			if (sb.getFieryness() == 1 || sb.getFieryness() == 2) {
				candidateList.add(sb.getID());
			}
		}
		if (candidateList.size() > 0) {
			for (EntityID id : this.chargeFBList) {
				this.addMessage(new ExtinguishAreaTaskMessage(time, this.myID,
						id, candidateList));
			}
		}
	}

	@Override
	protected EnumSet<StandardEntityURN> getRequestedEntityURNsEnum() {
		return EnumSet.of(StandardEntityURN.FIRE_STATION);
	}
}
