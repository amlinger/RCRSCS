package jp.ac.nagoyau.is.ss.kishii.ob.primitive;

import java.util.ArrayList;
import java.util.List;

import rescuecore2.misc.Pair;

/**
 * 一区画を表現<br>
 * 中身は区画に含まれるFireSiteのIDが入る
 * 
 * @author takefumi
 * 
 */
public class District {
	private List<Integer> siteIDList;
	private Pair<Integer, Integer> center;

	public District() {
		super();
		this.siteIDList = new ArrayList<Integer>();
	}

	public List<Integer> getSiteList() {
		return this.siteIDList;
	}

	public void setCenter(int x, int y) {
		this.center = new Pair<Integer, Integer>(x, y);
	}

	public void setCenter(Pair<Integer, Integer> center) {
		this.center = center;
	}

	public Pair<Integer, Integer> getCenter() {
		return this.center;
	}
}
