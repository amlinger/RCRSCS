package jp.ac.nagoyau.is.ss.kishii.ob.team.center.fs.specialize;

import java.util.Comparator;
import java.util.Map;

public class HashValueComparator<T> implements Comparator<T> {
	Map<T, Double> scoreMap;

	public HashValueComparator(Map<T, Double> scoreMap) {
		this.scoreMap = scoreMap;
	}

	@Override
	public int compare(T o1, T o2) {
		return Double.compare(this.scoreMap.get(o2), this.scoreMap.get(o1));
	}
}
