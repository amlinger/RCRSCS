package jp.ac.nagoyau.is.ss.kishii.ob.team.agent.at;

import java.util.Collection;
import java.util.EnumSet;

import jp.ac.nagoyau.is.ss.kishii.ob.team.agent.AbstractOBPlatoonAgent;
import rescuecore2.messages.Command;
import rescuecore2.standard.entities.AmbulanceTeam;
import rescuecore2.standard.entities.StandardEntityURN;
import rescuecore2.worldmodel.ChangeSet;

public class AbstractOBAmbulanceTeam extends
		AbstractOBPlatoonAgent<AmbulanceTeam> {
	@Override
	protected EnumSet<StandardEntityURN> getRequestedEntityURNsEnum() {
		return EnumSet.of(StandardEntityURN.AMBULANCE_TEAM);
	}

	@Override
	protected void arrangeInformation(int time, ChangeSet changed) {
		super.addAmbulanceTeamInformation(time, this.me());
		super.arrangeInformation(time, changed);
	}

	@Override
	protected void arrangeChargeArea() {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	protected void thinking2(int arg0, ChangeSet arg1, Collection<Command> arg2) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	protected void setSelfDataToMessage(int time) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	protected void arrangeTasks(int time) {
		// TODO 自動生成されたメソッド・スタブ
		
	}
}
