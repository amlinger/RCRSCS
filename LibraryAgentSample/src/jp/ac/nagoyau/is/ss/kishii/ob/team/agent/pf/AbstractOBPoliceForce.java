package jp.ac.nagoyau.is.ss.kishii.ob.team.agent.pf;

import jp.ac.nagoyau.is.ss.kishii.ob.team.agent.AbstractOBPlatoonAgent;
import rescuecore2.standard.entities.PoliceForce;
import rescuecore2.worldmodel.ChangeSet;

public abstract class AbstractOBPoliceForce extends
		AbstractOBPlatoonAgent<PoliceForce> {

	public AbstractOBPoliceForce() {
		super();
	}

	@Override
	protected void arrangeInformation(int time, ChangeSet changed) {
		super.addPoliceForceInformation(time, this.me());
		super.arrangeInformation(time, changed);
	}

}
