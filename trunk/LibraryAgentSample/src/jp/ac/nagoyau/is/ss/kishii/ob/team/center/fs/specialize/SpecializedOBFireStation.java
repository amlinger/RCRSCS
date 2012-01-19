package jp.ac.nagoyau.is.ss.kishii.ob.team.center.fs.specialize;

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
import jp.ac.nagoyau.is.ss.kishii.ob.primitive.FireGroup;
import jp.ac.nagoyau.is.ss.kishii.ob.primitive.FireSite;
import jp.ac.nagoyau.is.ss.kishii.ob.primitive.SuntoriBuilding;
import jp.ac.nagoyau.is.ss.kishii.ob.primitive.Util;
import jp.ac.nagoyau.is.ss.kishii.ob.route.ASter;
import jp.ac.nagoyau.is.ss.kishii.ob.team.center.AbstractOBCenter;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.information.UnpassableInformation;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.task.fb.ExtinguishAreaTaskMessage;
import rescuecore2.messages.Command;
import rescuecore2.misc.Pair;
import rescuecore2.standard.entities.FireBrigade;
import rescuecore2.standard.entities.FireStation;
import rescuecore2.standard.entities.StandardEntity;
import rescuecore2.standard.entities.StandardEntityURN;
import rescuecore2.worldmodel.ChangeSet;
import rescuecore2.worldmodel.EntityID;

public class SpecializedOBFireStation extends AbstractOBCenter<FireStation> {
	List<FireGroup> fireCluster;
	Map<EntityID, Map<FireGroup, Double>> groupValueMapForEachFB;
	Set<EntityID> sendBuildingIDSet;
	private Map<EntityID, SuntoriNode> nodeMap;
	private Map<EntityID, List<EntityID>> fbReachableMap;

	private Set<EntityID> checkedFreeFBSet;
	private int[] addAgentNumMat;

	@Override
	protected void postConnect() {
		super.postConnect();
		this.fireCluster = null;
		this.groupValueMapForEachFB = new HashMap<EntityID, Map<FireGroup, Double>>();
		this.sendBuildingIDSet = new HashSet<EntityID>();
		if (this.router instanceof ASter) {
			this.nodeMap = ((ASter) this.router).getNodeMap();
		}
		this.fbReachableMap = new HashMap<EntityID, List<EntityID>>();
		this.checkedFreeFBSet = null;
		this.addAgentNumMat = null;
	}

	@Override
	protected void arrangeMessage(ChangeSet changed) {
		super.arrangeMessage(changed);
		for (UnpassableInformation ui : this.unpassableInfoList) {
			this.router.setUnpassable(ui.getFromAreaID(), ui.getToAreaID());
		}
	}

	@Override
	protected void thinking2(int time, ChangeSet changed,
			Collection<Command> heard) {
		this.groupValueMapForEachFB = new HashMap<EntityID, Map<FireGroup, Double>>();
		this.sendBuildingIDSet = new HashSet<EntityID>();

		this.fireCluster = Util2.getFireGroupList(this.model, this.sbList);
		System.out.println("FireGroup:" + this.fireCluster.size());
		System.out.println(Util.getBurningFireSite(this.fireSites).size());
		this.selectTargetZone(time);
		if (this.sendBuildingIDSet.size() > 0) {
			List<EntityID> sendBuildingIDList = new ArrayList<EntityID>();
			sendBuildingIDList.addAll(this.sendBuildingIDSet);
			if (sendBuildingIDList.size() > 0) {
				for (EntityID id : this.fireBrigadeList) {
					this.addMessage(new ExtinguishAreaTaskMessage(time,
							getID(), id, sendBuildingIDList));
				}
				System.out.println(sendBuildingIDList);
			}
		}
	}

	/**
	 * 優先順位の高い消火対象を決定する。<br>
	 * 担当エリア内の火災かどうかを評価に加えてもいいかも
	 */
	private void selectTargetZone(int time) {
		List<EntityID> freeFB = new ArrayList<EntityID>();

		// 動けるFBの計算(FSが水の残量まで考慮する必要は無く，あくまで動けることが必要)
		for (StandardEntity se : this.model
				.getEntitiesOfType(StandardEntityURN.FIRE_BRIGADE)) {
			FireBrigade fb = (FireBrigade) se;
			if (!this.refugeList.contains(fb.getPosition())
					&& fb.getBuriedness2() == 0) {
				freeFB.add(fb.getID());
			}
		}

		// FireSiteの更新作業
		for (FireSite fs : this.fireSites) {
			fs.setEffectedFireSiteCount(0);
			if (fs.getBurningBuilding().size() > 0) {
				for (FireSite nfs : fs.getSiteWithin150m()) {
					nfs.setEffectedFireSiteCount(nfs.getEffectedFireSiteCount() + 1);
				}
			}
		}

		if (freeFB.size() == 0) {
			return;
		}

		if (this.fireCluster.size() == 0) {
			return;
		}

		// それぞれのFBの移動可能エリアの計算
		this.fbReachableMap = new HashMap<EntityID, List<EntityID>>();
		for (EntityID id : freeFB) {
			FireBrigade fb = (FireBrigade) this.model.getEntity(id);
			this.fbReachableMap.put(id, this.calcReachable(fb.getPosition()));
		}

		this.checkedFreeFBSet = new HashSet<EntityID>();

		// FireGroupの評価
		this.evaluateFireGroup();
		int sendAgentNum = freeFB.size() / this.fireCluster.size();
		// 各タスクの送信先エージェント数を決定
		int[] agentNumMat = new int[this.fireCluster.size()];
		this.addAgentNumMat = new int[this.fireCluster.size()];
		for (int i = 0; i < agentNumMat.length; i++) {
			agentNumMat[i] = sendAgentNum;
		}
		int surpluss = freeFB.size() % sendAgentNum;
		for (int i = 0; i < surpluss; i++) {
			agentNumMat[i] = agentNumMat[i] + 1;
		}

		LOOP: for (EntityID id : this.fireBrigadeList) {
			List<EntityID> ffbList = new ArrayList<EntityID>(freeFB);
			// クラスタの評価値でソート
			Collections.sort(this.fireCluster,
					new HashValueComparator<FireGroup>(
							this.groupValueMapForEachFB.get(id)));
			// FireSiteの評価(FireGroupに均等にエージェントを割り振り)
			for (int i = 0; i < this.fireCluster.size(); i++) {
				FireGroup fg = this.fireCluster.get(i);

				List<FireSite> candidate = new ArrayList<FireSite>();
				Map<FireSite, Double> siteValueMap = new HashMap<FireSite, Double>();
				for (SuntoriBuilding sb : fg.getAllFires()) {
					FireSite tmp = this.siteMap.get(sb);
					if (!candidate.contains(tmp) && tmp != null) {
						candidate.add(tmp);
						double value = tmp.getLivingFireSiteAreaAsEffectSites();
						value *= (double) (tmp.totalLivingArea()
								/ tmp.toatlBuildingArea() * 3);
						siteValueMap.put(tmp, value);
					}
				}
				// FireSiteを評価値でソート
				Collections.sort(candidate, new HashValueComparator<FireSite>(
						siteValueMap));
				// 消火人数に合わせてどのエリアを消火するかを決定
				for (FireSite fs : candidate) {
					List<SuntoriBuilding> fires = new ArrayList<SuntoriBuilding>(
							fs.getBurningBuilding());
					if (fires.size() == 0) {
						continue;
					}
					HashMap<SuntoriBuilding, Double> fireValueMap = new HashMap<SuntoriBuilding, Double>();
					for (SuntoriBuilding sb : fs.getBuildings()) {
						double value;
						if (sb.getConnectBurnableCount() >= 1) {
							value = sb.simpleCalculateConnectValue() + 100000000;
							// TODO burningTimeを入れる必要があるかも。(燃え立ては消しやすいはず)
						} else {
							value = Double.MIN_VALUE;
						}
						fireValueMap.put(sb, value);
					}
					// 建物を評価値でソート
					Collections.sort(fires,
							new HashValueComparator<SuntoriBuilding>(
									fireValueMap));
					if (this.evaluateBuilding(time, id, fires, ffbList,
							agentNumMat[i], i)) {
						continue LOOP;
					}
				}
			}
		}
		System.out.println(time + "::::" + this.sendBuildingIDSet.size());
	}

	private void evaluateFireGroup() {
		for (EntityID id : this.fireBrigadeList) {
			FireBrigade fb = (FireBrigade) this.model.getEntity(id);
			Pair<Integer, Integer> location = fb.getLocation(this.model);
			// 火災クラスタの評価
			Map<FireGroup, Double> groupValueMap = new HashMap<FireGroup, Double>();
			for (FireGroup fg : this.fireCluster) {
				// クラスタ内での火災発生位置に関する評価
				int selfDistanceValue = (int) Math
						.pow(fg.distance(location.first(), location.second()) / 1000,
								2);
				// クラスタが大きければその分重要である可能性が高いが消せない可能性が高くなる
				double value = (double) fg.totalArea() / 10 + selfDistanceValue;
				FireSite nearestFS = fg.getNearestFireSite(this.fireSites,
						this.model);
				int livingArea = nearestFS.getLivingFireSiteAreaAsEffectSites();
				// クラスタの重要度に対する、燃え残っている建物の割合で重要度を算出
				value = Math.sqrt(livingArea) / (value + 1);
				groupValueMap.put(fg, value);
			}
			this.groupValueMapForEachFB.put(id, groupValueMap);
		}
	}

	private boolean evaluateBuilding(int time, EntityID fbID,
			List<SuntoriBuilding> fires, List<EntityID> freeFB, int num,
			int index) {
		for (SuntoriBuilding sb : fires) {
			if (freeFB.isEmpty()) {
				break;
			}
			// 動けるFBを目標の建物までの距離でソート
			Collections.sort(freeFB, new DistanceComparator(this.model, sb));
			int need = this.needFBNum(sb, freeFB.size());
			if (sb.getConnectBurnableCount() == 0) {
				need = 0;
			}
			for (int i = 0; i < need; i++) {
				if (freeFB.size() == 0) {
					return false;
				}
				if (freeFB.get(0).equals(fbID)) {
					if (this.fbReachableMap.get(fbID).contains(sb.getID())
							&& this.addAgentNumMat[index] < num
							&& !this.checkedFreeFBSet.contains(fbID)) {
						// List<EntityID> bIDList = new ArrayList<EntityID>();
						// bIDList.add(sb.getID());
						// this.addMessage(new ExtinguishAreaTaskMessage(time,
						// getID(),
						// fbID, bIDList));
						this.sendBuildingIDSet.add(sb.getID());
						this.addAgentNumMat[index] = this.addAgentNumMat[index] + 1;
						this.checkedFreeFBSet.add(fbID);
						// this.targetCluster = fg;
						// this.targetSite = fs;
						// this.targetBuilding = sb;
						return true;
					} else {
						return false;
					}
				} else {
					freeFB.remove(0);
				}
			}
		}
		return false;
	}

	public int needFBNum(SuntoriBuilding sb, int max) {
		int res = 0;
		int area = sb.getGroundArea();
		if (area < 200) {
			// res = 1;
			res = max / 3;
		} else if (area < 400 * this.fireBrigadeList.size()) {
			// res = 2;
			res = max / 2;
		} else if (area < 1800 * this.fireBrigadeList.size()) {
			res = (max * 2) / 3;
		}
		return res;
	}

	@Override
	protected EnumSet<StandardEntityURN> getRequestedEntityURNsEnum() {
		return EnumSet.of(StandardEntityURN.FIRE_STATION);
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
