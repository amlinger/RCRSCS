package jp.ac.nagoyau.is.ss.kishii.suntori.message.components;

import java.util.Collection;

import rescuecore2.messages.Command;
import rescuecore2.standard.entities.StandardEntity;
import rescuecore2.worldmodel.ChangeSet;

/**
 * エージェントのabstractクラス．
 * 
 * @author takefumi
 * 
 * @param <E>
 */
public abstract class AbstractCSAgent<E extends StandardEntity> extends
		RCRSCSAgent<E> {
	/**
	 * コンストラクタ
	 */
	public AbstractCSAgent() {
		super();
	}

	@Override
	/**
	 * 各ステップの最初に受信したデータの中で，チャンネルがメッセージ受信チャンネルであるものをコンバートしています．
	 */
	protected void think(int time, ChangeSet changed, Collection<Command> heard) {
		super.receiveMessage(heard);
		this.thinking(time, changed, heard);
	}

	/**
	 * ユーザ実装部<br>
	 * この時点で，受信したデータの中でチャンネルがメッセージ受信チャンネルであるものをコンバートしています．<br>
	 * ユーザはreceivedMessageListの中身を見ればメッセージが見れる状態にあります．
	 * 
	 * @param time
	 * @param changed
	 * @param heard
	 */
	protected abstract void thinking(int time, ChangeSet changed,
			Collection<Command> heard);
}
