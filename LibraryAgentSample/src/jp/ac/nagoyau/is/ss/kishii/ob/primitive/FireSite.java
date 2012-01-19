package jp.ac.nagoyau.is.ss.kishii.ob.primitive;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rescuecore2.misc.Pair;
import rescuecore2.standard.entities.Area;
import rescuecore2.standard.entities.Edge;
import rescuecore2.standard.entities.Road;
import rescuecore2.standard.entities.StandardEntity;
import rescuecore2.standard.entities.StandardWorldModel;
import rescuecore2.worldmodel.EntityID;

public class FireSite {
	private Set<SuntoriBuilding> buildingSet;

	private int centerX;
	private int centerY;

	private int effectedFireSiteCount;

	private boolean burnout;

	private List<FireSite> siteWithin150m;
	private Set<FireSite> connectionSite;

	public FireSite() {
		this.buildingSet = new HashSet<SuntoriBuilding>();
		this.centerX = -1;
		this.centerY = -1;
		this.effectedFireSiteCount = 0;
		this.burnout = false;
		this.siteWithin150m = new ArrayList<FireSite>();
		this.connectionSite = new HashSet<FireSite>();
	}

	public void add(SuntoriBuilding sb) {
		this.buildingSet.add(sb);
	}

	public void addAll(Collection<SuntoriBuilding> col) {
		this.buildingSet.addAll(col);
	}

	public void merge(FireSite fs) {
		this.buildingSet.addAll(fs.getBuildings());
	}

	public Set<SuntoriBuilding> getBuildings() {
		return this.buildingSet;
	}

	public boolean contains(EntityID id) {
		boolean res = false;
		for (SuntoriBuilding sb : this.buildingSet) {
			if (sb.getID().equals(id)) {
				res = true;
			}
		}
		return res;
	}

	private void init(StandardWorldModel model) {
		if (this.centerX < 0 || this.centerY < 0) {
			this.centerX = 0;
			this.centerY = 0;
			for (SuntoriBuilding sb : this.buildingSet) {
				Pair<Integer, Integer> location = sb.getLocation(model);
				this.centerX += location.first();
				this.centerY += location.second();
			}
			this.centerX /= this.buildingSet.size();
			this.centerY /= this.buildingSet.size();
		}
	}

	public int getX(StandardWorldModel model) {
		init(model);
		return this.centerX;
	}

	public int getY(StandardWorldModel model) {
		init(model);
		return this.centerY;
	}

	/**
	 * FireSiteを作成したら以下を行う必要あり.<br>
	 * for (int i = 0; i < this.fireSites.size(); i++) {<br>
	 * FireSite fs0 = this.fireSites.get(i);<br>
	 * for (int j = i; j < this.fireSites.size(); j++) {<br>
	 * FireSite fs1 = this.fireSites.get(j);<br>
	 * if (fs0.distance(fs1, this.model) < 150 * 000) {<br>
	 * fs0.getSiteWithin150m().add(fs1);<br>
	 * fs1.getSiteWithin150m().add(fs0);<br>
	 * }<br>
	 * }<br>
	 * }<br>
	 * for (SuntoriBuilding sb : this.sbList) {<br>
	 * FireSite fs = this.siteMap.get(sb.getID());<br>
	 * for (SuntoriBuilding sb2 : sb.getDirectConnectBuilding()) {<br>
	 * FireSite fs2 = this.siteMap.get(sb2.getID());<br>
	 * if (this.fireSites.indexOf(fs) != this.fireSites<br>
	 * .indexOf(fs2)) {<br>
	 * fs.getConnectionFireSite().add(fs2);<br>
	 * fs2.getConnectionFireSite().add(fs);<br>
	 * }<br>
	 * }<br>
	 * }<br>
	 * 
	 * @param model
	 * @param sbList
	 * @return
	 */
	public static List<FireSite> createFireSites(StandardWorldModel model,
			List<SuntoriBuilding> sbList) {
		List<FireSite> res = new ArrayList<FireSite>();
		List<SuntoriBuilding> buildings = new ArrayList<SuntoriBuilding>(sbList);
		while (buildings.size() > 0) {
			FireSite newGroup = new FireSite();
			SuntoriBuilding sb = buildings.get(0);
			buildings.remove(0);
			newGroup.add(sb);
			checkCluster(buildings, newGroup, sb, model);
			res.add(newGroup);
		}

		return res;
	}

	private static void checkCluster(List<SuntoriBuilding> unchecked,
			FireSite group, SuntoriBuilding tar, StandardWorldModel model) {
		final int SITE_LINE = 3000;
		List<SuntoriBuilding> newMember = new ArrayList<SuntoriBuilding>(
				unchecked);
		List<SuntoriBuilding> connectedBuilding = new ArrayList<SuntoriBuilding>(
				tar.getDirectConnectBuilding());

		for (int i = tar.getDirectConnectBuilding().size() - 1; i >= 0; i--) {
			Double dis1 = tar.getDistance(connectedBuilding.get(i));
			Double dis2 = connectedBuilding.get(i).getDistance(tar);

			if ((dis1 != null && dis1 < SITE_LINE)
					|| (dis2 != null && dis2 < SITE_LINE)) {
			} else {
				connectedBuilding.remove(i);
			}
		}

		Pair<Integer, Integer> tarLoc = tar.getLocation(model);
		LOOP: for (int i = connectedBuilding.size() - 1; i >= 0; i--) {
			SuntoriBuilding sb = connectedBuilding.get(i);
			Pair<Integer, Integer> sbLoc = sb.getLocation(model);
			Pair<Integer, Integer> cp = Util.getCenterOf(tarLoc, sbLoc);
			Double dis = tar.getDistance(sb);
			List<Area> roads = new ArrayList<Area>();
			for (StandardEntity se : model.getObjectsInRange(cp.first(),
					cp.second(), (int) (dis / 2))) {
				if (se instanceof Road) {
					roads.add((Area) se);
				}
			}
			Edge edge = new Edge(tarLoc.first(), tarLoc.second(),
					sbLoc.first(), sbLoc.second());
			for (Area a : roads) {
				for (Edge e : a.getEdges()) {
					if (Util.isCross(edge, e)) {
						connectedBuilding.remove(sb);
						continue LOOP;
					}
				}
			}
		}
		newMember.retainAll(connectedBuilding);
		// for (SuntoriBuilding sb : newMember) {
		// if (sb.getID().getValue() == 944) {
		// System.out.println(tar.getID());
		// System.out.println(tar.getDistance(sb));
		// }
		// }
		if (newMember.size() != 0) {
			unchecked.removeAll(newMember);
			for (SuntoriBuilding sb : newMember) {
				group.add(sb);
			}
			for (SuntoriBuilding sb : newMember) {
				checkCluster(unchecked, group, sb, model);
			}
		}
	}

	/**
	 * FireSiteの評価を行う。<br>
	 * 評価値が高いほど重要と判断
	 * 
	 * @param model
	 * @return
	 */
	public int calcEvaluationValue(StandardWorldModel model) {
		int res = 0;

		return res;
	}

	public static Map<SuntoriBuilding, FireSite> createSiteMap(
			List<FireSite> fireSites, List<SuntoriBuilding> sbList) {
		Map<SuntoriBuilding, FireSite> res = new HashMap<SuntoriBuilding, FireSite>();
		for (SuntoriBuilding sb : sbList) {
			for (FireSite fs : fireSites) {
				if (fs.contains(sb.getID())) {
					res.put(sb, fs);
				}
			}
		}
		return res;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((buildingSet == null) ? 0 : buildingSet.hashCode());
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
		FireSite other = (FireSite) obj;
		if (buildingSet == null) {
			if (other.buildingSet != null)
				return false;
		} else if (!buildingSet.equals(other.buildingSet))
			return false;
		return true;
	}

	public void setEffectedFireSiteCount(int count) {
		this.effectedFireSiteCount = count;
	}

	public int getEffectedFireSiteCount() {
		return this.effectedFireSiteCount;
	}

	public int getLivingFireSiteAreaAsEffectSites() {
		int sum = 0;
		for (FireSite fs : this.siteWithin150m) {
			sum += fs.totalLivingArea() / (fs.getEffectedFireSiteCount() + 1);
		}
		sum += totalLivingArea() / (1 + getEffectedFireSiteCount());
		return sum;
	}

	public int totalLivingArea() {
		int sum = 0;
		if (this.burnout) {
			return sum;
		}
		for (SuntoriBuilding sb : this.buildingSet) {
			if (sb.isUnburned() || sb.isPutout()) {
				sum += sb.getGroundArea();
			}
		}
		if (sum == 0) {
			this.burnout = true;
		}
		return sum;
	}

	public int toatlBuildingArea() {
		int sum = 0;
		for (SuntoriBuilding sb : this.buildingSet) {
			sum += sb.getGroundArea();
		}
		return sum;
	}

	public List<SuntoriBuilding> getBurningBuilding() {
		List<SuntoriBuilding> res = new ArrayList<SuntoriBuilding>();
		for (SuntoriBuilding b : this.buildingSet) {
			if (b.isBurning()) {
				res.add(b);
			}
		}
		return res;
	}

	public double distance(FireSite fs, StandardWorldModel model) {
		long dx = this.getX(model) - fs.getX(model);
		long dy = this.getY(model) - fs.getY(model);
		return Math.sqrt((dx * dx) + (dy * dy));
	}

	public List<FireSite> getSiteWithin150m() {
		return this.siteWithin150m;
	}

	public Set<FireSite> getConnectionFireSite() {
		return this.connectionSite;
	}
}
