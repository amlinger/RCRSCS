package jp.ac.nagoyau.is.ss.kishii.ob.primitive.svm;

import libsvm.wrapper.SvmFeatureVector;

public class SvmFeatureData<K> extends FeatureData<K, SvmFeatureVector> {
	public SvmFeatureData(K id, SvmFeatureVector feature) {
		super(id, feature);
	}
}
