package jp.ac.nagoyau.is.ss.kishii.ob.team.center.fs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;

import jp.ac.nagoyau.is.ss.kishii.ob.primitive.FireSite;
import jp.ac.nagoyau.is.ss.kishii.ob.primitive.SuntoriBuilding;
import jp.ac.nagoyau.is.ss.kishii.ob.team.center.AbstractOBCenter;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.task.fb.ExtinguishAreaTaskMessage;
import rescuecore2.messages.Command;
import rescuecore2.standard.entities.FireStation;
import rescuecore2.standard.entities.StandardEntityURN;
import rescuecore2.worldmodel.ChangeSet;
import rescuecore2.worldmodel.EntityID;

public class RandomCommandFireStation extends AbstractOBCenter<FireStation> {
	Random rand;
	List<EntityID> chargeFBList;

	public RandomCommandFireStation() {
		super();
		this.rand = new Random();
		this.rand.nextBoolean();
		this.chargeFBList = new ArrayList<EntityID>();

	}

	@Override
	protected void postConnect() {
		super.postConnect();
		int myIndex = this.fireStationList.indexOf(this.myID);
		for (int i = 0; i < this.fireBrigadeList.size(); i++) {
			int index = i % this.fireStationList.size();
			if (myIndex == index) {
				this.chargeFBList.add(this.fireBrigadeList.get(index));
			}
		}
	}

	@Override
	protected void thinking2(int time, ChangeSet changed,
			Collection<Command> heard) {
		List<EntityID> candidateList = new ArrayList<EntityID>();
		for (FireSite fs : this.fireSites) {
			if (fs.getBurningBuilding().size() > 0) {// 燃えている
				if (this.rand.nextBoolean()) {
					candidateList.add(new ArrayList<SuntoriBuilding>(fs
							.getBuildings()).get(0).getID());
				}
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
