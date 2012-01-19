package jp.ac.nagoyau.is.ss.kishii.ob.primitive.perceptron;

import java.util.List;

import jp.ac.nagoyau.is.ss.kishii.ob.primitive.Feature;

public class PerceptronFeature extends Feature {
	public PerceptronFeature(List<Double> list) {
		super(list);
		this.list.add(0, 1d);
	}

}
