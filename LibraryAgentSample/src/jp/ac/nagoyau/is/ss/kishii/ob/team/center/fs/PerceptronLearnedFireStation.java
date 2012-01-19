package jp.ac.nagoyau.is.ss.kishii.ob.team.center.fs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

import jp.ac.nagoyau.is.ss.kishii.ob.primitive.FireSite;
import jp.ac.nagoyau.is.ss.kishii.ob.primitive.SuntoriBuilding;
import jp.ac.nagoyau.is.ss.kishii.ob.primitive.perceptron.PercepFeatureData;
import jp.ac.nagoyau.is.ss.kishii.ob.primitive.perceptron.TwoClassPerceptron;
import jp.ac.nagoyau.is.ss.kishii.ob.team.center.AbstractOBCenter;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.task.fb.ExtinguishAreaTaskMessage;
import rescuecore2.messages.Command;
import rescuecore2.standard.entities.FireStation;
import rescuecore2.standard.entities.StandardEntityURN;
import rescuecore2.worldmodel.ChangeSet;
import rescuecore2.worldmodel.EntityID;

public class PerceptronLearnedFireStation extends AbstractOBCenter<FireStation> {
	TwoClassPerceptron percepModel;
	private static final String MODEL_FILE_PATH = "perceptronModel.dat";

	List<EntityID> chargeFBList;

	public PerceptronLearnedFireStation() {
		super();
		try {
			this.percepModel = TwoClassPerceptron.loadModel(MODEL_FILE_PATH);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.chargeFBList = new ArrayList<EntityID>();
	}

	@Override
	protected void postConnect() {
		super.postConnect();
		int myIndex = this.fireStationList.indexOf(this.myID);
		for (int i = 0; i < this.fireBrigadeList.size(); i++) {
			int index = i % this.fireStationList.size();
			if (myIndex == index) {
				this.chargeFBList.add(this.fireBrigadeList.get(index));
			}
		}
	}

	@Override
	protected EnumSet<StandardEntityURN> getRequestedEntityURNsEnum() {
		return EnumSet.of(StandardEntityURN.FIRE_STATION);
	}

	@Override
	protected void thinking2(int time, ChangeSet changed,
			Collection<Command> heard) {
		List<PercepFeatureData<Integer>> fdList = this.getBuildingFeatures();
		List<EntityID> candidateList = new ArrayList<EntityID>();
		for (PercepFeatureData<Integer> fd : fdList) {
			double val = this.percepModel.calcEstimateValue(fd.getFeature());
			if (val > 0) {
				candidateList.add(new ArrayList<SuntoriBuilding>(this.fireSites
						.get(fd.getId()).getBuildings()).get(0).getID());
			}
		}
		for (EntityID id : this.chargeFBList) {
			this.addMessage(new ExtinguishAreaTaskMessage(time, this.myID, id,
					candidateList));
		}
	}

	/**
	 * 建物に関する特徴ベクトルを生成する．
	 * 
	 * @return
	 */
	protected List<PercepFeatureData<Integer>> getBuildingFeatures() {
		List<PercepFeatureData<Integer>> res = new ArrayList<PercepFeatureData<Integer>>();
		for (FireSite site : this.fireSites) {
			List<Double> featureVec = new ArrayList<Double>();
			// 燃焼度
			double[] degree = new double[9];
			for (SuntoriBuilding sb : site.getBuildings()) {
				degree[sb.getFieryness()]++;
			}
			int sum = site.getBuildings().size();
			if (sum > 0) {
				for (int i = 0; i < degree.length; i++) {
					degree[i] /= sum;
				}
			}
			addElementToVec(featureVec, degree);
			// 隣接する地域の燃焼度による割合
			degree = new double[9];
			sum = 0;
			for (FireSite neighbor : site.getConnectionFireSite()) {
				sum += neighbor.getBuildings().size();
				for (SuntoriBuilding sb : neighbor.getBuildings()) {
					degree[sb.getFieryness()]++;
				}
			}
			if (sum > 0) {
				for (int i = 0; i < degree.length; i++) {
					degree[i] /= sum;
				}
			}
			addElementToVec(featureVec, degree);
			// // 隣接するエリア数
			// featureVec.add((double) site.getConnectionFireSite().size());
			// // 注目している地域に含まれる建物数
			// featureVec.add((double) site.getBuildings().size());
			// // 注目している地域に含まれる建物の平均面積
			// double average = 0;
			// for (SuntoriBuilding sb : site.getBuildings()) {
			// average += sb.getGroundArea();
			// }
			// average /= site.getBuildings().size();
			// featureVec.add(average);
			// // Group Num
			// List<SiteGroup> groupList = Util.getFireAreaGroupList(model,
			// site.getConnectionFireSite());
			// featureVec.add((double) groupList.size());
			// // Targetから全隣接火災の重心までの距離
			// List<FireSite> burningFireSiteList = Util.getBurningFireSite(site
			// .getConnectionFireSite());
			// double x = 0;
			// double y = 0;
			// if (burningFireSiteList.size() > 0) {
			// for (FireSite fs : burningFireSiteList) {
			// x += fs.getX(model);
			// y += fs.getY(model);
			// }
			// x /= burningFireSiteList.size();
			// y /= burningFireSiteList.size();
			// }
			// double distance = Util.getDistance(site.getX(model),
			// site.getY(model), x, y);
			// featureVec.add(distance);
			// // 火災が関係しているFireSiteの数
			// burningFireSiteList = Util.getBurningFireSite(this.fireSites);
			// groupList = Util.getFireAreaGroupList(model, this.fireSites);
			// if (burningFireSiteList.contains(site)) {
			// for (SiteGroup sg : groupList) {
			// if (sg.contains(site)) {
			// featureVec.add((double) sg.size());
			// break;
			// }
			// }
			// } else {
			// featureVec.add(0d);
			// }
			// // FireGroupの数
			// featureVec.add((double) groupList.size());

			res.add(new PercepFeatureData<Integer>(
					this.fireSites.indexOf(site), featureVec));
		}
		return res;
	}

	private void addElementToVec(List<Double> vec, double[] mat) {
		for (double d : mat) {
			vec.add(d);
		}
	}
}
