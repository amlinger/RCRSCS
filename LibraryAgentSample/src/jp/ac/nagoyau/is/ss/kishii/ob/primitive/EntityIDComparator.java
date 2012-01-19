package jp.ac.nagoyau.is.ss.kishii.ob.primitive;

import java.util.Comparator;

import rescuecore2.worldmodel.EntityID;

public class EntityIDComparator implements Comparator<EntityID> {
	@Override
	public int compare(EntityID o1, EntityID o2) {
		return o1.getValue() - o2.getValue();
	}
}
