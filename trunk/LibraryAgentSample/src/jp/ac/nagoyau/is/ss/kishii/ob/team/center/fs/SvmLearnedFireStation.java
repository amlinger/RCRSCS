package jp.ac.nagoyau.is.ss.kishii.ob.team.center.fs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.ac.nagoyau.is.ss.kishii.ob.network.SuntoriNode;
import jp.ac.nagoyau.is.ss.kishii.ob.primitive.District;
import jp.ac.nagoyau.is.ss.kishii.ob.primitive.FireSite;
import jp.ac.nagoyau.is.ss.kishii.ob.primitive.SuntoriBuilding;
import jp.ac.nagoyau.is.ss.kishii.ob.primitive.svm.SvmFeatureData;
import jp.ac.nagoyau.is.ss.kishii.ob.route.ASter;
import jp.ac.nagoyau.is.ss.kishii.ob.team.center.AbstractOBCenter;
import jp.ac.nagoyau.is.ss.kishii.ob.team.center.fs.specialize.DistanceComparator;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.information.UnpassableInformation;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.task.fb.ExtinguishAreaTaskMessage;
import libsvm.wrapper.Svm;
import libsvm.wrapper.SvmFeature;
import libsvm.wrapper.SvmFeatureVector;
import libsvm.wrapper.SvmModel;
import rescuecore2.messages.Command;
import rescuecore2.standard.entities.FireBrigade;
import rescuecore2.standard.entities.FireStation;
import rescuecore2.standard.entities.StandardEntityURN;
import rescuecore2.worldmodel.ChangeSet;
import rescuecore2.worldmodel.EntityID;

public class SvmLearnedFireStation extends AbstractOBCenter<FireStation> {
	SvmModel svmModel;
	private static final String MODEL_FILE_PATH = "learnedSvmModel.dat";

	List<EntityID> chargeFBList;

	District[] dists;

	private Map<EntityID, SuntoriNode> nodeMap;
	private Map<EntityID, List<EntityID>> fbReachableMap;

	public SvmLearnedFireStation() {
		super();
		try {
			this.svmModel = Svm.loadModel(MODEL_FILE_PATH);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.chargeFBList = new ArrayList<EntityID>();
		this.dists = new District[9];
		for (int i = 0; i < this.dists.length; i++) {
			this.dists[i] = new District();
		}
	}

	@Override
	protected void arrangeMessage(ChangeSet changed) {
		super.arrangeMessage(changed);
		for (UnpassableInformation ui : this.unpassableInfoList) {
			this.router.setUnpassable(ui.getFromAreaID(), ui.getToAreaID());
		}
	}

	@Override
	protected void postConnect() {
		super.postConnect();
		int myIndex = this.fireStationList.indexOf(this.myID);
		// for (int i = 0; i < this.fireBrigadeList.size(); i++) {
		// int index = i % this.fireStationList.size();
		// if (myIndex == index) {
		// this.chargeFBList.add(this.fireBrigadeList.get(index));
		// }
		// }
		this.chargeFBList.addAll(this.fireBrigadeList);

		double rangeX = this.model.getWorldBounds().second().first() / 3;
		double rangeY = this.model.getWorldBounds().second().second() / 3;
		for (int i = 0; i < this.fireSites.size(); i++) {
			FireSite fs = this.fireSites.get(i);
			int x = fs.getX(this.model);
			int y = fs.getY(this.model);
			if (x <= rangeX) {
				if (y <= rangeY) {
					this.dists[0].getSiteList().add(i);
				} else if (y <= rangeY * 2) {
					this.dists[1].getSiteList().add(i);
				} else {
					this.dists[2].getSiteList().add(i);
				}
			} else if (x <= rangeX * 2) {
				if (y <= rangeY) {
					this.dists[3].getSiteList().add(i);
				} else if (y <= rangeY * 2) {
					this.dists[4].getSiteList().add(i);
				} else {
					this.dists[5].getSiteList().add(i);
				}
			} else {
				if (y <= rangeY) {
					this.dists[6].getSiteList().add(i);
				} else if (y <= rangeY * 2) {
					this.dists[7].getSiteList().add(i);
				} else {
					this.dists[8].getSiteList().add(i);
				}
			}
		}
		rangeX /= 2;
		rangeY /= 2;
		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				this.dists[3 * x + y].setCenter((int) (rangeX * ((2 * x) + 1)),
						(int) (rangeY * ((2 * y) + 1)));
			}
		}
		
		if (this.router instanceof ASter) {
			this.nodeMap = ((ASter) this.router).getNodeMap();
		}
		this.fbReachableMap = new HashMap<EntityID, List<EntityID>>();
	}

	@Override
	protected EnumSet<StandardEntityURN> getRequestedEntityURNsEnum() {
		return EnumSet.of(StandardEntityURN.FIRE_STATION);
	}

	@Override
	protected void thinking2(int time, ChangeSet changed,
			Collection<Command> heard) {
		// それぞれのFBの移動可能エリアの計算
		this.fbReachableMap = new HashMap<EntityID, List<EntityID>>();
		for (EntityID id : this.chargeFBList) {
			FireBrigade fb = (FireBrigade) this.model.getEntity(id);
			this.fbReachableMap.put(id, this.calcReachable(fb.getPosition()));
		}

		List<SvmFeatureData<Integer>> fdList = this.getFireSiteFeatures();
		// List<EntityID> candidateList = new ArrayList<EntityID>();
		Set<Integer> candidateFireSet = new HashSet<Integer>();
		Set<EntityID> cSet = new HashSet<EntityID>();
		for (SvmFeatureData<Integer> fd : fdList) {
			int classId = (int) Svm.predict(this.svmModel, fd.getFeature());
			if (classId == 1) {
				cSet.add(new ArrayList<SuntoriBuilding>(this.fireSites.get(
						fd.getId()).getBuildings()).get(0).getID());
				candidateFireSet.add(fd.getId());
			}
		}
		// candidateList = new ArrayList<EntityID>(cSet);
		// Task分散
		// 消火対象となる区画の数を特定
		int distCount = 0;
		District[] candDists = new District[9];
		for (int i = 0; i < candDists.length; i++) {
			candDists[i] = new District();
			candDists[i].setCenter(this.dists[i].getCenter());
		}

		for (int i = 0; i < this.dists.length; i++) {
			District d = this.dists[i];
			List<Integer> cList = new ArrayList<Integer>(d.getSiteList());
			cList.retainAll(candidateFireSet);
			if (cList.size() > 0) {
				candDists[i].getSiteList().addAll(cList);
				distCount++;
			}
		}

		if (distCount > 0) {
			int fbIndex = 0;
			int sendAgentNum = this.chargeFBList.size() / distCount;
			if (sendAgentNum == 0) {

			} else {
				// 各タスクの送信先エージェント数を決定
				int[] agentNumMat = new int[distCount];
				for (int i = 0; i < distCount; i++) {
					agentNumMat[i] = sendAgentNum;
				}
				int surpluss = this.chargeFBList.size() % sendAgentNum;
				for (int i = 0; i < surpluss; i++) {
					agentNumMat[i] = agentNumMat[i] + 1;
				}
				int matIndex = 0;
				// 送信先エージェント数に応じてタスクメッセージを作成
				// for (int i = 0; i < candDists.length; i++) {
				// List<Integer> cands = candDists[i].getSiteList();
				// List<EntityID> candidateList = new ArrayList<EntityID>();
				// for (Integer index : cands) {
				// candidateList.add(new ArrayList<SuntoriBuilding>(
				// this.fireSites.get(index).getBuildings())
				// .get(0).getID());
				// }
				// if (cands.size() > 0) {
				// for (int j = 0; j < agentNumMat[matIndex]; j++) {
				// this.addMessage(new ExtinguishAreaTaskMessage(time,
				// this.myID, this.chargeFBList.get(j
				// + fbIndex), candidateList));
				// }
				// fbIndex += agentNumMat[matIndex];
				// matIndex++;
				// }
				// }
				// 近いエージェントから配置しないと全体的におかしくなる
				Set<EntityID> checked = new HashSet<EntityID>();
				matIndex = 0;
				for (int i = 0; i < candDists.length; i++) {
					District d = candDists[i];
					if (d.getSiteList().size() > 0) {
						int num = agentNumMat[matIndex];
						int count = 0;
						List<EntityID> fbList = new ArrayList<EntityID>(
								this.chargeFBList);
						Collections.sort(fbList, new DistanceComparator(
								this.model, d.getCenter()));
						List<EntityID> candidateList = new ArrayList<EntityID>();
						List<EntityID> siteBuildingList = new ArrayList<EntityID>();
						for (Integer index : d.getSiteList()) {
							candidateList.add(new ArrayList<SuntoriBuilding>(
									this.fireSites.get(index).getBuildings())
									.get(0).getID());
							for (SuntoriBuilding sb : this.fireSites.get(index)
									.getBuildings()) {
								siteBuildingList.add(sb.getID());
							}
						}
						for (int j = 0; j < fbList.size()
								&& checked.size() < this.chargeFBList.size()
								&& count < num; j++) {
							EntityID id = fbList.get(j);
							boolean send = false;
							for (EntityID bId : siteBuildingList) {
								if (this.fbReachableMap.get(id).contains(bId)) {
									send = true;
									break;
								}
							}
							if (!checked.contains(id) && send) {
								this.addMessage(new ExtinguishAreaTaskMessage(
										time, this.myID, id, candidateList));
								count++;
								checked.add(id);
							}
						}
						matIndex++;
					}
				}
			}
		}
	}

	/**
	 * FireSiteに関する特徴ベクトルを生成する．
	 * 
	 * @return
	 */
	protected List<SvmFeatureData<Integer>> getFireSiteFeatures() {
		List<SvmFeatureData<Integer>> res = new ArrayList<SvmFeatureData<Integer>>();
		for (FireSite site : this.fireSites) {
			if (site.getBurningBuilding().size() > 0) {
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
			    // 隣接するエリア数
				featureVec.add((double) site.getConnectionFireSite().size());
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
				// List<FireSite> burningFireSiteList =
				// Util.getBurningFireSite(site
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
				// burningFireSiteList =
				// Util.getBurningFireSite(this.fireSites);
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

				res.add(new SvmFeatureData<Integer>(this.fireSites
						.indexOf(site), this.createSvmFeatureVector(featureVec)));
			}
		}
		return res;
	}

	private void addElementToVec(List<Double> vec, double[] mat) {
		for (double d : mat) {
			vec.add(d);
		}
	}

	private SvmFeatureVector createSvmFeatureVector(List<Double> fList) {
		SvmFeatureVector res = new SvmFeatureVector();
		for (int i = 0; i < fList.size(); i++) {
			SvmFeature feature = new SvmFeature(i, fList.get(i));
			res.addFeature(feature);
		}
		return res;
	}

	/**
	 * 到達可能であると考えられるエリアの計算
	 */
	protected List<EntityID> calcReachable(EntityID positionID) {
		List<EntityID> res = new ArrayList<EntityID>();
		if (this.nodeMap != null) {
			Set<EntityID> set = new HashSet<EntityID>();
			Set<EntityID> finished = new HashSet<EntityID>();
			List<EntityID> unchecked = new ArrayList<EntityID>();
			unchecked.add(positionID);
			while (unchecked.size() > 0) {
				EntityID id = unchecked.get(0);
				set.add(id);
				unchecked.remove(id);
				finished.add(id);
				SuntoriNode node = this.nodeMap.get(id);
				if (node == null) {
					continue;
				}
				List<EntityID> passable = node.getPassable();
				passable.removeAll(finished);
				passable.removeAll(unchecked);
				unchecked.addAll(passable);
			}
			res = new ArrayList<EntityID>(set);
		} else {
			res = new ArrayList<EntityID>(this.areaList);
		}
		return res;
	}
}
