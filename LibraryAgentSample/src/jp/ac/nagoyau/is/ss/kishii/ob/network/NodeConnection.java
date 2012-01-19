package jp.ac.nagoyau.is.ss.kishii.ob.network;

import java.util.HashSet;
import java.util.Set;

import rescuecore2.worldmodel.EntityID;

public class NodeConnection {
	private Set<EntityID> set;

	public NodeConnection(EntityID from, EntityID to) {
		this.set = new HashSet<EntityID>();
		this.set.add(from);
		this.set.add(to);
	}

	public Set<EntityID> getConnection() {
		return this.set;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((set == null) ? 0 : set.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NodeConnection other = (NodeConnection) obj;
		if (set == null) {
			if (other.set != null)
				return false;
		} else if (!set.equals(other.set))
			return false;
		return true;
	}
}
