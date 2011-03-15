package jp.ac.nagoyau.is.ss.kishii.suntori.message.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jp.ac.nagoyau.is.ss.kishii.suntori.message.RCRSCSMessage;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.RCRSCSMessageConverter;
import rescuecore2.messages.Command;
import rescuecore2.standard.components.StandardAgent;
import rescuecore2.standard.entities.StandardEntity;
import rescuecore2.standard.messages.AKSpeak;

/**
 * メッセージ通信ライブラリを使用して通信するエージェント用のabstractエージェント
 * 
 * @author takefumi
 * 
 * @param <E>
 */
abstract class RCRSCSAgent<E extends StandardEntity> extends StandardAgent<E> {
	RCRSCSMessageConverter messageConverter;
	private List<RCRSCSMessage> messageList;
	protected List<RCRSCSMessage> receivedMessageList;
	private int messageChannel;

	/**
	 * コンストラクタ
	 */
	public RCRSCSAgent() {
		this.messageList = new ArrayList<RCRSCSMessage>();
		this.receivedMessageList = new ArrayList<RCRSCSMessage>();
		this.messageChannel = -1;
	}

	@Override
	protected void postConnect() {
		super.postConnect();
		this.messageConverter = new RCRSCSMessageConverter(me().getID(),
				this.model, this.config);
	}

	/**
	 * メッセージ送受信チャンネルを設定します．
	 * 
	 * @param channel
	 *            送受信チャンネル
	 */
	protected final void setMessageChannel(int channel) {
		this.messageChannel = channel;
	}

	/**
	 * 現在設定されている送受信チャンネルを取得します．
	 * 
	 * @return 送受信チャンネル
	 */
	protected final int getMessageChannel() {
		return this.messageChannel;
	}

	/**
	 * メッセージを受信します．<br>
	 * 送受信チャンネルに設定されているチャンネルから送られてきたデータに対してコンバートを行います．
	 * 
	 * @param heard
	 */
	protected final void receiveMessage(Collection<Command> heard) {
		this.receivedMessageList = new ArrayList<RCRSCSMessage>();
		for (Command command : heard) {
			if (command instanceof AKSpeak) {
				AKSpeak speak = (AKSpeak) command;
				// System.out.println("bit size " + speak.getContent().length *
				// 8);
				if (speak.getChannel() == this.messageChannel) {
					this.receivedMessageList.addAll(this.messageConverter
							.bytesToMessageList(speak.getContent()));
				}
			}
		}
	}

	/**
	 * 送信するメッセージを追加します．
	 * 
	 * @param message
	 */
	protected final void addMessage(RCRSCSMessage message) {
		messageList.add(message);
	}

	/**
	 * 追加されたメッセージを送信します．
	 * 
	 * @param time
	 */
	protected final void sendMessage(int time) {
		byte[] data = this.messageConverter.messageToBytes(this.messageList);
		if (data != null) {
			super.sendSpeak(time, this.messageChannel, data);
		}
	}

	@Override
	/**
	 * チャンネルがメッセージ送受信チャンネルであった場合，メッセージ送信を行い，
	 * 異なった場合，通常のsendSpeakを行います．
	 */
	protected final void sendSpeak(int time, int channel, byte[] data) {
		if (channel == this.messageChannel) {
			this.sendMessage(time);
		} else {
			super.sendSpeak(time, this.messageChannel, data);
		}
	}
}