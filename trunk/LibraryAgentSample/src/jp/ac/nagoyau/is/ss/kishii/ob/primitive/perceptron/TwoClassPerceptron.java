package jp.ac.nagoyau.is.ss.kishii.ob.primitive.perceptron;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import jp.ac.nagoyau.is.ss.kishii.ob.primitive.TeacherData;

public class TwoClassPerceptron extends AbstractPerceptron {
	private int LOOP_MAX = -1;
	private final static int WEIGHT_INDEX = 0;

	public TwoClassPerceptron(int dimension, double rate, int loopMap) {
		super(2, dimension, rate);
		this.LOOP_MAX = loopMap;
	}

	public TwoClassPerceptron(double[] w) {
		super(new PerceptronWeight(w));
	}

	@Override
	public void learn(List<TeacherData> dataList) {
		System.out.println("start learning");
		int loopCount = 0;
		while (LOOP_MAX < 0 || loopCount < LOOP_MAX) {
			int missCount = 0;
			// System.out.println("before:"
			// + this.weights[WEIGHT_INDEX].toString());
			for (TeacherData d : dataList) {
				double eValue = this.calcEstimateValue(0, d.getFeature().get());
				if (eValue > 0) {// Class1と判断
					if (d.getAnswer() == 2) {
						this.weights[WEIGHT_INDEX].update(this.rate, d
								.getFeature().get(), false);
						missCount++;
					}
				} else {// Class2と判断
					if (d.getAnswer() == 1) {
						this.weights[WEIGHT_INDEX].update(this.rate, d
								.getFeature().get(), true);
						missCount++;
					}
				}
			}
			// System.out
			// .println("after:" + this.weights[WEIGHT_INDEX].toString());

			if (missCount == 0) {
				break;
			}
			if (LOOP_MAX >= 0) {
				loopCount++;
			}
			// System.out.println(missCount);
		}
		System.out.println("end learning");
	}

	@Override
	public void test(List<TeacherData> dataList) {
		System.out.println("start test");
		int corCor = 0;
		int corIncor = 0;
		int incorCor = 0;
		int incorIncor = 0;

		for (TeacherData data : dataList) {
			double eValue = this.calcEstimateValue(WEIGHT_INDEX, data
					.getFeature().get());
			if (eValue > 0) {
				if (data.getAnswer() == 1) {
					corCor++;
				} else {
					corIncor++;
				}
			} else {
				if (data.getAnswer() == 1) {
					incorCor++;
				} else {
					incorIncor++;
				}
			}

		}
		System.out.println("end test");
		System.out.println("[result]");
		int corTotal = corCor + corIncor;
		int incorTotal = incorCor + incorIncor;
		System.out.println("confusion matrix");
		System.out.println("正解,不正解");
		System.out.println("正解," + ((double) corCor / corTotal) + ","
				+ ((double) corIncor / corTotal));
		System.out.println("不正解," + ((double) incorCor / incorTotal) + ","
				+ ((double) incorIncor / incorTotal));
		System.out.println();
		System.out.println("正解,不正解");
		System.out.println("正解," + corCor + "," + corIncor);
		System.out.println("不正解," + incorCor + "," + incorIncor);
	}

	public static TwoClassPerceptron loadModel(String filePath)
			throws IOException {
		try {
			FileInputStream fis = new FileInputStream(filePath);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);

			String line = null;
			line = br.readLine();
			int dimension = 0;
			if (line != null) {
				dimension = Integer.parseInt(line);
				line = br.readLine();
				if (line != null) {
					String[] data = line.split(",");
					double[] w = new double[dimension];
					for (int i = 0; i < dimension; i++) {
						w[i] = Double.parseDouble(data[i]);
					}
					br.close();
					return new TwoClassPerceptron(w);
				} else {
					br.close();
					throw new IOException("Counld not read file:" + filePath);
				}
			} else {
				br.close();
				throw new IOException("Counld not read file:" + filePath);
			}
		} catch (IOException e) {
			throw e;
		}
	}

	public double calcEstimateValue(List<Double> data) throws RuntimeException {
		return super.calcEstimateValue(WEIGHT_INDEX, data);
	}
}
