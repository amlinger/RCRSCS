package jp.ac.nagoyau.is.ss.kishii.suntori.message.components;

import java.util.Collection;

import rescuecore2.messages.Command;
import rescuecore2.standard.entities.StandardEntity;
import rescuecore2.worldmodel.ChangeSet;

/**
 * この通信ライブラリを使用したAgentを表すクラスです.<br>
 * The AbstractCSAgent show the Agent using this communication library.
 * 
 * @author takefumi
 * 
 * @param <E>
 */
public abstract class AbstractCSAgent<E extends StandardEntity> extends
		RCRSCSAgent<E> {
	/**
	 * コンストラクタ<br>
	 * Constructor
	 */
	public AbstractCSAgent() {
		super();
	}

	@Override
	/**
	 * 各ステップの最初に受信したデータの中で，チャンネルがメッセージ受信チャンネルであるものをコンバートしています．<br>
	 * Represent each step thinking.<br>
	 * (1.Receive message,2.think,3.send new messages)
	 */
	protected final void think(int time, ChangeSet changed,
			Collection<Command> heard) {
		super.receiveMessage(heard);
		this.thinking(time, changed, heard);
		super.sendMessage(time);
	}

	/**
	 * ユーザ実装部<br>
	 * この時点で，受信したデータの中でチャンネルがメッセージ受信チャンネルであるものをコンバートしています．<br>
	 * ユーザはreceivedMessageListの中身を見ればメッセージが見れる状態にあります．<br>
	 * Development part that have to be created by users.<br>
	 * At this point,we already receive messages from other Agents. Received
	 * messages are in 'receivedMessageList'.
	 */
	protected abstract void thinking(int time, ChangeSet changed,
			Collection<Command> heard);
}
