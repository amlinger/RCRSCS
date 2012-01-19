package jp.ac.nagoyau.is.ss.kishii.ob.primitive;

public class TeacherData {
	private Feature feature;
	private int answer;

	public TeacherData(Feature f, int a) {
		this.feature = f;
		this.answer = a;
	}

	public Feature getFeature() {
		return this.feature;
	}

	public int getAnswer() {
		return this.answer;
	}

	@Override
	public String toString() {
		return "TeacherData [answer=" + answer + ":" + this.feature + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((feature == null) ? 0 : feature.hashCode());
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
		TeacherData other = (TeacherData) obj;
		if (feature == null) {
			if (other.feature != null)
				return false;
		} else if (!feature.equals(other.feature))
			return false;
		return true;
	}
}
