package jp.ac.nagoyau.is.ss.kishii.suntori.message;

import java.util.Comparator;

import rescuecore2.worldmodel.EntityID;

class EntityIDComparator implements Comparator<EntityID> {
	@Override
	public int compare(EntityID o1, EntityID o2) {
		return o1.getValue() - o2.getValue();
	}
}
