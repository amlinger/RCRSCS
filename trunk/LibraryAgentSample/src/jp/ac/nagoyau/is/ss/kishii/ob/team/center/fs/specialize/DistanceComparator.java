package jp.ac.nagoyau.is.ss.kishii.ob.team.center.fs.specialize;

import java.util.Comparator;

import jp.ac.nagoyau.is.ss.kishii.ob.primitive.SuntoriBuilding;
import jp.ac.nagoyau.is.ss.kishii.ob.primitive.Util;
import rescuecore2.misc.Pair;
import rescuecore2.standard.entities.StandardWorldModel;
import rescuecore2.worldmodel.EntityID;

public class DistanceComparator implements Comparator<EntityID> {
	StandardWorldModel model;
	Pair<Integer, Integer> location;

	public DistanceComparator(StandardWorldModel model, SuntoriBuilding sb) {
		this(model, sb.getLocation(model));
	}

	public DistanceComparator(StandardWorldModel model,
			Pair<Integer, Integer> location) {
		super();
		this.model = model;
		this.location = location;
	}

	@Override
	public int compare(EntityID o1, EntityID o2) {
		double dis1 = Util.getDistance(this.location, this.model.getEntity(o1)
				.getLocation(model));
		double dis2 = Util.getDistance(this.location, this.model.getEntity(o2)
				.getLocation(model));
		return Double.compare(dis1, dis2);
	}

}
