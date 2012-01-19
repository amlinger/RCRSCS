package jp.ac.nagoyau.is.ss.kishii.ob.primitive.perceptron;

import java.util.List;
import java.util.Random;

/**
 * Perceptron用の重みクラスです．
 * 
 * @author takefumi
 * 
 */
public class PerceptronWeight extends Weight {

	/**
	 * コンストラクタ<br/>
	 * w0のセットもここで行います．
	 * 
	 * @param length
	 */
	public PerceptronWeight(int length) {
		super(length + 1);
		Random rand = new Random();
		rand.nextDouble();
		double w0 = 0;
		for (int i = 1; i < this.w.length; i++) {
			this.w[i] = rand.nextDouble();
			w0 += (this.w[i]) * (this.w[i]);
		}
		w0 = -Math.sqrt(w0) / 2;
		this.w[0] = w0;
	}

	public PerceptronWeight(double[] w) {
		super(w);
	}

	/**
	 * 重みの更新を行います．
	 * 
	 * @param rate
	 *            更新率(>0)
	 * @param x
	 *            更新用ベクトル
	 */
	public void update(double rate, double[] x, boolean plus) {
		if (this.w.length == x.length) {
			if (plus) {
				for (int i = 0; i < this.w.length; i++) {
					this.w[i] = this.w[i] + (rate * x[i]);
				}
			} else {
				for (int i = 0; i < this.w.length; i++) {
					this.w[i] = this.w[i] - (rate * x[i]);
				}
			}
		} else {
			throw new RuntimeException(
					"Length of weight and update vector must be same.");
		}
	}

	/**
	 * 重みの更新を行います．
	 * 
	 * @param rate
	 *            更新率(>0)
	 * @param x
	 *            更新用ベクトル
	 */
	public void update(double rate, List<Double> x, boolean plus) {
		if (this.w.length == x.size()) {
			if (plus) {
				for (int i = 0; i < this.w.length; i++) {
					this.w[i] = this.w[i] + (rate * x.get(i));
				}
			} else {
				for (int i = 0; i < this.w.length; i++) {
					this.w[i] = this.w[i] - (rate * x.get(i));
				}
			}
		} else {
			throw new RuntimeException(
					"Length of weight and update vector must be same.");
		}
	}

	@Override
	public String toString() {
		String res = "weight:";
		for (int i = 0; i < this.w.length; i++) {
			res += this.w[i];
			if (i < this.w.length - 1) {
				res += ",";
			}
		}
		return res;
	}
}
