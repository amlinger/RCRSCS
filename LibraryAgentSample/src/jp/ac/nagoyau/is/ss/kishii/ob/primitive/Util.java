package jp.ac.nagoyau.is.ss.kishii.ob.primitive;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.ac.nagoyau.is.ss.kishii.ob.primitive.svm.SiteGroup;
import rescuecore2.misc.Pair;
import rescuecore2.standard.entities.Blockade;
import rescuecore2.standard.entities.Edge;
import rescuecore2.standard.entities.StandardEntity;
import rescuecore2.standard.entities.StandardWorldModel;
import rescuecore2.worldmodel.EntityID;

public class Util {
	/**
	 * 強制的にMapに対して逐次加算型のPutを行う。<br>
	 * nullであるkeyを指定した場合に対応
	 */
	public static <K> void doForcedAddingPut(Map<K, Double> map, K key,
			Double value) {
		Double tmp = map.get(key);
		if (tmp == null) {
			tmp = 0d;
		}
		map.put(key, tmp + value);
	}

	public static boolean isCross(Edge edge, List<Edge> list) {
		for (Edge e : list) {
			if (isCross(edge, e)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 円との交点を計算
	 * 
	 * @param a
	 * @param b
	 * @param r
	 * @param c
	 * @param d
	 * @param edge
	 * @return
	 */
	public final static Set<Point> calculateCrossPointWithCircle(double a,
			double b, double r, double c, double d, Edge edge) {
		Set<Point> res = new HashSet<Point>();
		double numerator = Util.square((c * (d - b)) - a);
		double denominator = (c * c) + 1;
		double discriminant = numerator
				- (denominator * ((a * a) + Util.square(d - b) - (r * r)));
		if (discriminant < 0) {
		} else if (discriminant == 0) {
			double kx = -numerator / denominator;
			double ky = (c * kx) + d;
			if (isOnLine(edge, kx, ky)) {
				res.add(new Point((int) kx, (int) ky));
			}
		} else {
			double tmp = Math.sqrt(discriminant);
			double kx1 = (-numerator + tmp) / denominator;
			double ky1 = (c * kx1) + d;
			if (Util.isOnLine(edge, kx1, ky1)) {
				res.add(new Point((int) kx1, (int) ky1));
			}
			double kx2 = (-numerator - tmp) / denominator;
			double ky2 = (c * kx2) + d;
			if (Util.isOnLine(edge, kx2, ky2)) {
				res.add(new Point((int) kx2, (int) ky2));
			}
		}
		return res;
	}

	/**
	 * 与えられたEdgeに座標があるかどうかを判定する．<br>
	 * ただし，与えられた点はEdgeの直線上にあることを前提とする．
	 * 
	 * @return
	 */
	public final static boolean isOnLine(Edge edge, double x, double y) {
		boolean res = false;
		double ds = Util.getDistance(edge.getStartX(), edge.getStartY(), x, y);
		double de = Util.getDistance(edge.getEndX(), edge.getEndY(), x, y);
		double length = Util.getDistance(edge.getStartX(), edge.getStartY(),
				edge.getEndX(), edge.getEndY());
		if (ds + de <= length) {
			res = true;
		}
		return res;
	}

	/**
	 * 与えられた値を2乗する．
	 * 
	 * @param value
	 * @return
	 */
	public final static double square(double value) {
		return value * value;
	}

	/**
	 * 点fromからlocationへの角度（ラジアン）を取得する
	 * 
	 * @param fromX
	 * @param fromY
	 * @param location
	 * @return
	 */
	public final static double radian(double fromX, double fromY,
			Pair<Integer, Integer> location) {
		double dx = fromX - location.first();
		double dy = fromY - location.second();
		double theta = Math.atan2(dy, dx);
		if (theta < 0) {
			theta += 2 * Math.PI;
		}
		return theta;
	}

	public static double getDistance(int x1, int y1, int x2, int y2) {
		long dx = x1 - x2;
		long dy = y1 - y2;
		return Math.sqrt((dx * dx) + (dy * dy));
	}

	public static double getDistance(double x1, double y1, double x2, double y2) {
		double dx = x1 - x2;
		double dy = y1 - y2;
		return Math.sqrt((dx * dx) + (dy * dy));
	}

	/**
	 * 2点のユークリッド距離を取得する．
	 * 
	 * @param loc1
	 * @param loc2
	 * @return
	 */
	public final static double getDistance(Pair<Integer, Integer> loc1,
			Pair<Integer, Integer> loc2) {
		return getDistance(loc1.first(), loc1.second(), loc2.first(),
				loc2.second());
	}

	/**
	 * 2点の中心点座標を取得する．
	 * 
	 * @param loc1
	 * @param loc2
	 * @return
	 */
	public final static Pair<Integer, Integer> getCenterOf(
			Pair<Integer, Integer> loc1, Pair<Integer, Integer> loc2) {
		return getCenterOf(loc1.first(), loc1.second(), loc2.first(),
				loc2.second());
	}

	public final static Pair<Integer, Integer> getCenterOf(int x1, int y1,
			int x2, int y2) {
		return new Pair<Integer, Integer>((x1 + x2) / 2, (y1 + y2) / 2);
	}

	public final static boolean isCross(Edge e1, Edge e2) {
		// x座標によるチェック
		long x1 = e1.getStartX();
		long x2 = e1.getEndX();
		long x3 = e2.getStartX();
		long x4 = e2.getEndX();
		if (x1 >= x2) {
			if ((x1 < x3 && x1 < x4) || (x2 > x3 && x2 > x4)) {
				return false;
			}
		} else {
			if ((x2 < x3 && x2 < x4) || (x1 > x3 && x1 > x4)) {
				return false;
			}
		}
		// y座標によるチェック
		long y1 = e1.getStartY();
		long y2 = e1.getEndY();
		long y3 = e2.getStartY();
		long y4 = e2.getEndY();
		if (y1 >= y2) {
			if ((y1 < y3 && y1 < y4) || (y2 > y3 && y2 > y4)) {
				return false;
			}
		} else {
			if ((y2 < y3 && y2 < y4) || (y1 > y3 && y1 > y4)) {
				return false;
			}
		}

		// 交差判定
		long je1 = ((x1 - x2) * (y3 - y1) + (y1 - y2) * (x1 - x3));
		long je2 = ((x1 - x2) * (y4 - y1) + (y1 - y2) * (x1 - x4));
		if ((je1 > 0 && je2 > 0) || (je1 < 0 && je2 < 0)) {
			return false;
		}
		long je3 = ((x3 - x4) * (y1 - y3) + (y3 - y4) * (x3 - x1));
		long je4 = ((x3 - x4) * (y2 - y3) + (y3 - y4) * (x3 - x2));
		if ((je3 > 0 && je4 > 0) || (je3 < 0 && je4 < 0)) {
			return false;
		}

		return true;
	}

	/**
	 * ある閉塞のEdgeの中で一番近いものへの距離を取得する．<br>
	 * Edgeが取得できない場合はDouble.MAX_VALUEが返される．
	 * 
	 * @param loc
	 * @param target
	 * @return
	 */
	public static double getMinDistanceOfBlockadeEdge(
			Pair<Integer, Integer> loc, Blockade target) {
		double res = Double.MAX_VALUE;
		if (target.isApexesDefined()) {
			int[] apexes = target.getApexes();
			for (int i = 0; i < apexes.length; i += 2) {
				int a1 = i;
				int a2 = (i + 2) % apexes.length;
				int cx = (apexes[a1] + apexes[a2]) / 2;
				int cy = (apexes[a1 + 1] + apexes[a2 + 1]) / 2;
				double val = Util
						.getDistance(loc.first(), loc.second(), cx, cy);
				if (val < res) {
					res = val;
				}
			}
		}
		return res;
	}

	/**
	 * ある閉塞のEdgeの中で一番近いEdgeを取得する<br>
	 * 
	 * @param loc
	 * @param target
	 * @return
	 */
	public static Edge getMinDistanceEdgeOfBlockade(Pair<Integer, Integer> loc,
			Blockade target) {
		double dis = Double.MAX_VALUE;
		Edge res = null;
		if (target.isApexesDefined()) {
			int[] apexes = target.getApexes();
			for (int i = 0; i < apexes.length; i += 2) {
				int a1 = i;
				int a2 = (i + 2) % apexes.length;
				int cx = (apexes[a1] + apexes[a2]) / 2;
				int cy = (apexes[a1 + 1] + apexes[a2 + 1]) / 2;
				double val = Util
						.getDistance(loc.first(), loc.second(), cx, cy);
				if (val < dis) {
					dis = val;
					res = new Edge(apexes[a1], apexes[a1 + 1], apexes[a2],
							apexes[a2 + 1]);
				}
			}
		}
		return res;
	}

	/**
	 * 閉塞のapexから閉塞を構成しているEdgeを生成する．
	 * 
	 * @param b
	 * @return
	 */
	public static final List<Edge> createEdgeOfBlockade(Blockade b) {
		List<Edge> res = new ArrayList<Edge>();
		if (b.isApexesDefined()) {
			int[] apexes = b.getApexes();
			for (int i = 0; i < apexes.length; i += 2) {
				int a1 = i;
				int a2 = (i + 2) % apexes.length;
				res.add(new Edge(apexes[a1], apexes[a1 + 1], apexes[a2],
						apexes[a2 + 1]));
			}
		}
		return res;
	}

	/**
	 * 火災クラスタの生成
	 * 
	 * @param model
	 * @param sbList
	 * @return
	 */
	// public static List<FireGroup> getFireGroupList(StandardWorldModel model,
	// List<SuntoriBuilding> sbList) {
	// List<FireGroup> res = new ArrayList<FireGroup>();
	// List<SuntoriBuilding> fires = getFire(sbList);
	// while (fires.size() > 0) {
	// FireGroup newGroup = new FireGroup();
	// SuntoriBuilding sb = fires.get(0);
	// fires.remove(0);
	//
	// newGroup.add(sb);
	// checkCluster(fires, newGroup, sb);
	//
	// res.add(newGroup);
	// }
	// return res;
	// }

	// private static void checkCluster(List<SuntoriBuilding> unchecked,
	// FireGroup group, SuntoriBuilding tar) {
	// List<SuntoriBuilding> newMember = new ArrayList<SuntoriBuilding>(
	// unchecked);
	// newMember.retainAll(tar.getConnectingBuilding());
	// if (newMember.size() == 0) {
	// return;
	// }
	// unchecked.removeAll(newMember);
	// group.addAll(newMember);
	//
	// for (SuntoriBuilding sb : newMember) {
	// checkCluster(unchecked, group, sb);
	// }
	// }
	//
	// public static List<SuntoriBuilding> getFire(List<SuntoriBuilding> sbList)
	// {
	// List<SuntoriBuilding> res = new ArrayList<SuntoriBuilding>();
	// for (SuntoriBuilding sb : sbList) {
	// if (sb.isBurning()) {
	// res.add(sb);
	// }
	// }
	// return res;
	// }

	public static EntityID getNearestBlockade(StandardWorldModel model,
			Pair<Integer, Integer> loc, Collection<EntityID> list) {
		EntityID res = null;
		double min = Double.MAX_VALUE;
		for (EntityID id : list) {
			StandardEntity se = model.getEntity(id);
			if (se instanceof Blockade) {
				double dis = getMinDistanceOfBlockadeEdge(loc, (Blockade) se);
				if (dis < min) {
					min = dis;
					res = se.getID();
				}
			}
		}
		return res;
	}

	public static List<SiteGroup> getFireAreaGroupList(
			StandardWorldModel model, List<FireSite> fireSites) {
		List<SiteGroup> res = new ArrayList<SiteGroup>();
		// 火災の発生している隣接エリアを計算
		List<FireSite> burningSite = getBurningFireSite(fireSites);
		while (burningSite.size() > 0) {
			SiteGroup newGroup = new SiteGroup();
			FireSite site = burningSite.get(0);
			burningSite.remove(0);

			newGroup.add(site);
			checkAreaCluster(site, burningSite, newGroup);
			res.add(newGroup);
		}
		return res;
	}

	/**
	 * 隣接エリアの燃えているものをグルーピングする
	 * 
	 * @param model
	 * @param sites
	 * @return
	 */
	public static List<SiteGroup> getFireAreaGroupList(
			StandardWorldModel model, Set<FireSite> sites) {
		List<SiteGroup> res = new ArrayList<SiteGroup>();
		// 火災の発生している隣接エリアを計算
		List<FireSite> burningSite = getBurningFireSite(sites);
		while (burningSite.size() > 0) {
			SiteGroup newGroup = new SiteGroup();
			FireSite site = burningSite.get(0);
			burningSite.remove(0);

			newGroup.add(site);
			checkAreaCluster(site, burningSite, newGroup);
			res.add(newGroup);
		}
		return res;
	}

	/**
	 * 火災の発生している隣接エリアを計算
	 * 
	 * @param model
	 * @param sites
	 *            隣接エリアのセット
	 * @return
	 */
	public static List<FireSite> getBurningFireSite(Collection<FireSite> sites) {
		List<FireSite> res = new ArrayList<FireSite>();
		for (FireSite site : sites) {
			for (SuntoriBuilding sb : site.getBuildings()) {
				if (sb.isBurning()) {
					res.add(site);
					break;
				}
			}
		}
		return res;
	}

	private static void checkAreaCluster(FireSite site,
			List<FireSite> unchecked, SiteGroup group) {
		List<FireSite> newMember = new ArrayList<FireSite>(unchecked);
		newMember.retainAll(site.getConnectionFireSite());
		if (newMember.size() == 0) {
			return;
		}
		unchecked.removeAll(newMember);
		group.addAll(newMember);
		for (FireSite fs : newMember) {
			checkAreaCluster(fs, unchecked, group);
		}
	}

	public static boolean isInOrCrossingBlockade(
			Pair<Integer, Integer> fromPoint, Pair<Integer, Integer> toPoint,
			List<Blockade> blockList) {
		for (Blockade b : blockList) {
			if (isInOrClossingWith(b, fromPoint, toPoint)) {
				return true;
			}
		}
		return false;
	}

	private static boolean isInOrClossingWith(Blockade block,
			Pair<Integer, Integer> fromPoint, Pair<Integer, Integer> toPoint) {
		List<Edge> edges = Util.createEdgeOfBlockade(block);
		Edge target = new Edge(fromPoint.first(), fromPoint.second(),
				toPoint.first(), toPoint.second());
		if (isIn(edges, toPoint) || isCrossWithBlock(edges, target)) {
			return true;
		}
		return false;
	}

	private static boolean isCrossWithBlock(List<Edge> edges, Edge target) {
		for (Edge e : edges) {
			if (isCross(target, e)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 指定された点が閉塞の内部にあるかを判定する
	 * 
	 * @param block
	 * @param point
	 * @return
	 */
	private static boolean isIn(List<Edge> edges, Pair<Integer, Integer> point) {
		// 境界判定(閉塞のedge上に点が来ているときはtrueを返す)
		int x = point.first();
		int y = point.second();
		for (Edge e : edges) {
			if (isOnLine(e, x, y)) {
				return true;
			}
		}
		// 境界線上に無いとき
		int count = 0;
		for (Edge e : edges) {
			int y1 = e.getStartY();
			int y2 = e.getEndY();
			boolean yFlag1 = y <= y1;
			boolean yFlag2 = y <= y2;
			if (yFlag1 != yFlag2) {// pointからの線分を横切る可能性がある
				int x1 = e.getStartX();
				int x2 = e.getEndX();
				boolean xFlag1 = x <= x1;
				boolean xFlag2 = x <= x2;
				if (xFlag1 == xFlag2) {// 点の片側に偏っている
					if (xFlag1) {// 右にあることが確定(xFlag1=xFlag2より)
						count++;
					}
				} else {
					int clossX = x1
							+ (int) ((double) ((x1 - x2) * (y - y1)) / (y1 - y2));
					if (x <= clossX) {
						count++;
					}
				}
			}
		}
		if (count % 2 == 0) {
			return false;
		} else {
			return true;
		}
	}
}
