package jp.ac.nagoyau.is.ss.kishii.ob.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.ac.nagoyau.is.ss.kishii.ob.primitive.Util;
import rescuecore2.standard.entities.Area;
import rescuecore2.standard.entities.Edge;
import rescuecore2.standard.entities.StandardWorldModel;
import rescuecore2.worldmodel.EntityID;

/**
 * world model からリンク-ノードを取得する．
 * 
 * @author takefumi
 * 
 */
public class NodeCreater {
	/**
	 * リンク-ノード生成メソッド<br>
	 * AgentGUIで使用される関数．<br>
	 * 本番では特に必要のないメソッド
	 * 
	 * @param model
	 * @return
	 */
	public static List<SuntoriNode> getNetwork(StandardWorldModel model) {
		Map<EntityID, SuntoriNode> nodeMap = new HashMap<EntityID, SuntoriNode>();
		List<SuntoriNode> res = new ArrayList<SuntoriNode>();
		for (Area area : model.getAreaList()) {
			SuntoriNode node = new SuntoriNode(area);
			nodeMap.put(area.getID(), node);
			res.add(node);
		}
		// setLink(...)との比較は必要かも
		// もしかしたらあるノード数以上の時はこっちで，それ以外は別の方のがいいのかもしれないから．
		// (計算時間に関しての話)
		for (SuntoriNode node : res) {
			for (EntityID id : node.getNeighbours()) {
				SuntoriNode sn = nodeMap.get(id);
				node.addLink(sn, calculateCost(node, sn, model));
			}
		}
		return res;
	}

	/**
	 * リンク-ノード生成メソッド
	 * 
	 * @param model
	 * @return
	 */
	public static Map<EntityID, SuntoriNode> getNetworkMap(
			StandardWorldModel model) {
		Map<EntityID, SuntoriNode> res = new HashMap<EntityID, SuntoriNode>();
		List<SuntoriNode> nodeList = new ArrayList<SuntoriNode>();
		for (Area area : model.getAreaList()) {
			SuntoriNode node = new SuntoriNode(area);
			res.put(area.getID(), node);
			nodeList.add(node);
		}
		// setLink(...)との比較は必要かも
		// もしかしたらあるノード数以上の時はこっちで，それ以外は別の方のがいいのかもしれないから．
		for (SuntoriNode node : nodeList) {
			for (EntityID id : node.getNeighbours()) {
				SuntoriNode sn = res.get(id);
				node.addLink(sn, calculateCost(node, sn, model));
			}
		}
		return res;
	}

	/**
	 * ノード間コストの計算を行う．
	 * 
	 * @param node1
	 * @param node2
	 * @return
	 */
	public static double calculateCost(SuntoriNode node1, SuntoriNode node2,
			StandardWorldModel model) {
		return minDistanceToEdge(node1, node2, model);
	}

	/**
	 * node1の重心からnode2の重心まで，エリアの中心を通るように動いた時の予想移動距離．
	 * 
	 * @param node1
	 * @param node2
	 * @param model
	 * @return
	 */
	private static double minDistanceToEdge(SuntoriNode node1,
			SuntoriNode node2, StandardWorldModel model) {
		List<Edge> edge1 = node1.getConnectingEdges(node2);
		List<Edge> edge2 = node2.getConnectingEdges(node1);

		double res = 0;
		if (edge1.size() == 1) {
			Edge edge = edge1.get(0);
			int centerX = (edge.getStartX() + edge.getEndX()) / 2;
			int centerY = (edge.getStartY() + edge.getEndY()) / 2;
			res += Util.getDistance(node1.getSelf().getX(), node1.getSelf()
					.getY(), centerX, centerY);
		} else {
			int centerX = 0;
			int centerY = 0;
			for (Edge edge : edge1) {
				centerX += (edge.getStartX() + edge.getEndX()) / 2;
				centerY += (edge.getStartY() + edge.getEndY()) / 2;
			}
			centerX /= edge1.size();
			centerY /= edge1.size();
			res += Util.getDistance(node1.getSelf().getX(), node1.getSelf()
					.getY(), centerX, centerY);
		}
		if (edge2.size() == 1) {
			Edge edge = edge2.get(0);
			int centerX = (edge.getStartX() + edge.getEndX()) / 2;
			int centerY = (edge.getStartY() + edge.getEndY()) / 2;
			res += Util.getDistance(node2.getSelf().getX(), node2.getSelf()
					.getY(), centerX, centerY);
		} else {
			int centerX = 0;
			int centerY = 0;
			for (Edge edge : edge2) {
				centerX += (edge.getStartX() + edge.getEndX()) / 2;
				centerY += (edge.getStartY() + edge.getEndY()) / 2;
			}
			centerX /= edge2.size();
			centerY /= edge2.size();
			res += Util.getDistance(node2.getSelf().getX(), node2.getSelf()
					.getY(), centerX, centerY);
		}

		return res;
	}

	/**
	 * 生成したノードにリンクを張り，リンク-ノードの形にする．<br>
	 * (再帰的にリンクを張っていくようにしたほう)
	 * 
	 * @param nodeMap
	 * @param nodeList
	 */
	private static void setLinks(Map<EntityID, SuntoriNode> nodeMap,
			List<SuntoriNode> nodeList, StandardWorldModel model) {
		setLink(nodeMap, nodeList.get(0), model);
	}

	/**
	 * 生成したノードにリンクを張り，リンク-ノードの形にする．
	 * 
	 * @param nodeMap
	 * @param node
	 * @param model
	 */
	private static void setLink(Map<EntityID, SuntoriNode> nodeMap,
			SuntoriNode node, StandardWorldModel model) {
		for (EntityID id : node.getNeighbours()) {
			SuntoriNode next = nodeMap.get(id);
			if (!node.hasLink(next)) {
				double cost = calculateCost(node, next, model);
				node.addLink(next, cost);
				next.addLink(next, cost);
				setLink(nodeMap, next, model);
			}
		}
	}
}
