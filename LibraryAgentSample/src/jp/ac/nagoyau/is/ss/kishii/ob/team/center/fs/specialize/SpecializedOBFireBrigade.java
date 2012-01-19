package jp.ac.nagoyau.is.ss.kishii.ob.team.center.fs.specialize;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.ac.nagoyau.is.ss.kishii.ob.primitive.District;
import jp.ac.nagoyau.is.ss.kishii.ob.primitive.FireSite;
import jp.ac.nagoyau.is.ss.kishii.ob.primitive.SuntoriBuilding;
import jp.ac.nagoyau.is.ss.kishii.ob.primitive.Util;
import jp.ac.nagoyau.is.ss.kishii.ob.route.ASter;
import jp.ac.nagoyau.is.ss.kishii.ob.team.agent.fb.AbstractOBFireBrigade;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.RCRSCSMessage;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.information.UnpassableInformation;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.task.fb.ExtinguishAreaTaskMessage;
import rescuecore2.messages.Command;
import rescuecore2.misc.Pair;
import rescuecore2.standard.entities.Area;
import rescuecore2.standard.entities.Blockade;
import rescuecore2.standard.entities.Building;
import rescuecore2.standard.entities.Edge;
import rescuecore2.standard.entities.FireBrigade;
import rescuecore2.standard.entities.StandardEntity;
import rescuecore2.standard.entities.StandardEntityURN;
import rescuecore2.worldmodel.ChangeSet;
import rescuecore2.worldmodel.EntityID;

public class SpecializedOBFireBrigade extends AbstractOBFireBrigade {
	protected List<SuntoriBuilding> sbList;
	protected List<FireSite> fireSites;
	protected Map<SuntoriBuilding, FireSite> siteMap;
	protected Map<EntityID, FireSite> idSiteMap;
	// protected List<FireGroup> fireCluster;
	// private FireGroup targetCluster;
	// private FireSite targetSite;
	private SuntoriBuilding targetBuilding;
	// private Map<SuntoriBuilding, Double> targetValueMap;
	// private Map<EntityID, BuildingData> buildingDataMap;
	// private Map<Integer, FireSiteData> fireSiteDataMap;
	private ExtinguishAreaTaskMessage task;

	private Map<EntityID, SuntoriBuilding> sbMap;

	District[] dists;

	public SpecializedOBFireBrigade() {
		super();
		// this.extinguishCandidateMap = new HashMap<EntityID,
		// List<ExtinguishPoint>>();
		this.sbList = new ArrayList<SuntoriBuilding>();
		this.fireSites = new ArrayList<FireSite>();
		this.siteMap = new HashMap<SuntoriBuilding, FireSite>();
		this.idSiteMap = new HashMap<EntityID, FireSite>();
		// this.fireCluster = new ArrayList<FireGroup>();
		// this.targetCluster = null;
		// this.targetSite = null;
		this.targetBuilding = null;
		// this.targetValueMap = new HashMap<SuntoriBuilding, Double>();
		// this.buildingDataMap = new HashMap<EntityID, BuildingData>();
		// this.fireSiteDataMap = new HashMap<Integer, FireSiteData>();
		this.task = null;

		this.sbMap = new HashMap<EntityID, SuntoriBuilding>();
		this.dists = new District[9];
		for (int i = 0; i < this.dists.length; i++) {
			this.dists[i] = new District();
		}
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
			this.sbMap.put(sb.getID(), sb);
		}

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
			System.out.println(this.myID + "//////"
					+ this.task.getTargetAreaList());
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

			// 命令に殉ずる
			List<SuntoriBuilding> candidateSBList = new ArrayList<SuntoriBuilding>();
			for (EntityID id : this.task.getTargetAreaList()) {
				if (this.reachableAreaList.contains(id)) {
					candidateSBList.add(this.sbMap.get(id));
				}
			}

			if (candidateSBList == null || candidateSBList.size() == 0) {
				return;
			}
			// 各タスクの送信先エージェント数を決定
			int sendAgentNum = freeFB.size() / candidateSBList.size();
			int[] agentNumMat = new int[candidateSBList.size()];
			for (int i = 0; i < agentNumMat.length; i++) {
				agentNumMat[i] = sendAgentNum;
			}
			int surpluss = freeFB.size() % sendAgentNum;
			for (int i = 0; i < surpluss; i++) {
				agentNumMat[i] = agentNumMat[i] + 1;
			}

			for (int j = 0; j < candidateSBList.size(); j++) {
				SuntoriBuilding sb = candidateSBList.get(j);
				if (freeFB.isEmpty()) {
					break;
				}
				// 動けるFBを目標の建物までの距離でソート
				Collections.sort(freeFB, new DistanceComparator(model, sb));
				// int need = this.needFBNum(sb, freeFB.size());
				int need = agentNumMat[j];
				if (sb.getConnectBurnableCount() == 0) {
					need = 0;
				}
				for (int i = 0; i < need; i++) {
					if (freeFB.size() == 0) {
						return;
					}
					if (freeFB.get(0).equals(this.myID)) {
						// this.targetCluster = fg;
						// this.targetSite = fs;
						this.targetBuilding = sb;
					} else {
						freeFB.remove(0);
					}
				}
			}
			if (this.targetBuilding != null && this.targetBuilding.isBurning()) {
				if (this.extinguish(arg0, this.targetBuilding.getID(),
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
			if (target != null && target.isOnFire()) {
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
	protected void checkBlockadeInTheWay(int time, ChangeSet changed,
			EntityID fromId, EntityID toId) {
		if (!this.myPosition.equals(fromId)) {// 自分の現在位置がfromIdと一致することが最低条件
			return;
		}
		// 検索対象の閉塞を計算
		List<Blockade> fromBlockList = new ArrayList<Blockade>();
		List<Blockade> toBlockList = new ArrayList<Blockade>();
		for (EntityID id : changed.getChangedEntities()) {
			StandardEntity se = this.model.getEntity(id);
			if (se instanceof Blockade) {
				Blockade b = (Blockade) se;
				EntityID position = b.getPosition();
				if (position.equals(fromId)) {
					fromBlockList.add(b);
				} else if (position.equals(toId)) {
					toBlockList.add(b);
				}
			}
		}
		Area fromArea = (Area) this.model.getEntity(fromId);
		Area toArea = (Area) this.model.getEntity(toId);
		Pair<Integer, Integer> toAreaLocation = toArea.getLocation(this.model);
		// 接続エッジの取得
		Edge edge = fromArea.getEdgeTo(toId);
		// 接続エッジの中点計算
		Pair<Integer, Integer> centerOfEdge = Util.getCenterOf(
				edge.getStartX(), edge.getStartY(), edge.getEndX(),
				edge.getEndY());
		EntityID blockade = null;
		if ((blockade = Util2.isInOrCrossingBlockade(this.myLocation,
				centerOfEdge, fromBlockList)) != null) {
			this.router.setUnpassable(fromId, toId);
			this.addMessage(new UnpassableInformation(time, this.myID, fromId,
					toId, blockade));
		} else if ((blockade = Util2.isInOrCrossingBlockade(centerOfEdge,
				toAreaLocation, toBlockList)) != null) {
			this.router.setUnpassable(fromId, toId);
			this.addMessage(new UnpassableInformation(time, this.myID, fromId,
					toId, blockade));
		} else {
			List<Blockade> blockList = new ArrayList<Blockade>(fromBlockList);
			blockList.addAll(toBlockList);
			if ((blockade = Util2.isInOrCrossingBlockade(this.myLocation,
					toAreaLocation, blockList)) != null) {
				this.router.setUnpassable(fromId, toId);
				this.addMessage(new UnpassableInformation(time, this.myID,
						fromId, toId, blockade));
			} else {
				this.router.setPassable(fromId, toId);
			}
		}
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
