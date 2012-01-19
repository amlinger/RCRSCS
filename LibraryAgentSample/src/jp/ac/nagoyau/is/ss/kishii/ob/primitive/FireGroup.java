package jp.ac.nagoyau.is.ss.kishii.ob.primitive;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rescuecore2.standard.entities.StandardWorldModel;

public class FireGroup {
	List<SuntoriBuilding> fires;
	List<SuntoriBuilding> nears;

	public FireGroup() {
		this.fires = new ArrayList<SuntoriBuilding>();
		this.nears = new ArrayList<SuntoriBuilding>();
	}

	public void add(SuntoriBuilding sb) {
		fires.add(sb);
		nears.addAll(sb.getConnectingBuilding());
	}

	public void addAll(Collection<SuntoriBuilding> fires) {
		this.fires.addAll(fires);
		for (SuntoriBuilding sb : fires) {
			this.nears.addAll(sb.getConnectingBuilding());
		}
	}

	public boolean contains(SuntoriBuilding sb) {
		return this.fires.contains(sb);
	}

	public List<SuntoriBuilding> getAllFires() {
		return this.fires;
	}

	/**
	 * 火災クラスタの近辺の建物群を取得
	 * 
	 * @return
	 */
	public Collection<SuntoriBuilding> getNearBuilding() {
		List<SuntoriBuilding> res = new ArrayList<SuntoriBuilding>();
		for (SuntoriBuilding sb : this.fires) {
			res.addAll(sb.getConnectingBuilding());
		}
		return res;
	}

	/**
	 * 火災クラスタ内にある燃焼していない建物を取得
	 * 
	 * @return
	 */
	public Collection<SuntoriBuilding> getUnburnedOrPutoutBuilding() {
		List<SuntoriBuilding> res = new ArrayList<SuntoriBuilding>(
				this.getNearBuilding());
		res.removeAll(this.fires);
		for (int i = res.size(); i >= 0; i--) {
			if (res.get(i).isBurning() || res.get(i).getFieryness() == 8) {
				res.remove(i);
			}
		}
		return res;
	}

	public int totalUnburnedArea() {
		int res = 0;
		for (SuntoriBuilding sb : this.nears) {
			if (sb.isUnburned() || sb.isPutout()) {
				res += sb.getGroundArea();
			}
		}
		return res;
	}

	public int totalArea() {
		int res = 0;
		for (SuntoriBuilding sb : this.nears) {
			res += sb.getGroundArea();
		}
		return res;
	}

	public int totalBurningFieryness() {
		int res = 0;
		for (SuntoriBuilding sb : this.nears) {
			if (sb.isBurning()) {
				res += sb.getFieryness();
			}
		}
		return res;
	}

	public int centerX() {
		int sum = 0;
		for (SuntoriBuilding sb : this.fires) {
			sum += sb.getX();
		}
		return sum / this.fires.size();
	}

	public int centerY() {
		int sum = 0;
		for (SuntoriBuilding sb : this.fires) {
			sum += sb.getY();
		}
		return sum / this.fires.size();
	}

	public FireSite getNearestFireSite(List<FireSite> fireSites,
			StandardWorldModel model) {
		double min = Double.MAX_VALUE;
		FireSite res = null;
		for (FireSite fs : fireSites) {
			double dx = fs.getX(model) - this.centerX();
			double dy = fs.getY(model) - this.centerY();
			double dis = (dx * dx) + (dy * dy);
			if (min > dis) {
				min = dis;
				res = fs;
			}
		}
		return res;
	}

	public Collection<FireSite> getContainFireSite(
			Map<SuntoriBuilding, FireSite> siteMap) {
		Set<FireSite> res = new HashSet<FireSite>();
		for (SuntoriBuilding sb : this.getAllFires()) {
			res.add(siteMap.get(sb));
		}
		return res;
	}

	public double distance(int x, int y) {
		return Util.getDistance(centerX(), centerY(), x, y);
	}
}
