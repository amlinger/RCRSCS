package jp.ac.nagoyau.is.ss.kishii.ob.primitive.svm;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import jp.ac.nagoyau.is.ss.kishii.ob.primitive.FireSite;

public class SiteGroup {
	private Set<FireSite> siteList;

	public SiteGroup() {
		siteList = new HashSet<FireSite>();
	}

	public void add(FireSite site) {
		this.siteList.add(site);
	}

	public Set<FireSite> getContainedFireSite() {
		return this.siteList;
	}

	public void addAll(Collection<FireSite> sites) {
		this.siteList.addAll(sites);
	}

	public boolean contains(FireSite site) {
		return this.siteList.contains(site);
	}

	public int size() {
		return this.siteList.size();
	}
}
