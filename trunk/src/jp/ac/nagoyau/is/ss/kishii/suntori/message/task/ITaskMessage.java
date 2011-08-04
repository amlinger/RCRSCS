package jp.ac.nagoyau.is.ss.kishii.suntori.message.task;

import rescuecore2.worldmodel.EntityID;

public interface ITaskMessage {
	/**
	 * このタスクを実行すべきエージェントのEntityIDを取得します．<br>
	 * Return EntityID of agent that have to execute this task.
	 * 
	 * @return エージェントのEntityID<br>
	 *         EntityID of agent
	 */
	public EntityID getAssignedAgentID();
}
