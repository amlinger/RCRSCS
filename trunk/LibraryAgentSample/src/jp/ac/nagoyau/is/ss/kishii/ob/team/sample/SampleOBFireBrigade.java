package jp.ac.nagoyau.is.ss.kishii.ob.team.sample;

import static rescuecore2.misc.Handy.objectsToIDs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import rescuecore2.messages.Command;
import rescuecore2.standard.entities.Building;
import rescuecore2.standard.entities.FireBrigade;
import rescuecore2.standard.entities.Refuge;
import rescuecore2.standard.entities.StandardEntity;
import rescuecore2.standard.entities.StandardEntityURN;
import rescuecore2.worldmodel.ChangeSet;
import rescuecore2.worldmodel.EntityID;

/**
 * A sample fire brigade agent.
 */
public class SampleOBFireBrigade extends AbstractOBSampleAgent<FireBrigade> {
	private static final String MAX_WATER_KEY = "fire.tank.maximum";
	private static final String MAX_DISTANCE_KEY = "fire.extinguish.max-distance";
	private static final String MAX_POWER_KEY = "fire.extinguish.max-sum";

	private int maxWater;
	private int maxDistance;
	private int maxPower;

	public SampleOBFireBrigade() {
		super();
	}

	@Override
	public String toString() {
		return "Sample fire brigade";
	}

	@Override
	protected void postConnect() {
		super.postConnect();
		model.indexClass(StandardEntityURN.BUILDING, StandardEntityURN.REFUGE);
		maxWater = config.getIntValue(MAX_WATER_KEY);
		maxDistance = config.getIntValue(MAX_DISTANCE_KEY);
		maxPower = config.getIntValue(MAX_POWER_KEY);
	}

	@Override
	protected void thinking2(int time, ChangeSet changed,
			Collection<Command> heard) {
		FireBrigade me = me();
		// Are we currently filling with water?
		if (me.isWaterDefined() && me.getWater() < maxWater
				&& location() instanceof Refuge) {
			sendRest(time);
			return;
		}
		// Are we out of water?
		if (me.isWaterDefined() && me.getWater() == 0) {
			// Head for a refuge
			List<EntityID> path = search.breadthFirstSearch(me().getPosition(),
					refugeIDs);
			if (path != null) {
				sendMove(time, path);
				return;
			} else {
				path = randomWalk();
				sendMove(time, path);
				return;
			}
		}
		// Find all buildings that are on fire
		Collection<EntityID> all = getBurningBuildings();
		// Can we extinguish any right now?
		for (EntityID next : all) {
			if (model.getDistance(getID(), next) <= maxDistance) {
				sendExtinguish(time, next, maxPower);
				// sendSpeak(time, 1, ("Extinguishing " + next).getBytes());
				return;
			}
		}
		// Plan a path to a fire
		for (EntityID next : all) {
			List<EntityID> path = planPathToFire(next);
			if (path != null) {
				sendMove(time, path);
				return;
			}
		}
		List<EntityID> path = null;
		path = randomWalk();
		sendMove(time, path);
	}

	@Override
	protected EnumSet<StandardEntityURN> getRequestedEntityURNsEnum() {
		return EnumSet.of(StandardEntityURN.FIRE_BRIGADE);
	}

	private Collection<EntityID> getBurningBuildings() {
		Collection<StandardEntity> e = model
				.getEntitiesOfType(StandardEntityURN.BUILDING);
		List<Building> result = new ArrayList<Building>();
		for (StandardEntity next : e) {
			if (next instanceof Building) {
				Building b = (Building) next;
				if (b.isOnFire()) {
					result.add(b);
				}
			}
		}
		// Sort by distance
		Collections.sort(result, new DistanceSorter(location(), model));
		return objectsToIDs(result);
	}

	private List<EntityID> planPathToFire(EntityID target) {
		// Try to get to anything within maxDistance of the target
		Collection<StandardEntity> targets = model.getObjectsInRange(target,
				maxDistance);
		if (targets.isEmpty()) {
			return null;
		}
		return search.breadthFirstSearch(me().getPosition(),
				objectsToIDs(targets));
	}
}
