package jp.ac.nagoyau.is.ss.kishii.ob.route;

import java.util.List;

import rescuecore2.worldmodel.EntityID;

public interface ISuntoriRouter {
	/**
	 * 経路を計算する．
	 * 
	 * @param start
	 *            出発地点のEntityID
	 * @param goal
	 *            目標地点のEntityID
	 * @return 経路(EntityIDのリスト)
	 */
	public List<EntityID> getRoute(EntityID start, EntityID goal);

	public List<EntityID> getRoute(EntityID start, List<EntityID> goals);

	/**
	 * 通行不可を設定する．
	 * 
	 * @param id
	 */
	public void setUnpassable(EntityID from, EntityID to);

	/**
	 * 通行不可を解除する．
	 * 
	 * @param id
	 */
	public void setPassable(EntityID from, EntityID to);
}
