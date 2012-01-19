package jp.ac.nagoyau.is.ss.kishii.ob.team.center.fs.specialize;

import java.util.ArrayList;
import java.util.List;

import jp.ac.nagoyau.is.ss.kishii.ob.primitive.FireGroup;
import jp.ac.nagoyau.is.ss.kishii.ob.primitive.SuntoriBuilding;
import jp.ac.nagoyau.is.ss.kishii.ob.primitive.Util;
import rescuecore2.misc.Pair;
import rescuecore2.standard.entities.Blockade;
import rescuecore2.standard.entities.Edge;
import rescuecore2.standard.entities.StandardWorldModel;
import rescuecore2.worldmodel.EntityID;

/**
 * 特化エージェント作成用に移植されてきたUtilメソッド群
 * 
 * @author takefumi
 * 
 */
public class Util2 {
	/**
	 * 火災クラスタの生成
	 * 
	 * @param model
	 * @param sbList
	 * @return
	 */
	public static List<FireGroup> getFireGroupList(StandardWorldModel model,
			List<SuntoriBuilding> sbList) {
		List<FireGroup> res = new ArrayList<FireGroup>();
		List<SuntoriBuilding> fires = getFire(sbList);
		while (fires.size() > 0) {
			FireGroup newGroup = new FireGroup();
			SuntoriBuilding sb = fires.get(0);
			fires.remove(0);

			newGroup.add(sb);
			checkCluster(fires, newGroup, sb);

			res.add(newGroup);
		}
		return res;
	}

	private static void checkCluster(List<SuntoriBuilding> unchecked,
			FireGroup group, SuntoriBuilding tar) {
		List<SuntoriBuilding> newMember = new ArrayList<SuntoriBuilding>(
				unchecked);
		newMember.retainAll(tar.getConnectingBuilding());
		if (newMember.size() == 0) {
			return;
		}
		unchecked.removeAll(newMember);
		group.addAll(newMember);

		for (SuntoriBuilding sb : newMember) {
			checkCluster(unchecked, group, sb);
		}
	}

	private static List<SuntoriBuilding> getFire(List<SuntoriBuilding> sbList) {
		List<SuntoriBuilding> res = new ArrayList<SuntoriBuilding>();
		for (SuntoriBuilding sb : sbList) {
			if (sb.isBurning()) {
				res.add(sb);
			}
		}
		return res;
	}

	public static EntityID isInOrCrossingBlockade(
			Pair<Integer, Integer> fromPoint, Pair<Integer, Integer> toPoint,
			List<Blockade> blockList) {
		for (Blockade b : blockList) {
			if (isInOrClossingWith(b, fromPoint, toPoint)) {
				return b.getID();
			}
		}
		return null;
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
}
