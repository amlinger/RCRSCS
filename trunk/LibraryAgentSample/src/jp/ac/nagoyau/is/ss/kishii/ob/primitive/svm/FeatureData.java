package jp.ac.nagoyau.is.ss.kishii.ob.primitive.svm;

public class FeatureData<K, V> {
	private K id;
	private V feature;

	public FeatureData(K id, V feature) {
		this.id = id;
		this.feature = feature;
	}

	public K getId() {
		return this.id;
	}

	public V getFeature() {
		return this.feature;
	}
}
