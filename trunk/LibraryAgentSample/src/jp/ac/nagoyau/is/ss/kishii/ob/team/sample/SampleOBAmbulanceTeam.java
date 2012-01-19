package jp.ac.nagoyau.is.ss.kishii.ob.team.sample;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;

import rescuecore2.messages.Command;
import rescuecore2.standard.entities.AmbulanceTeam;
import rescuecore2.standard.entities.Civilian;
import rescuecore2.standard.entities.Human;
import rescuecore2.standard.entities.Refuge;
import rescuecore2.standard.entities.StandardEntity;
import rescuecore2.standard.entities.StandardEntityURN;
import rescuecore2.worldmodel.ChangeSet;
import rescuecore2.worldmodel.EntityID;

/**
 * A sample ambulance team agent.
 */
public class SampleOBAmbulanceTeam extends AbstractOBSampleAgent<AmbulanceTeam> {
	private Collection<EntityID> unexploredBuildings;

	@Override
	public String toString() {
		return "Sample ambulance team";
	}

	@Override
	protected void postConnect() {
		super.postConnect();
		model.indexClass(StandardEntityURN.CIVILIAN,
				StandardEntityURN.FIRE_BRIGADE, StandardEntityURN.POLICE_FORCE,
				StandardEntityURN.AMBULANCE_TEAM, StandardEntityURN.REFUGE,
				StandardEntityURN.BUILDING);
		unexploredBuildings = new HashSet<EntityID>(buildingIDs);
	}

	@Override
	protected void thinking2(int time, ChangeSet changed,
			Collection<Command> heard) {
		updateUnexploredBuildings(changed);
		// Am I transporting a civilian to a refuge?
		if (someoneOnBoard()) {
			// Am I at a refuge?
			if (location() instanceof Refuge) {
				// Unload!
				sendUnload(time);
				return;
			} else {
				// Move to a refuge
				List<EntityID> path = search.breadthFirstSearch(me()
						.getPosition(), refugeIDs);
				if (path != null) {
					sendMove(time, path);
					return;
				}
				// What do I do now? Might as well carry on and see if we can
				// dig someone else out.
			}
		}
		// Go through targets (sorted by distance) and check for things we can
		// do
		for (Human next : getTargets()) {
			if (next.getPosition().equals(location().getID())) {
				// Targets in the same place might need rescueing or loading
				if ((next instanceof Civilian) && next.getBuriedness() == 0
						&& !(location() instanceof Refuge)) {
					// Load
					sendLoad(time, next.getID());
					return;
				}
				if (next.getBuriedness() > 0) {
					// Rescue
					sendRescue(time, next.getID());
					return;
				}
			} else {
				// Try to move to the target
				List<EntityID> path = search.breadthFirstSearch(me()
						.getPosition(), next.getPosition());
				if (path != null) {
					sendMove(time, path);
					return;
				}
			}
		}
		// Nothing to do
		List<EntityID> path = search.breadthFirstSearch(me().getPosition(),
				unexploredBuildings);
		if (path != null) {
			sendMove(time, path);
			return;
		}
		sendMove(time, randomWalk());
	}

	@Override
	protected EnumSet<StandardEntityURN> getRequestedEntityURNsEnum() {
		return EnumSet.of(StandardEntityURN.AMBULANCE_TEAM);
	}

	private boolean someoneOnBoard() {
		for (StandardEntity next : model
				.getEntitiesOfType(StandardEntityURN.CIVILIAN)) {
			if (((Human) next).getPosition().equals(getID())) {
				return true;
			}
		}
		return false;
	}

	private List<Human> getTargets() {
		List<Human> targets = new ArrayList<Human>();
		for (StandardEntity next : model.getEntitiesOfType(
				StandardEntityURN.CIVILIAN, StandardEntityURN.FIRE_BRIGADE,
				StandardEntityURN.POLICE_FORCE,
				StandardEntityURN.AMBULANCE_TEAM)) {
			Human h = (Human) next;
			if (h == me()) {
				continue;
			}
			if (h.isHPDefined() && h.isBuriednessDefined()
					&& h.isDamageDefined() && h.isPositionDefined()
					&& h.getHP() > 0
					&& (h.getBuriedness() > 0 || h.getDamage() > 0)) {
				targets.add(h);
			}
		}
		Collections.sort(targets, new DistanceSorter(location(), model));
		return targets;
	}

	private void updateUnexploredBuildings(ChangeSet changed) {
		for (EntityID next : changed.getChangedEntities()) {
			unexploredBuildings.remove(next);
		}
	}
}
