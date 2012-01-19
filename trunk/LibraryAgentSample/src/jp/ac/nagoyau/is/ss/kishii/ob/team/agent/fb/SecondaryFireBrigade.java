package jp.ac.nagoyau.is.ss.kishii.ob.team.agent.fb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.ac.nagoyau.is.ss.kishii.ob.primitive.FireSite;
import jp.ac.nagoyau.is.ss.kishii.ob.primitive.SuntoriBuilding;
import jp.ac.nagoyau.is.ss.kishii.ob.primitive.Util;
import jp.ac.nagoyau.is.ss.kishii.ob.route.ASter;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.RCRSCSMessage;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.task.fb.ExtinguishAreaTaskMessage;
import rescuecore2.messages.Command;
import rescuecore2.standard.entities.Building;
import rescuecore2.standard.entities.StandardEntity;
import rescuecore2.worldmodel.ChangeSet;
import rescuecore2.worldmodel.EntityID;

public class SecondaryFireBrigade extends AbstractOBFireBrigade {
	protected List<SuntoriBuilding> sbList;
	protected List<FireSite> fireSites;
	protected Map<SuntoriBuilding, FireSite> siteMap;
	protected Map<EntityID, FireSite> idSiteMap;
	// protected List<FireGroup> fireCluster;
	// private FireGroup targetCluster;
	private FireSite targetSite;
	private SuntoriBuilding targetBuilding;
	private Map<SuntoriBuilding, Double> targetValueMap;
	// private Map<EntityID, BuildingData> buildingDataMap;
	// private Map<Integer, FireSiteData> fireSiteDataMap;
	private ExtinguishAreaTaskMessage task;
	private List<EntityID> movableAreaList;
	private List<EntityID> allUnvisitedRoad;
	
	public SecondaryFireBrigade() {
		super();
		// this.extinguishCandidateMap = new HashMap<EntityID,
		// List<ExtinguishPoint>>();
		this.sbList = new ArrayList<SuntoriBuilding>();
		this.fireSites = new ArrayList<FireSite>();
		this.siteMap = new HashMap<SuntoriBuilding, FireSite>();
		this.idSiteMap = new HashMap<EntityID, FireSite>();
		// this.fireCluster = new ArrayList<FireGroup>();
		// this.targetCluster = null;
		this.targetSite = null;
		this.targetBuilding = null;
		this.targetValueMap = new HashMap<SuntoriBuilding, Double>();
		// this.buildingDataMap = new HashMap<EntityID, BuildingData>();
		// this.fireSiteDataMap = new HashMap<Integer, FireSiteData>();
		this.task = null;

		this.movableAreaList = new ArrayList<EntityID>();

		this.allUnvisitedRoad = new ArrayList<EntityID>();
	}

	@Override
	protected void postConnect() {
		super.postConnect();
		this.sbList = SuntoriBuilding.createSuntoriBuildings(this.model);
		this.fireSites = FireSite.createFireSites(this.model, sbList);
		for (int i = 0; i < this.fireSites.size(); i++) {
			FireSite fs0 = this.fireSites.get(i);
			for (int j = i; j < this.fireSites.size(); j++) {
				FireSite fs1 = this.fireSites.get(j);
				if (fs0.distance(fs1, this.model) < 150 * 000) {
					fs0.getSiteWithin150m().add(fs1);
					fs1.getSiteWithin150m().add(fs0);
				}
			}
			for (SuntoriBuilding sb : fs0.getBuildings()) {
				this.siteMap.put(sb, fs0);
				this.idSiteMap.put(sb.getID(), fs0);
			}
		}
		for (SuntoriBuilding sb : this.sbList) {
			FireSite fs = this.siteMap.get(sb.getID());
			for (SuntoriBuilding sb2 : sb.getDirectConnectBuilding()) {
				FireSite fs2 = this.siteMap.get(sb2.getID());
				if (this.fireSites.indexOf(fs) != this.fireSites.indexOf(fs2)) {
					fs.getConnectionFireSite().add(fs2);
					fs2.getConnectionFireSite().add(fs);
				}
			}
		}
	}

	@Override
	protected void thinking2(int arg0, ChangeSet arg1, Collection<Command> arg2) {
		super.thinking2(arg0, arg1, arg2);
		// if (this.myID.getValue() == this.fireBrigadeList.get(0).getValue()) {
		// System.out.println("receive:" + this.receivedMessageList.size());
		// for (RCRSCSMessage message : this.receivedMessageList) {
		// if (message instanceof TransferInformation
		// && this.policeForceList
		// .contains(((TransferInformation) message)
		// .getAgentID())) {
		// System.out.println("come transfer:"
		// + ((TransferInformation) message).getPathway());
		// }
		// }
		// }
		if (this.task != null) {// タスクを受信
			// TODO 命令に殉ずるったい
			Set<FireSite> candidateSet = new HashSet<FireSite>();
			for (EntityID id : this.task.getTargetAreaList()) {
				candidateSet.add(idSiteMap.get(id));
			}
			System.out.println(arg0 + ":" + this.myID + "::"
					+ this.task.getTargetAreaList());
			// 自分から一番近い対象FireSiteを選択
			double min = Double.MAX_VALUE;
			FireSite site = null;
			for (FireSite fs : candidateSet) {
				// 消火対象はある？
				if (fs.getBurningBuilding().size() == 0) {
					continue;
				}
				// 消火対象には到達可能？
				boolean isReachable = false;
				for (SuntoriBuilding sb : fs.getBurningBuilding()) {
					if (this.reachableAreaList.contains(sb.getID())) {
						isReachable = true;
						break;
					}
				}
				if (!isReachable) {
					continue;
				}
				// 上記条件をクリアしたものの中から一番近いものを選択
				double distance = Util.getDistance(this.myLocation.first(),
						this.myLocation.second(), fs.getX(this.model),
						fs.getY(this.model));
				if (distance <= min) {
					min = distance;
					site = fs;
				}
			}
			if (site != null) {
				this.targetSite = site;// ターゲット確定
			}
		}

		// if (this.targetSite != null) {
		// if (this.targetSite.getBurningBuilding().size() == 0) {
		// this.targetSite = null;
		// }
		// }

		if (this.targetSite != null) {
			// 燃えている建物の中から自分に一番近い建物を選択
			double min = Double.MAX_VALUE;
			SuntoriBuilding target = null;
			for (SuntoriBuilding sb : this.targetSite.getBurningBuilding()) {
				double distance = Util.getDistance(this.myLocation,
						sb.getLocation(this.model));
				if (distance <= min
						&& this.reachableAreaList.contains(sb.getID())) {
					min = distance;
					target = sb;
				}
			}
			if (target != null) {
				if (this.extinguish(arg0, target.getID(),
						this.constants.EXTINGUISHABLE_POWER)) {
					return;
				}
			}
		}
		// タスクが存在しないとき
		// 現在認識しているものの中で一番近い建物の消火を計算
		double distance = Double.MAX_VALUE;
		Building build = null;
		for (SuntoriBuilding sb : this.sbList) {
			if (sb.isBurning() && reachableAreaList.contains(sb.getID())) {
				double tmp = Util.getDistance(this.myLocation,
						sb.getLocation(this.model));
				if (tmp <= distance) {
					distance = tmp;
					build = sb.getBuilding();
				}
			}
		}
		if (build == null) {
			double min = Double.MAX_VALUE;
			Building target = null;
			List<Building> visibleBuilding = new ArrayList<Building>();
			for (EntityID id : this.visibles) {
				StandardEntity se = this.model.getEntity(id);
				if (se instanceof Building) {
					visibleBuilding.add((Building) se);
				}
			}
			for (Building b : visibleBuilding) {
				distance = Util.getDistance(this.myLocation,
						b.getLocation(this.model));
				if (distance <= min
						&& this.reachableAreaList.contains(b.getID())
						&& b.isOnFire()) {
					min = distance;
					target = b;
				}
			}
			if (target != null) {
				if (this.extinguish(arg0, target.getID(),
						this.constants.EXTINGUISHABLE_POWER)) {
					return;
				}
			}

			List<EntityID> charge = new ArrayList<EntityID>(this.chargeRoads);
			charge.retainAll(this.reachableAreaList);
			if (charge.size() > 0 && super.moveToAnyGoal(arg0, charge)) {
			} else {
				// super.move(arg0, this.reachableAreaList.get(this.random
				// .nextInt(this.reachableAreaList.size())));
				List<EntityID> sendPath = new ArrayList<EntityID>();
				sendPath.add(this.roadList.get(this.random
						.nextInt(this.roadList.size())));
				if (this.router instanceof ASter) {
					super.move(arg0, ((ASter) this.router)
							.getRouteIncludeUnpassable(this.myPosition,
									this.roadList.get(this.random
											.nextInt(this.roadList.size()))));
				} else {
					List<EntityID> reachable = new ArrayList<EntityID>(
							this.allUnvisitedRoad);
					reachable.retainAll(this.reachableAreaList);
					super.move(arg0, reachable.get(this.random
							.nextInt(reachable.size())));
				}
			}
			// super.rest(arg0);
			return;
		}
		super.extinguish(arg0, build.getID(),
				this.constants.EXTINGUISHABLE_POWER);
		return;
	}

	@Override
	protected void arrangeTasks(int time) {
		this.task = null;
		for (RCRSCSMessage message : this.receivedMessageList) {
			if (message instanceof ExtinguishAreaTaskMessage) {
				this.task = (ExtinguishAreaTaskMessage) message;
				break;
			}
		}
		if (this.task == null) {
			return;
		}
	}

}
