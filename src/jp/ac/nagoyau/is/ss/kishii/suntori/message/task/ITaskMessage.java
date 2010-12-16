package jp.ac.nagoyau.is.ss.kishii.suntori.message.task;

import rescuecore2.worldmodel.EntityID;

public interface ITaskMessage {
	/**
	 * タスクを実行すべきエージェントのEntityID(platoon)を取得します．
	 * 
	 * @return エージェントのEntityID
	 */
	public EntityID getAssignedAgentID();
}
