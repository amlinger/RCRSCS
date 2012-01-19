package jp.ac.nagoyau.is.ss.kishii.ob.team.sample;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jp.ac.nagoyau.is.ss.kishii.ob.primitive.ChannelData;
import jp.ac.nagoyau.is.ss.kishii.ob.primitive.ConfigConstants;
import jp.ac.nagoyau.is.ss.kishii.ob.primitive.RadioChannelData;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.information.AmbulanceTeamInformation;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.information.BlockadeInformation;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.information.BuildingInformation;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.information.FireBrigadeInformation;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.information.PoliceForceInformation;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.information.TransferInformation;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.information.VictimInformation;
import rescuecore2.Constants;
import rescuecore2.messages.Command;
import rescuecore2.standard.entities.AmbulanceTeam;
import rescuecore2.standard.entities.Blockade;
import rescuecore2.standard.entities.Building;
import rescuecore2.standard.entities.Civilian;
import rescuecore2.standard.entities.FireBrigade;
import rescuecore2.standard.entities.Human;
import rescuecore2.standard.entities.PoliceForce;
import rescuecore2.standard.entities.StandardEntity;
import rescuecore2.standard.kernel.comms.ChannelCommunicationModel;
import rescuecore2.worldmodel.ChangeSet;
import rescuecore2.worldmodel.EntityID;

public abstract class AbstractOBSampleAgent<E extends Human> extends
		AbstractSampleAgent<E> {

	protected List<EntityID> path;
	protected ConfigConstants constants;

	public AbstractOBSampleAgent() {
		super();
		this.path = null;
	}

	@Override
	protected void postConnect() {
		// TODO 自動生成されたメソッド・スタブ
		super.postConnect();
		this.constants = new ConfigConstants(this.config);
		this.useSpeak = config.getValue(Constants.COMMUNICATION_MODEL_KEY)
				.equals(ChannelCommunicationModel.class.getName());
		if (useSpeak) {
			choiseMessageChannel(constants);
		}
	}

	/**
	 * メッセージコンバータが使用するチャンネルを選択する．<br>
	 * 現在は，一番容量が多いものを選択する．
	 * 
	 * @param con
	 */
	private void choiseMessageChannel(ConfigConstants con) {
		int max = Integer.MIN_VALUE;
		ChannelData selected = null;
		for (ChannelData data : con.channelSettings) {
			if (data instanceof RadioChannelData) {
				int size = data.getMaxMessageSize();
				if (max < size) {
					max = size;
					selected = data;
				}
			}
		}
		if (selected != null) {
			this.setMessageChannel(selected.getChannelNumber());
		}
	}

	@Override
	protected void thinking(int arg0, ChangeSet arg1, Collection<Command> arg2) {
		if (arg0 == this.constants.START_ACTION_TIME) {
			int channel = this.getMessageChannel();
			this.sendSubscribe(arg0, channel);
		}
		if (arg0 >= this.constants.START_ACTION_TIME) {
			this.setMessage(arg0, arg1);
			this.thinking2(arg0, arg1, arg2);
		}
	}

	protected abstract void thinking2(int time, ChangeSet changed,
			Collection<Command> heard);

	/**
	 * 状況報告ようのメッセージ登録
	 */
	protected void setMessage(int time, ChangeSet changed) {
		E self = this.me();
		this.setInSightInformation(time, changed, self);
		this.setSelfInformation(time, self);
		this.setTransferData(time, self);
	}

	/**
	 * 自分の移動の軌跡情報をメッセージにセットする．
	 * 
	 * @param time
	 */
	protected void setTransferData(int time, E self) {
		if (this.path != null && this.path.size() > 0) {
			int index = this.path.indexOf(self.getPosition());
			if (index > 0) {
				this.addMessage(new TransferInformation(
						time,
						self.getID(),
						new ArrayList<EntityID>(this.path.subList(0, index + 1))));
			}
		}
	}

	/**
	 * 自身の情報
	 * 
	 * @param time
	 * @param self
	 */
	private void setSelfInformation(int time, E self) {
		if (self instanceof FireBrigade) {
			FireBrigade fb = (FireBrigade) self;
			this.addMessage(new FireBrigadeInformation(time, fb.getID(), fb
					.getHP2(), fb.getDamage(), fb.getBuriedness2(), fb
					.getWater2(), self.getPosition()));
		} else if (self instanceof AmbulanceTeam) {
			this.addMessage(new AmbulanceTeamInformation(time, self.getID(),
					self.getHP2(), self.getDamage(), self.getBuriedness2(),
					self.getPosition()));
		} else if (self instanceof PoliceForce) {
			this.addMessage(new PoliceForceInformation(time, self.getID(), self
					.getHP2(), self.getDamage(), self.getBuriedness2(), self
					.getPosition()));
		}
	}

	/**
	 * 視覚に関する情報
	 * 
	 * @param time
	 * @param changed
	 */
	private void setInSightInformation(int time, ChangeSet changed, E self) {
		for (EntityID id : changed.getChangedEntities()) {
			StandardEntity se = this.model.getEntity(id);
			if (se instanceof Building) {
				this.addBuildingInformation(time, (Building) se);
			} else if (se instanceof Blockade) {
				if (!(self instanceof PoliceForce)) {
					this.addBlockadeInformation(time, (Blockade) se);
				}
			} else if (se instanceof Civilian) {
				this.addVictimInformation(time, (Civilian) se);
			}
		}
	}

	private void addBuildingInformation(int time, Building b) {
		BuildingInformation info = new BuildingInformation(time, b.getID(),
				b.getFieryness2(), b.getBrokenness2());
		this.addMessage(info);
	}

	private void addBlockadeInformation(int time, Blockade b) {
		BlockadeInformation info = new BlockadeInformation(time, b.getID(),
				b.getPosition(), b.getRepairCost());
		this.addMessage(info);
	}

	private void addVictimInformation(int time, Civilian civ) {
		if (!(this.model.getEntity(civ.getPosition()) instanceof AmbulanceTeam)) {
			VictimInformation info = new VictimInformation(time, civ.getID(),
					civ.getPosition(), civ.getHP2(), civ.getBuriedness2(),
					civ.getDamage());
			this.addMessage(info);
		}
	}

	@Override
	protected void sendMove(int time, List<EntityID> path, int destX, int destY) {
		super.sendMove(time, path, destX, destY);
		this.path = path;
	}

	@Override
	protected void sendMove(int time, List<EntityID> path) {
		super.sendMove(time, path);
		this.path = path;
	}
}
