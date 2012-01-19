package jp.ac.nagoyau.is.ss.kishii.ob.primitive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rescuecore2.misc.Pair;
import rescuecore2.standard.entities.AmbulanceCentre;
import rescuecore2.standard.entities.Area;
import rescuecore2.standard.entities.Building;
import rescuecore2.standard.entities.Edge;
import rescuecore2.standard.entities.FireStation;
import rescuecore2.standard.entities.PoliceOffice;
import rescuecore2.standard.entities.Refuge;
import rescuecore2.standard.entities.StandardEntity;
import rescuecore2.standard.entities.StandardEntityURN;
import rescuecore2.standard.entities.StandardWorldModel;
import rescuecore2.worldmodel.EntityID;

/**
 * Suntori用の建物クラス<br>
 * FireSiteの生成などに使用する.
 * <p>
 * 保持するデータは<br>
 * <ul>
 * <li>自信を表すBuildingクラス</li>
 * <li>自信を円形と仮定したときに求められる半径</li>
 * <li>ある程度近くに存在する建物を保持しているセット</li>
 * <li>自信にほぼ隣接していると考えられる建物を保持しているリスト</li>
 * </ul>
 * 
 * @author takefumi
 * 
 */
public class SuntoriBuilding {
	private EntityID id;
	private Building b;
	private double radius;
	private Set<SuntoriBuilding> nearBuilding;
	private Set<SuntoriBuilding> connectingBuilding;
	private Set<SuntoriBuilding> directConnectBuilding;
	private Map<SuntoriBuilding, Double> distanceMap;

	private Map<SuntoriBuilding, Double> connectValueMap;

	public SuntoriBuilding(Building b) {
		this.id = b.getID();
		this.b = b;
		// 建物の面積取得
		Integer squareMeasure = (Integer) b.getProperty(
				"urn:rescuecore2.standard:property:buildingareaground")
				.getValue();
		// 建物を円形であるとしたとき，建物の面積から求められる円の半径
		this.radius = Math.sqrt(squareMeasure.doubleValue()
				* ConfigConstants.BUILDING_SQUARE_MEASURE_CORRECTION_VALUE
				/ Math.PI);
		this.nearBuilding = new HashSet<SuntoriBuilding>();
		this.connectingBuilding = new HashSet<SuntoriBuilding>();
		this.distanceMap = new HashMap<SuntoriBuilding, Double>();
		this.directConnectBuilding = new HashSet<SuntoriBuilding>();
		this.connectValueMap = new HashMap<SuntoriBuilding, Double>();
	}

	public void addNearBuilding(SuntoriBuilding sb) {
		this.nearBuilding.add(sb);
	}

	public Set<SuntoriBuilding> getNearBuilding() {
		return this.nearBuilding;
	}

	public void addConnectingBuilding(SuntoriBuilding sb) {
		this.connectingBuilding.add(sb);
	}

	public List<SuntoriBuilding> getConnectingBuilding() {
		return new ArrayList<SuntoriBuilding>(this.connectingBuilding);
	}

	public Building getBuilding() {
		return this.b;
	}

	public double getRadius() {
		return this.radius;
	}

	public Pair<Integer, Integer> getLocation(StandardWorldModel model) {
		return this.b.getLocation(model);
	}

	public Double getDistance(SuntoriBuilding sb) {
		return this.distanceMap.get(sb);
	}

	public void setDistance(SuntoriBuilding sb, double value) {
		this.distanceMap.put(sb, value);
	}

	public EntityID getID() {
		return this.b.getID();
	}

	public int getX() {
		return this.b.getX();
	}

	public int getY() {
		return this.b.getY();
	}

	public static List<SuntoriBuilding> createSuntoriBuildings(
			StandardWorldModel model) {
		List<SuntoriBuilding> res = new ArrayList<SuntoriBuilding>();
		for (StandardEntity se : model
				.getEntitiesOfType(StandardEntityURN.BUILDING)) {
			res.add(new SuntoriBuilding((Building) se));
		}
		setNearBuilding(res, model);
		setConnectingBuilding2(res, model);
		return res;
	}

	/**
	 * 隣接していると考えられそうな建物を登録する．
	 * 
	 * @param sbList
	 * @param model
	 */
	private static void setConnectingBuilding(List<SuntoriBuilding> sbList,
			StandardWorldModel model) {
		// 大まかな計算(近接している建物を取得する)
		for (SuntoriBuilding sb : sbList) {
			for (SuntoriBuilding sb2 : sb.getNearBuilding()) {
				if (sb.getDistance(sb2) - sb.getRadius() - sb2.getRadius() < 40000) {
					sb.addConnectingBuilding(sb2);
					sb2.addConnectingBuilding(sb);
				}
			}
		}

		// 隣接していると考えられる建物の中からほぼ確定しているものを計算
		for (SuntoriBuilding sb : sbList) {
			Pair<Integer, Integer> loc = sb.getLocation(model);
			LOOP: for (SuntoriBuilding sb2 : sb.getConnectingBuilding()) {
				Pair<Integer, Integer> loc2 = sb2.getLocation(model);
				Pair<Integer, Integer> cp = Util.getCenterOf(loc, loc2);
				Double dis = sb.getDistance(sb2);
				List<Area> areas = new ArrayList<Area>();
				for (StandardEntity se : model.getObjectsInRange(cp.first(),
						cp.second(), (int) (dis / 2))) {
					// if (se instanceof Road) {
					EntityID id = se.getID();
					if (se instanceof Area && !id.equals(sb.getID())
							&& !id.equals(sb2.getID())) {
						areas.add((Area) se);
					}
					// }
				}
				Edge edge = new Edge(loc.first(), loc.second(), loc2.first(),
						loc2.second());
				for (Area a : areas) {
					for (Edge e : a.getEdges()) {
						if (Util.isCross(edge, e)) {
							continue LOOP;
						}
					}
				}
				// sb.addDirectConnect(sb2);
				// sb2.addDirectConnect(sb);
				// 2つのBiuldingの重心を結ぶ直線と交差する2Edgeの距離をお互いの建物のconnectValueに設定
				Edge sbEdge = null;
				for (Edge ed : sb.getBuilding().getEdges()) {
					if (Util.isCross(edge, ed)) {
						sbEdge = ed;
						break;// 交点は各建物に1点のハズ
					}
				}
				Edge sbEdge2 = null;
				for (Edge ed : sb2.getBuilding().getEdges()) {
					if (Util.isCross(edge, ed)) {
						sbEdge2 = ed;
						break;// 交点は各建物に1点のハズ
					}
				}
				if (sbEdge != null && sbEdge2 != null) {
					// 2つの線分の距離をざっくり計算
					int x1 = (sbEdge.getStartX() + sbEdge.getEndX()) / 2;
					int y1 = (sbEdge.getStartY() + sbEdge.getEndY()) / 2;
					int x2 = (sbEdge2.getStartX() + sbEdge2.getEndX()) / 2;
					int y2 = (sbEdge2.getStartY() + sbEdge2.getEndY()) / 2;
					double d = Util.getDistance(x1, y1, x2, y2);
					sb.getConnectValueMap().put(sb2, d);
					sb2.getConnectValueMap().put(sb, d);
				}
			}
		}
	}

	private Set<SuntoriBuilding> tmpSet = new HashSet<SuntoriBuilding>();

	public void setTmp(SuntoriBuilding tmp) {
		this.tmpSet.add(tmp);
	}

	public Set<SuntoriBuilding> getTmpSet() {
		return this.tmpSet;
	}

	/**
	 * 隣接していると考えられそうな建物を登録する．
	 * 
	 * @param sbList
	 * @param model
	 */
	private static void setConnectingBuilding2(List<SuntoriBuilding> sbList,
			StandardWorldModel model) {
		for (SuntoriBuilding sb : sbList) {
			for (SuntoriBuilding sb2 : sb.getNearBuilding()) {
				if (sb.getDistance(sb2) - sb.getRadius() - sb2.getRadius() < 28000) {
					sb.addConnectingBuilding(sb2);
					sb2.addConnectingBuilding(sb);
				}
			}
		}
		// ラフチェック(時間は早いが形がいびつ？)
		for (SuntoriBuilding sb : sbList) {
			Pair<Integer, Integer> loc = sb.getLocation(model);
			LOOP: for (SuntoriBuilding sb2 : sb.getConnectingBuilding()) {
				Pair<Integer, Integer> loc2 = sb2.getLocation(model);
				Pair<Integer, Integer> cp = Util.getCenterOf(loc, loc2);
				Double dis = sb.getDistance(sb2);
				List<Area> areas = new ArrayList<Area>();
				for (StandardEntity se : model.getObjectsInRange(cp.first(),
						cp.second(), (int) (dis / 2))) {
					// if (se instanceof Road) {
					EntityID id = se.getID();
					if (se instanceof Building && !id.equals(sb.getID())
							&& !id.equals(sb2.getID())) {
						areas.add((Area) se);
					}
					// }
				}
				Edge edge = new Edge(loc.first(), loc.second(), loc2.first(),
						loc2.second());
				for (Area a : areas) {
					for (Edge e : a.getEdges()) {
						if (Util.isCross(edge, e)) {
							continue LOOP;
						}
					}
				}
				sb.setTmp(sb2);
				sb2.setTmp(sb);
			}
		}
		// 隣接していると考えられる建物の中からほぼ確定しているものを計算
		// 輪郭情報を使用
		for (SuntoriBuilding sb : sbList) {
			setConnectedNeighbours(sb);
		}
	}

	private static void setConnectedNeighbours(SuntoriBuilding sb) {
		List<BuildingEdge> edgeList = new ArrayList<BuildingEdge>();
		List<BuildingEdge> myEdges = new ArrayList<BuildingEdge>();
		// 隣接している可能性のある建物の輪郭を取り出しておく
		for (SuntoriBuilding b : sb.getTmpSet()) {
			// for (SuntoriBuilding b : sb.getConnectingBuilding()) {
			for (Edge edge : b.getBuilding().getEdges()) {
				edgeList.add(new BuildingEdge(edge, b));
			}
		}
		for (Edge edge : sb.getBuilding().getEdges()) {
			myEdges.add(new BuildingEdge(edge, sb));
		}
		int x = sb.getX();
		int y = sb.getY();
		final int DEGREE = 5;
		// 近接接続判定用放射直線を引くときの長さ
		final int BASE_SIZE = 28000;
		Set<BuildingEdge> connected = new HashSet<BuildingEdge>();
		for (int i = 0; i < 180; i += DEGREE) {
			double angle = (double) (i * Math.PI) / 180;
			double dx = Math.cos(angle);
			double dy = Math.sin(angle);
			int px = (int) (x + BASE_SIZE * dx);
			int py = (int) (y + BASE_SIZE * dy);
			int px2 = (int) (x - BASE_SIZE * dx);
			int py2 = (int) (y - BASE_SIZE * dy);
			ArrayList<BuildingEdge> crossEdge1 = new ArrayList<BuildingEdge>();
			ArrayList<BuildingEdge> crossEdge2 = new ArrayList<BuildingEdge>();

			for (BuildingEdge be : edgeList) {
				if (be.isCross(px, py, px2, py2, x, y)) {
					if (be.getDistance() >= 0) {
						// 正の方向に伸びる直線の場合はこっち
						crossEdge1.add(be);
					} else {
						// 逆方向に伸びる直線の場合はこっち
						crossEdge2.add(be);
					}
				}
			}
			// 現在見ている直線上のEdgeの中で，もっとも遠いものを選択(自分のEdge)．
			BuildingEdge myMaxEdge1 = null;
			BuildingEdge myMaxEdge2 = null;
			for (BuildingEdge be : myEdges) {
				if (be.isCross(px, py, px2, py2, x, y)) {
					if (be.getDistance() >= 0) {
						if (myMaxEdge1 == null
								|| myMaxEdge1.getDistance() < be.getDistance()) {
							myMaxEdge1 = be;
						}
					} else {
						if (myMaxEdge2 == null
								|| myMaxEdge2.getDistance() > be.getDistance()) {
							myMaxEdge2 = be;
						}
					}
				}
			}
			BuildingEdge minEdge1 = null;
			BuildingEdge minEdge2 = null;
			// 交差するEdgeがあった場合，その中で最小の距離のものを接続しているとみなす．
			// ただし，そのEdgeが自分のEdgeであった場合は除く必要がある．
			for (BuildingEdge h : crossEdge1) {
				if (minEdge1 == null
						|| h.getDistance() < minEdge1.getDistance()) {
					minEdge1 = h;
				}
			}
			for (BuildingEdge h : crossEdge2) {
				if (minEdge2 == null
						|| h.getDistance() > minEdge2.getDistance()) {
					minEdge2 = h;
				}
			}
			double isolation = 0.0;
			if (minEdge1 == null) {
				isolation += 40000;
			} else {
				minEdge1.addCounter();
				connected.add(minEdge1);
				isolation += minEdge1.getDistance();

				double value = minEdge1.getDistance() - 0D;
				if (myMaxEdge1 != null) {
					value = minEdge1.getDistance() - myMaxEdge1.getDistance();
					if (value < 0) {// マップ上で重なっているものも少しある可能性がある(map
									// validationが行われていないときなど)
						value = 0;
					}
				}

				Double min = sb.getDistance(minEdge1.getOwner());
				if (min == null || min > value) {
					sb.setDistance(minEdge1.getOwner(), value);
				}
			}
			if (minEdge2 == null) {
				isolation += 40000;
			} else {
				minEdge2.addCounter();
				connected.add(minEdge2);
				isolation += minEdge2.getDistance();

				double value = -minEdge2.getDistance() - 0D;
				if (myMaxEdge2 != null) {
					value = -minEdge2.getDistance() + myMaxEdge2.getDistance();
					if (value < 0) {// マップ上で重なっているものも少しある可能性がある(map
									// validationが行われていないときなど)
						value = 0;
					}
				}

				Double min = sb.getDistance(minEdge2.getOwner());
				if (min == null || min > value) {
					sb.setDistance(minEdge2.getOwner(), value);
				}
			}
		}
		for (BuildingEdge be : connected) {
			SuntoriBuilding owner = be.getOwner();
			sb.addDirectConnect(owner);
			Double value = sb.getConnectValueMap().get(owner);
			if (value == null) {// 隣接する建物として初めてチェックする
				sb.getConnectValueMap().put(owner,
						(double) be.getCounter() * ((float) DEGREE / 360));
			} else {
				sb.getConnectValueMap().put(
						owner,
						value + (double) be.getCounter()
								* ((float) DEGREE / 360));
			}
		}
		// setIsolationValue(isolation*(DEGREE)/360);
	}

	public void addDirectConnect(SuntoriBuilding sb) {
		this.directConnectBuilding.add(sb);
	}

	public Set<SuntoriBuilding> getDirectConnectBuilding() {
		return this.directConnectBuilding;
	}

	/**
	 * ある程度近い同士の建物を登録する．
	 * 
	 * @param sbList
	 * @param model
	 */
	private static void setNearBuilding(List<SuntoriBuilding> sbList,
			StandardWorldModel model) {
		for (int i = 0; i < sbList.size(); i++) {
			SuntoriBuilding sb1 = sbList.get(i);
			for (int j = i + 1; j < sbList.size(); j++) {
				SuntoriBuilding sb2 = sbList.get(j);
				double dis = Util.getDistance(sb1.getLocation(model),
						sb2.getLocation(model));
				if (dis < 100000) {
					// 建物登録
					sb1.addNearBuilding(sb2);
					sb2.addNearBuilding(sb1);
					// 距離登録
					sb1.setDistance(sb2, dis);
					sb2.setDistance(sb1, dis);
				}
			}
		}
	}

	public boolean isBurning() {
		return this.b.isOnFire();
	}

	public int getFieryness() {
		return this.b.getFieryness2();
	}

	public boolean isUnburned() {
		return this.b.getFieryness2() == 0;
	}

	public boolean isPutout() {
		int fieryness = this.b.getFieryness2();
		return fieryness >= 5 && fieryness <= 7;
	}

	public int getGroundArea() {
		return this.b.getGroundArea();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SuntoriBuilding other = (SuntoriBuilding) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public int getConnectBurnableCount() {
		int res = 0;
		// for (SuntoriBuilding sb : this.connectingBuilding) {
		for (SuntoriBuilding sb : this.directConnectBuilding) {
			if (sb.isUnburned() || sb.isPutout()) {
				if (!sb.isBurnable()) {
					continue;
				}
				res++;
			}
		}
		return res;
	}

	/**
	 * この建物が燃焼可能であるかどうか
	 * 
	 * @return
	 */
	public boolean isBurnable() {
		boolean res = true;
		if (this.b instanceof Refuge || this.b instanceof FireStation
				|| this.b instanceof PoliceOffice
				|| this.b instanceof AmbulanceCentre) {
			res = false;
		}
		return res;
	}

	public double simpleCalculateConnectValue() {
		double res = 0;
		for (SuntoriBuilding sb : this.directConnectBuilding) {
			if (sb.getDirectConnectBuilding().contains(this)) {
				Building b = sb.getBuilding();
				if (b instanceof Refuge || b instanceof PoliceOffice
						|| b instanceof FireStation
						|| b instanceof AmbulanceCentre) {
					continue;
				}
				double value = sb.getConnectValueMap().get(this)
						* (sb.getDistance(this) / (40000d))
				// * ((double) this.getGroundArea())
				// / this.b.getTotalArea();
				;
				if (sb.isUnburned() || sb.isPutout()) {
					res += value;
				} else if (sb.isBurning()) {
					res -= value;
				}
			}
		}
		return res;
	}

	public Map<SuntoriBuilding, Double> getConnectValueMap() {
		return this.connectValueMap;
	}
}
