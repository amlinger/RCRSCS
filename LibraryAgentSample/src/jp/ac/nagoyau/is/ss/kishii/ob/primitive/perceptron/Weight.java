package jp.ac.nagoyau.is.ss.kishii.ob.primitive.perceptron;

import java.util.Arrays;

/**
 * 1次元の重みベクトルを表現するためのクラスです．
 * 
 * @author takefumi
 * 
 */
public class Weight {
	protected double[] w;

	/**
	 * コンストラクタ
	 * 
	 * @param length
	 */
	public Weight(int length) {
		this.w = new double[length];
	}

	public Weight(double[] w) {
		this.w = Arrays.copyOf(this.w, this.w.length);
	}

	/**
	 * 指定インデックスに重みの値を代入します．
	 * 
	 * @param index
	 * @param value
	 */
	public void setValue(int index, double value) {
		if (index >= 0 && index < this.w.length) {
			this.w[index] = value;
		}
	}

	/**
	 * 重みベクトルを取得します．
	 * 
	 * @return
	 */
	public double[] getValues() {
		return Arrays.copyOf(this.w, this.w.length);
	}

	public void setValues(double[] w) {
		if (this.w.length == w.length) {
			this.w = w;
		} else {
			throw new RuntimeException("weight length is illegal");
		}
	}
}
