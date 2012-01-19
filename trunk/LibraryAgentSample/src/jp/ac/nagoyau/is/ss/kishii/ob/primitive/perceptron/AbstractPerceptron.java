package jp.ac.nagoyau.is.ss.kishii.ob.primitive.perceptron;

import java.util.List;

import jp.ac.nagoyau.is.ss.kishii.ob.primitive.TeacherData;

public abstract class AbstractPerceptron {
	protected PerceptronWeight[] weights;
	protected double rate;

	public AbstractPerceptron(int classNum, int dimension, double rate) {
		this.weights = new PerceptronWeight[classNum];
		for (int i = 0; i < classNum - 1; i++) {
			this.weights[i] = new PerceptronWeight(dimension);
		}
		this.rate = rate;
	}

	public AbstractPerceptron(PerceptronWeight... weights) {
		this.weights = weights;
	}

	/**
	 * 
	 * @param index
	 *            使用するweightのindex.パーセプトロンの場合は0を指定
	 * @param data
	 * @return
	 * @throws RuntimeException
	 */
	protected double calcEstimateValue(int index, List<Double> vals)
			throws RuntimeException {
		double[] weightMat = weights[index].getValues();
		double res = weightMat[0];
		if (weightMat.length == vals.size()) {
			for (int i = 1; i < weightMat.length; i++) {
				res += weightMat[i] * vals.get(i);
			}
		} else {
			throw new RuntimeException(
					"length of weight mat and that of feature are not equal...\n"
							+ "" + weightMat.length + ":" + vals.size());
		}
		return res;
	}

	/**
	 * 重みの学習を行います．
	 * 
	 * @param dataList
	 *            学習データリスト
	 */
	public abstract void learn(List<TeacherData> dataList);

	/**
	 * テストを行います．
	 * 
	 * @param dataList
	 *            テストデータリスト
	 */
	public abstract void test(List<TeacherData> dataList);
}
