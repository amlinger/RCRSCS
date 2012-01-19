package jp.ac.nagoyau.is.ss.kishii.ob.team.agent.fb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.ac.nagoyau.is.ss.kishii.ob.primitive.Action;
import jp.ac.nagoyau.is.ss.kishii.ob.primitive.ExtinguishPoint;
import jp.ac.nagoyau.is.ss.kishii.ob.primitive.Point;
import jp.ac.nagoyau.is.ss.kishii.ob.primitive.Util;
import jp.ac.nagoyau.is.ss.kishii.ob.team.agent.AbstractOBPlatoonAgent;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.information.FireBrigadeInformation;
import rescuecore2.messages.Command;
import rescuecore2.standard.entities.Area;
import rescuecore2.standard.entities.Building;
import rescuecore2.standard.entities.Edge;
import rescuecore2.standard.entities.FireBrigade;
import rescuecore2.standard.entities.Road;
import rescuecore2.standard.entities.StandardEntity;
import rescuecore2.standard.entities.StandardEntityURN;
import rescuecore2.worldmodel.ChangeSet;
import rescuecore2.worldmodel.EntityID;

public abstract class AbstractOBFireBrigade extends
		AbstractOBPlatoonAgent<FireBrigade> {
	protected int water;
	protected int extinguishDistance;
	/**
	 * ある建物を消火することができる可能性のあるエリアのリストのマップ<br>
	 * かつ，建物を視認することができる場所である必要がある．
	 */
	protected Map<EntityID, List<ExtinguishPoint>> extinguishCandidateMap;
	protected List<EntityID> unvisitedRoads;
	protected List<EntityID> allUnvisitedRoad;

	public AbstractOBFireBrigade() {
		super();
		this.water = 0;
		this.extinguishCandidateMap = new HashMap<EntityID, List<ExtinguishPoint>>();
		this.unvisitedRoads = null;
		this.allUnvisitedRoad = new ArrayList<EntityID>();
	}

	@Override
	protected void thinking2(int arg0, ChangeSet arg1, Collection<Command> arg2) {
		this.water = this.me().getWater2();
		if (this.refugeList.contains(this.myPosition)) {
			if (this.water <= this.constants.TANK_MAX * 0.8d) {
				this.rest(arg0);
				return;
			}
		} else if (this.water <= this.constants.EXTINGUISHABLE_POWER) {
			this.moveToRefuge(arg0);
			return;
		}
		if (this.unvisitedRoads.size() == 0) {
			this.unvisitedRoads = new ArrayList<EntityID>(this.roadList);
			// this.unvisitedRoads = new ArrayList<EntityID>(
			// this.reachableAreaList);
			Collections.shuffle(this.unvisitedRoads);
		}
	}

	@Override
	protected void postConnect() {
		super.postConnect();
		this.extinguishDistance = this.constants.RANGE_OF_VIEW;
		if (this.constants.RANGE_OF_VIEW > this.constants.EXTINGUISHABLE_DISTANCE) {
			this.extinguishDistance = this.constants.EXTINGUISHABLE_DISTANCE;
		}
		long start = System.currentTimeMillis();
		calculateExtinguishCandidate();
		System.out.println("Extinguish Point is Calculated : "
				+ (System.currentTimeMillis() - start) + "[ms]");
		this.allUnvisitedRoad = new ArrayList<EntityID>(this.roadList);
	}

	@Override
	protected void arrangeChargeArea() {
		this.unvisitedRoads = new ArrayList<EntityID>(this.chargeRoads);
		Collections.shuffle(this.chargeRoads);
	}

	/**
	 * ある建物を消火することができるかもしれないエリアの計算<br>
	 * 建物の一部でも消火可能範囲内であれば消火はできるが， 消火されたことが確認できないほど消火可能範囲が大きい場合があるため，
	 * 閾値は視野範囲と消火可能範囲の内，小さい方を選択する．<br>
	 */
	private void calculateExtinguishCandidate() {
		for (EntityID id : this.buildingList) {
			Set<Area> candidate = new HashSet<Area>();
			Set<Area> buildingCandidate = new HashSet<Area>();
			Building building = (Building) this.model.getEntity(id);
			// TODO 指定範囲内であっても他の建物が邪魔で見えない可能性があるのでその計算が必要だった
			// for (Edge edge : building.getEdges()) {
			// for (StandardEntity se : this.model.getObjectsInRange(
			// edge.getEndX(), edge.getEndY(),
			// this.extinguishDistance / 2)) {
			// if (se instanceof Road) {
			// candidate.add((Road) se);
			// } else if (se instanceof Building) {
			// buildingCandidate.add((Building) se);
			// }
			// }
			// for (StandardEntity se : this.model.getObjectsInRange(
			// (edge.getStartX() + edge.getEndX()) / 2,
			// (edge.getStartY() + edge.getEndY()) / 2,
			// this.extinguishDistance / 2)) {
			// if (se instanceof Road) {
			// candidate.add((Road) se);
			// } else if (se instanceof Building) {
			// buildingCandidate.add((Building) se);
			// }
			// }
			// }
			// 消火可能な道路がない場合(しかたがないので隣接するエリアを候補に追加)
			if (candidate.size() == 0) {
				List<EntityID> neighbours = building.getNeighbours();
				if (neighbours != null) {
					for (EntityID neighbour : neighbours) {
						StandardEntity se = this.model.getEntity(neighbour);
						if (se instanceof Road) {
							candidate.add((Area) se);
						} else if (se instanceof Building) {
							buildingCandidate.add((Area) se);
						}
					}
					if (candidate.size() == 0 && buildingCandidate.size() != 0) {
						candidate.addAll(buildingCandidate);
					}
				}
				// candidate.addAll(buildingCandidate);
			}
			List<ExtinguishPoint> candidateList = new ArrayList<ExtinguishPoint>();
			for (Area area : candidate) {
				ExtinguishPoint ep = this.calculateExtinguishPointCentroid(
						building, area);
				if (ep != null) {
					candidateList.add(ep);
				}
			}
			if (candidateList.size() > 0) {
				this.extinguishCandidateMap.put(id, candidateList);
			}
		}
	}

	/**
	 * 消火可能エリア上で，消火可能範囲に対する近似的な重心を計算する．<br>
	 * より効果的に消火ポイントに到達するための初期計算用メソッド．<br>
	 * 建物の各Edgeの端や中点を中心とする円（半径＝extinguishDistance）と交差する点と，
	 * 各円に含まれる点を収集し，その重心をそのエリアの消火目標地点として設定する．
	 * この座標がそのエリアのあるEdgeのとき，その途中に接合点があると移動の際引っかかってしまう可能性があるので注意
	 * 
	 * @param building
	 * @param area
	 */
	private ExtinguishPoint calculateExtinguishPointCentroid(Building building,
			Area area) {
		Set<Point> pointSet = new HashSet<Point>();
		int[] areaApexes = area.getApexList();
		List<Edge> areaEdges = area.getEdges();
		for (Edge edge : building.getEdges()) {
			int a1 = edge.getEndX();
			int b1 = edge.getEndY();
			int a2 = (edge.getStartX() + edge.getEndX()) / 2;
			int b2 = (edge.getStartY() + edge.getEndY()) / 2;

			// まずは円内にある点を探す．
			for (int i = 0; i < areaApexes.length; i += 2) {
				if (Util.getDistance(a1, b1, areaApexes[i], areaApexes[i + 1]) <= this.extinguishDistance) {
					pointSet.add(new Point(areaApexes[i], areaApexes[i + 1]));
				} else if (Util.getDistance(a2, b2, areaApexes[i],
						areaApexes[i + 1]) <= this.extinguishDistance) {
					pointSet.add(new Point(areaApexes[i], areaApexes[i + 1]));
				}
			}
			// for (int i = 0; i < areaApexes.length; i++) {
			//
			// }
			// 次に円との交点を求める．
			for (Edge ae : areaEdges) {
				long x1 = ae.getStartX();
				long y1 = ae.getStartY();
				long x2 = ae.getEndX();
				long y2 = ae.getEndY();
				long dx = x1 - x2;
				double c = ((double) (y1 - y2)) / dx;
				double d = ((double) ((x1 * y2) - (x2 * y1))) / dx;
				pointSet.addAll(Util.calculateCrossPointWithCircle(a1, b1,
						this.extinguishDistance, c, d, ae));
				pointSet.addAll(Util.calculateCrossPointWithCircle(a2, b2,
						this.extinguishDistance, c, d, ae));
			}
		}
		if (pointSet.size() == 0) {
			return null;
		}
		long cx = 0;
		long cy = 0;
		for (Point point : pointSet) {
			cx += point.getX();
			cy += point.getY();
		}
		cx /= pointSet.size();
		cy /= pointSet.size();
		return new ExtinguishPoint(area.getID(), (int) cx, (int) cy);
	}

	@Override
	protected void arrangeInformation(int time, ChangeSet changed) {
		super.addFireBrigadeInformation(time, this.me());
		super.arrangeInformation(time, changed);
		// 移動情報によるデータ更新
		if (this.transfer != null) {
			for (EntityID id : this.transfer) {
				this.unvisitedRoads.remove(id);
				this.allUnvisitedRoad.remove(id);
			}
		}
		if (this.allUnvisitedRoad.size() == 0) {
			this.allUnvisitedRoad = new ArrayList<EntityID>(this.roadList);
		}
	}

	@Override
	protected EnumSet<StandardEntityURN> getRequestedEntityURNsEnum() {
		return EnumSet.of(StandardEntityURN.FIRE_BRIGADE);
	}

	@Override
	protected void setSelfDataToMessage(int time) {
		FireBrigade self = this.me();
		if (self.isHPDefined() && self.isDamageDefined()
				&& self.isBuriednessDefined() && self.isPositionDefined()
				&& self.isWaterDefined()) {
			this.addMessage(new FireBrigadeInformation(time, self.getID(), self
					.getHP(), self.getDamage(), self.getBuriedness(), self
					.getWater(), self.getPosition()));
		}
		super.setTransferData(time);
	}

	private boolean isExtinguishable(EntityID target) {
		boolean res = false;
		Building building = (Building) this.model.getEntity(target);
		if (Util.getDistance(this.myLocation, building.getLocation(this.model)) <= this.extinguishDistance) {
			res = true;
		}
		return res;
	}

	/**
	 * 自分のいる場所が消火地点候補であるかを取得する<br>
	 * 自分の位置が消火地点候補にいない場合はnullを返す．
	 * 
	 * @param epList
	 *            消火地点候補
	 * @return
	 */
	private ExtinguishPoint getStandingExtinguishPointArea(
			List<ExtinguishPoint> epList) {
		ExtinguishPoint res = null;
		for (ExtinguishPoint ep : epList) {
			if (ep.getAreaID().equals(this.myPosition)) {
				res = ep;
				break;
			}
		}
		return res;
	}

	/**
	 * 消火を行う．
	 * 
	 * @param time
	 * @param target
	 *            消火対象
	 * @param power
	 * @return
	 */
	protected boolean extinguish(int time, EntityID target, int power) {
		boolean res = false;
		if (this.water == 0) {
			return this.moveToRefuge(time);
		}
		List<ExtinguishPoint> candidate = this.extinguishCandidateMap
				.get(target);
		if (this.visibles.contains(target) && isExtinguishable(target)) {
			super.sendExtinguish(time, target, power);
			this.previousAction = Action.EXTINGUISH;
			res = true;
			return res;
		}
		if (candidate != null && candidate.size() > 0) {
			ExtinguishPoint point = getStandingExtinguishPointArea(candidate);
			// // 消火可能エリアにいる
			if (point != null) {
				// 距離的に消火可能かつ見える
				if (isExtinguishable(target) && this.visibles.contains(target)) {
					super.sendExtinguish(time, target, power);
					this.previousAction = Action.EXTINGUISH;
					res = true;
				} else {
					// res = super.move(time, target);
					res = super.move(time, this.myPosition, point.getX(),
							point.getY());
				}
			} else {
				if (this.moveToExtiguishPoint(time, candidate)) {
					res = true;
				} else {
					res = super.moveToRefuge(time);
				}
			}
			// } else {
			// // 消せない
			// res = super.moveToRefuge(time);
			// }
		}
		return res;
	}

	protected boolean moveToExtiguishPoint(int time,
			List<ExtinguishPoint> candidate) {
		List<EntityID> col = new ArrayList<EntityID>();
		for (ExtinguishPoint ep : candidate) {
			col.add(ep.getAreaID());
		}
		return this.move(time, this.router.getRoute(this.myPosition, col));
	}
}
