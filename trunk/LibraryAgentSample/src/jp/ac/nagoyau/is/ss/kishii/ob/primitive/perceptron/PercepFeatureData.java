package jp.ac.nagoyau.is.ss.kishii.ob.primitive.perceptron;

import java.util.List;

import jp.ac.nagoyau.is.ss.kishii.ob.primitive.svm.FeatureData;

public class PercepFeatureData<K> extends FeatureData<K, List<Double>> {
	public PercepFeatureData(K id, List<Double> feature) {
		super(id, feature);
	}
}
