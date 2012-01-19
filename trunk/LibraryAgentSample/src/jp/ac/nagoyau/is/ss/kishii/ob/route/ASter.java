package jp.ac.nagoyau.is.ss.kishii.ob.route;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import jp.ac.nagoyau.is.ss.kishii.ob.network.NodeCreater;
import jp.ac.nagoyau.is.ss.kishii.ob.network.SuntoriNode;
import rescuecore2.standard.entities.StandardWorldModel;
import rescuecore2.worldmodel.EntityID;

/**
 * A*(a-star) algorithm を実装したクラス．
 * 
 * 
 * @author takefumi
 * 
 */
public class ASter implements ISuntoriRouter {
	StandardWorldModel model;
	Map<EntityID, SuntoriNode> nodeMap;

	private int count;
	private final static int move_random_shreshold = 5;
	private Random rand;

	/**
	 * コンストラクタ<br>
	 * エリアモデルをリンク-ノードの形に再形成する．
	 * 
	 * @param model
	 */
	public ASter(StandardWorldModel model) {
		this.nodeMap = NodeCreater.getNetworkMap(model);
		this.model = model;
		this.count = 0;
		this.rand = new Random();
		this.rand.nextInt();
	}

	public List<EntityID> getRouteIncludeUnpassable(EntityID start,
			EntityID goal) {
		List<EntityID> res = new ArrayList<EntityID>();
		if (start.equals(goal)) {
			res.add(start);
			this.count = 0;
			return res;
		}
		Map<EntityID, Double> gMap = new HashMap<EntityID, Double>();
		Map<EntityID, Double> fMap = new HashMap<EntityID, Double>();
		Map<EntityID, EntityID> routeMap = new HashMap<EntityID, EntityID>();
		Map<EntityID, Integer> unchecked = new HashMap<EntityID, Integer>();
		EntityID current = start;
		gMap.put(current, 0d);
		fMap.put(current, new Double(this.model.getDistance(current, goal)));
		try {
			unchecked.put(current, this.nodeMap.get(current).getNeighbours()
					.size());
		} catch (Exception e) {
			System.err.println(this.nodeMap);
			System.err.println(current);
			return null;
		}
		while (!current.equals(goal)) {
			Map<EntityID, Integer> candidateMap = new HashMap<EntityID, Integer>();
			// for (EntityID id : this.nodeMap.get(current).getNeighbours()) {
			for (EntityID id : this.nodeMap.get(current).getNeighbours()) {
				if (!fMap.containsKey(id)) {
					candidateMap.put(id, this.model.getDistance(id, goal));
				}
			}
			if (unchecked.size() + candidateMap.size() == 0) {
				System.err.println("Could not find route.:" + "from(" + start
						+ ")" + ",to(" + goal + ")");
				this.count++;
				if (this.count >= move_random_shreshold) {
					this.count = 0;
					res.add(start);
					List<EntityID> neiList = this.nodeMap.get(start)
							.getNeighbours();
					res.add(neiList.get(this.rand.nextInt(neiList.size())));
					return res;
				}
				return null;
			}

			// それ以降先に進めないと判断したとき
			if (candidateMap.size() == 0) {
				unchecked.remove(current);
				double min = Integer.MAX_VALUE;
				for (EntityID id : unchecked.keySet()) {
					double val = fMap.get(id);
					if (val < min) {
						min = val;
						current = id;
					}
				}
				continue;
			}

			EntityID next = null;
			double min = Integer.MAX_VALUE;
			double g = Integer.MAX_VALUE;
			for (EntityID id : candidateMap.keySet()) {
				double tmp_g = gMap.get(current)
						+ this.nodeMap.get(id).getCost(
								this.nodeMap.get(current));
				double val = tmp_g + candidateMap.get(id);
				if (val < min) {
					min = val;
					g = tmp_g;
					next = id;
				}
			}
			if (next == null) {
				this.count++;
				if (this.count >= move_random_shreshold) {
					this.count = 0;
					res.add(start);
					List<EntityID> neiList = this.nodeMap.get(start)
							.getNeighbours();
					res.add(neiList.get(this.rand.nextInt(neiList.size())));
					return res;
				}
				return null;
			}
			fMap.put(next, min);
			gMap.put(next, g);
			routeMap.put(next, current);
			unchecked.put(next, candidateMap.size());
			if (candidateMap.size() == 1) {
				unchecked.remove(current);
			}

			// decide next check node
			min = Integer.MAX_VALUE;
			for (EntityID id : unchecked.keySet()) {
				double val = fMap.get(id);
				if (val < min) {
					min = val;
					current = id;
				}
			}
		}
		res.add(goal);
		while (!current.equals(start)) {
			current = routeMap.get(current);
			res.add(0, current);
		}
		this.count = 0;
		return res;
	}

	public List<EntityID> getRoute(EntityID start, EntityID goal) {
		List<EntityID> res = new ArrayList<EntityID>();
		if (start.equals(goal)) {
			res.add(start);
			this.count = 0;
			return res;
		}
		Map<EntityID, Double> gMap = new HashMap<EntityID, Double>();
		Map<EntityID, Double> fMap = new HashMap<EntityID, Double>();
		Map<EntityID, EntityID> routeMap = new HashMap<EntityID, EntityID>();
		Map<EntityID, Integer> unchecked = new HashMap<EntityID, Integer>();
		EntityID current = start;
		gMap.put(current, 0d);
		fMap.put(current, new Double(this.model.getDistance(current, goal)));
		try {
			unchecked.put(current, this.nodeMap.get(current).getNeighbours()
					.size());
		} catch (Exception e) {
			System.err.println(this.nodeMap);
			System.err.println(current);
			return null;
		}
		while (!current.equals(goal)) {
			Map<EntityID, Integer> candidateMap = new HashMap<EntityID, Integer>();
			// for (EntityID id : this.nodeMap.get(current).getNeighbours()) {
			for (EntityID id : this.nodeMap.get(current)
					.getPassableNeighbours()) {
				if (!fMap.containsKey(id)) {
					candidateMap.put(id, this.model.getDistance(id, goal));
				}
			}
			if (unchecked.size() + candidateMap.size() == 0) {
				System.err.println("Could not find route.:" + "from(" + start
						+ ")" + ",to(" + goal + ")");
				this.count++;
				if (this.count >= move_random_shreshold) {
					this.count = 0;
					res.add(start);
					List<EntityID> neiList = this.nodeMap.get(start)
							.getNeighbours();
					res.add(neiList.get(this.rand.nextInt(neiList.size())));
					return res;
				}
				return null;
			}

			// それ以降先に進めないと判断したとき
			if (candidateMap.size() == 0) {
				unchecked.remove(current);
				double min = Integer.MAX_VALUE;
				for (EntityID id : unchecked.keySet()) {
					double val = fMap.get(id);
					if (val < min) {
						min = val;
						current = id;
					}
				}
				continue;
			}

			EntityID next = null;
			double min = Integer.MAX_VALUE;
			double g = Integer.MAX_VALUE;
			for (EntityID id : candidateMap.keySet()) {
				double tmp_g = gMap.get(current)
						+ this.nodeMap.get(id).getCost(
								this.nodeMap.get(current));
				double val = tmp_g + candidateMap.get(id);
				if (val < min) {
					min = val;
					g = tmp_g;
					next = id;
				}
			}
			if (next == null) {
				this.count++;
				if (this.count >= move_random_shreshold) {
					this.count = 0;
					res.add(start);
					List<EntityID> neiList = this.nodeMap.get(start)
							.getNeighbours();
					res.add(neiList.get(this.rand.nextInt(neiList.size())));
					return res;
				}
				return null;
			}
			fMap.put(next, min);
			gMap.put(next, g);
			routeMap.put(next, current);
			unchecked.put(next, candidateMap.size());
			if (candidateMap.size() == 1) {
				unchecked.remove(current);
			}

			// decide next check node
			min = Integer.MAX_VALUE;
			for (EntityID id : unchecked.keySet()) {
				double val = fMap.get(id);
				if (val < min) {
					min = val;
					current = id;
				}
			}
		}
		res.add(goal);
		while (!current.equals(start)) {
			current = routeMap.get(current);
			res.add(0, current);
		}
		this.count = 0;
		return res;
	}

	public List<EntityID> getRoute(EntityID start, List<EntityID> goals) {
		List<EntityID> res = new ArrayList<EntityID>();
		if (goals.contains(start)) {
			res.add(start);
			this.count = 0;
			return res;
		}
		Map<EntityID, Double> gMap = new HashMap<EntityID, Double>();
		Map<EntityID, Double> fMap = new HashMap<EntityID, Double>();
		Map<EntityID, EntityID> routeMap = new HashMap<EntityID, EntityID>();
		Map<EntityID, Integer> unchecked = new HashMap<EntityID, Integer>();
		EntityID current = start;
		gMap.put(current, 0d);
		// fMap.put(current, new Double(this.model.getDistance(current, goal)));
		fMap.put(current, new Double(this.getMinDistance(current, goals)));
		try {
			unchecked.put(current, this.nodeMap.get(current).getNeighbours()
					.size());
		} catch (Exception e) {
			System.err.println(this.nodeMap);
			System.err.println(current);
			return null;
		}
		while (!goals.contains(current)) {
			Map<EntityID, Integer> candidateMap = new HashMap<EntityID, Integer>();
			// for (EntityID id : this.nodeMap.get(current).getNeighbours()) {
			for (EntityID id : this.nodeMap.get(current)
					.getPassableNeighbours()) {
				if (!fMap.containsKey(id)) {
					candidateMap.put(id, this.getMinDistance(id, goals));
				}
			}
			if (unchecked.size() + candidateMap.size() == 0) {
				System.err.println("Could not find route.");
				this.count++;
				if (this.count >= move_random_shreshold) {
					this.count = 0;
					res.add(start);
					List<EntityID> neiList = this.nodeMap.get(start)
							.getNeighbours();
					res.add(neiList.get(this.rand.nextInt(neiList.size())));
					return res;
				}
				return null;
			}

			// それ以降先に進めないと判断したとき
			if (candidateMap.size() == 0) {
				unchecked.remove(current);
				double min = Integer.MAX_VALUE;
				for (EntityID id : unchecked.keySet()) {
					double val = fMap.get(id);
					if (val < min) {
						min = val;
						current = id;
					}
				}
				continue;
			}

			EntityID next = null;
			double min = Integer.MAX_VALUE;
			double g = Integer.MAX_VALUE;
			for (EntityID id : candidateMap.keySet()) {
				double tmp_g = gMap.get(current)
						+ this.nodeMap.get(id).getCost(
								this.nodeMap.get(current));
				double val = tmp_g + candidateMap.get(id);
				if (val < min) {
					min = val;
					g = tmp_g;
					next = id;
				}
			}
			if (next == null) {
				this.count++;
				if (this.count >= move_random_shreshold) {
					this.count = 0;
					res.add(start);
					List<EntityID> neiList = this.nodeMap.get(start)
							.getNeighbours();
					res.add(neiList.get(this.rand.nextInt(neiList.size())));
					return res;
				}
				return null;
			}
			fMap.put(next, min);
			gMap.put(next, g);
			routeMap.put(next, current);
			unchecked.put(next, candidateMap.size());
			if (candidateMap.size() == 1) {
				unchecked.remove(current);
			}

			// decide next check node
			min = Integer.MAX_VALUE;
			for (EntityID id : unchecked.keySet()) {
				double val = fMap.get(id);
				if (val < min) {
					min = val;
					current = id;
				}
			}
		}
		res.add(current);
		while (!current.equals(start)) {
			current = routeMap.get(current);
			res.add(0, current);
		}
		this.count = 0;
		return res;
	}

	@Override
	public void setUnpassable(EntityID from, EntityID to) {
		// 双方向に定義しないと経路計算時に思った通りの結果を得ることができない．
		this.nodeMap.get(from).setUnpassable(to);
		this.nodeMap.get(to).setUnpassable(from);
	}

	@Override
	public void setPassable(EntityID from, EntityID to) {
		// 双方向に定義しないと経路計算時に思った通りの結果を得ることができない．
		this.nodeMap.get(from).setPassable(to);
		this.nodeMap.get(to).setPassable(from);
	}

	private int getMinDistance(EntityID start, List<EntityID> goals) {
		int res = Integer.MAX_VALUE;
		for (EntityID id : goals) {
			int dis = this.model.getDistance(start, id);
			if (dis < res) {
				res = dis;
			}
		}
		return res;
	}

	public Map<EntityID, SuntoriNode> getNodeMap() {
		return this.nodeMap;
	}
}
