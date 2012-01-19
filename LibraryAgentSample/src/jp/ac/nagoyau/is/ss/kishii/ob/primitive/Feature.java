package jp.ac.nagoyau.is.ss.kishii.ob.primitive;

import java.util.List;

public class Feature {
	protected List<Double> list;

	public Feature(List<Double> list) {
		this.list = list;
	}

	public List<Double> get() {
		return this.list;
	}

	@Override
	public String toString() {
		String str = "Feature[";
		for (int i = 0; i < this.list.size(); i++) {
			str += this.list.get(i);
			if (i < this.list.size() - 1) {
				str += ",";
			}
		}
		str += "]";
		return str;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((list == null) ? 0 : list.hashCode());
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
		Feature other = (Feature) obj;
		if (list == null) {
			if (other.list != null)
				return false;
		} else if (!list.equals(other.list))
			return false;
		return true;
	}
}
