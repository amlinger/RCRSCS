package jp.ac.nagoyau.is.ss.kishii.ob.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rescuecore2.standard.entities.Area;
import rescuecore2.standard.entities.Edge;
import rescuecore2.worldmodel.EntityID;

public class SuntoriNode {
	/**
	 * 自身
	 */
	Area area;
	List<EntityID> neighbour;
	/**
	 * 他Areaへのリンクとそのコスト
	 */
	Map<SuntoriNode, Double> links;

	Set<EntityID> unpassableSet;
	/**
	 * 通行可能を固定できるものを格納するセット
	 */
	Set<EntityID> passableSet;

	/**
	 * コンストラクタ
	 * 
	 * @param area
	 */
	public SuntoriNode(Area area) {
		this.area = area;
		this.neighbour = area.getNeighbours();
		this.links = new HashMap<SuntoriNode, Double>();
		this.unpassableSet = new HashSet<EntityID>();
		this.passableSet = new HashSet<EntityID>();
	}

	/**
	 * リンクを追加する．<br>
	 * 重複する場合は上書きする．
	 * 
	 * @param node
	 * @param cost
	 */
	public void addLink(SuntoriNode node, double cost) {
		this.links.put(node, cost);
	}

	/**
	 * このノードの隣接ノードIDを取得する．<br>
	 * AreaのgetNeighboursがsingletonを使っている．
	 * 
	 * @return
	 */
	public List<EntityID> getNeighbours() {
		return this.neighbour;
	}

	/**
	 * この場所に隣接する場所で，到達可能であると考えられるエリアのEntityIDのリストを取得する．
	 * 
	 * @return 到達可能性のある隣接エリアのEntityIDのリスト
	 */
	public List<EntityID> getPassableNeighbours() {
		List<EntityID> res = new ArrayList<EntityID>(this.area.getNeighbours());
		res.removeAll(this.unpassableSet);
		return res;
	}

	/**
	 * 指定されたノードとのリンクをすでに形成しているかどうかを取得する．
	 * 
	 * @param node
	 * @return
	 */
	public boolean hasLink(SuntoriNode node) {
		return this.links.containsKey(node);
	}

	/**
	 * 通行不可をセットする．
	 */
	public void setUnpassable(EntityID id) {
		if (!this.passableSet.contains(id)) {
			this.unpassableSet.add(id);
		}
	}

	/**
	 * 通行不可を解除する．
	 * 
	 * @param id
	 */
	public void setPassable(EntityID id) {
		this.unpassableSet.remove(id);
		this.passableSet.add(id);
	}

	/**
	 * このノード(エリア)のEntityIDを取得する．
	 * 
	 * @return
	 */
	public EntityID getID() {
		return this.area.getID();
	}

	/**
	 * このノードのエリアを取得する．
	 * 
	 * @return
	 */
	public Area getSelf() {
		return this.area;
	}

	/**
	 * このノード(エリア)の輪郭を取得する．
	 * 
	 * @return
	 */
	public List<Edge> getEdges() {
		return this.area.getEdges();
	}

	/**
	 * このノード(エリア)と指定されたノード(エリア)とが接続している部分の輪郭を取得する．<br>
	 * マップによっては複数得られる可能性がある．
	 * 
	 * @param node
	 * @return
	 */
	public List<Edge> getConnectingEdges(SuntoriNode node) {
		EntityID id = node.getID();
		List<Edge> res = new ArrayList<Edge>();
		for (Edge edge : getEdges()) {
			if (edge.isPassable() && edge.getNeighbour().equals(id)) {
				res.add(edge);
			}
		}
		return res;
	}

	/**
	 * このノード(エリア)とそれに隣接しているノード(エリア)間を移動する際の予想移動距離．
	 * 
	 * @param node
	 * @return
	 */
	public Double getCost(SuntoriNode node) {
		return this.links.get(node);
	}

	@Override
	public String toString() {
		return this.area.getID() + "";
	}

	public String getString() {
		return toString() + " " + links;
	}

	public boolean isPassable(EntityID id) {
		return this.neighbour.contains(id) && !this.unpassableSet.contains(id);
	}

	public List<EntityID> getPassable() {
		List<EntityID> res = new ArrayList<EntityID>(this.neighbour);
		res.removeAll(this.unpassableSet);
		return res;
	}
}
