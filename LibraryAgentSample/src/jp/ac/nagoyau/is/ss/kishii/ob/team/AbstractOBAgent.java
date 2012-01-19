package jp.ac.nagoyau.is.ss.kishii.ob.team;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import jp.ac.nagoyau.is.ss.kishii.ob.primitive.ChannelData;
import jp.ac.nagoyau.is.ss.kishii.ob.primitive.ConfigConstants;
import jp.ac.nagoyau.is.ss.kishii.ob.primitive.EntityIDComparator;
import jp.ac.nagoyau.is.ss.kishii.ob.primitive.RadioChannelData;
import jp.ac.nagoyau.is.ss.kishii.ob.route.ASter;
import jp.ac.nagoyau.is.ss.kishii.ob.route.ISuntoriRouter;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.RCRSCSMessage;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.components.AbstractCSAgent;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.information.TransferInformation;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.information.UnpassableInformation;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.information.WorldInformation;
import rescuecore2.Constants;
import rescuecore2.messages.Command;
import rescuecore2.standard.entities.AmbulanceCentre;
import rescuecore2.standard.entities.AmbulanceTeam;
import rescuecore2.standard.entities.FireBrigade;
import rescuecore2.standard.entities.PoliceForce;
import rescuecore2.standard.entities.PoliceOffice;
import rescuecore2.standard.entities.StandardEntity;
import rescuecore2.standard.entities.StandardEntityURN;
import rescuecore2.standard.kernel.comms.ChannelCommunicationModel;
import rescuecore2.worldmodel.ChangeSet;
import rescuecore2.worldmodel.EntityID;

public abstract class AbstractOBAgent<E extends StandardEntity> extends
		AbstractCSAgent<E> {

	/**
	 * 救急隊のEntityIDリスト
	 */
	protected List<EntityID> ambulanceTeamList;
	/**
	 * 消防隊のEntityIDリスト
	 */
	protected List<EntityID> fireBrigadeList;
	/**
	 * 啓開隊のEntityIDリスト
	 */
	protected List<EntityID> policeForceList;
	/**
	 * 小隊のEnttyIDリスト(at, fb, pf)
	 */
	protected List<EntityID> platoonList;
	protected List<EntityID> belongPlatoon;
	protected int roleIndex;
	/**
	 * ACのEntityIDリスト
	 */
	protected List<EntityID> ambulanceCenterList;
	/**
	 * FSのEntityIDリスト
	 */
	protected List<EntityID> fireStationList;
	/**
	 * POのEntityIDリスト
	 */
	protected List<EntityID> policeOfficeList;
	/**
	 * センターのEntityIDリスト
	 */
	protected List<EntityID> centerList;

	/**
	 * 道路のEntityIDリスト
	 */
	protected List<EntityID> roadList;
	/**
	 * 建物のEntityIDリスト(AC, FS, PO, Refugeは含まない)
	 */
	protected List<EntityID> buildingList;
	/**
	 * 避難所のEntityIDリスト
	 */
	protected List<EntityID> refugeList;

	/**
	 * エリアのEntityIDリスト(AC, FS, PO, Refugeを含む)
	 */
	protected List<EntityID> areaList;
	/**
	 * エージェントのリスト(Agent+Center)
	 */
	protected List<EntityID> rescueList;

	/**
	 * 経路探索アルゴリズム
	 */
	protected ISuntoriRouter router;

	/**
	 * 自分のEntityID
	 */
	protected EntityID myID;

	protected ConfigConstants constants;
	protected boolean useSpeak;
	protected List<UnpassableInformation> unpassableInfoList;

	/**
	 * コンストラクタ
	 */
	public AbstractOBAgent() {
		super();
		this.ambulanceTeamList = new ArrayList<EntityID>();
		this.fireBrigadeList = new ArrayList<EntityID>();
		this.policeForceList = new ArrayList<EntityID>();
		this.platoonList = new ArrayList<EntityID>();
		this.belongPlatoon = new ArrayList<EntityID>();
		this.ambulanceCenterList = new ArrayList<EntityID>();
		this.fireStationList = new ArrayList<EntityID>();
		this.policeOfficeList = new ArrayList<EntityID>();
		this.centerList = new ArrayList<EntityID>();
		this.roadList = new ArrayList<EntityID>();
		this.buildingList = new ArrayList<EntityID>();
		this.refugeList = new ArrayList<EntityID>();
		this.areaList = new ArrayList<EntityID>();
		this.rescueList = new ArrayList<EntityID>();
		this.unpassableInfoList = new ArrayList<UnpassableInformation>();
	}

	@Override
	protected void postConnect() {
		super.postConnect();
		// 初期段階からStandardEntityを取得することができるものに関してindexを貼っておく．
		this.model.indexClass(StandardEntityURN.AMBULANCE_TEAM,
				StandardEntityURN.FIRE_BRIGADE, StandardEntityURN.POLICE_FORCE,
				StandardEntityURN.AMBULANCE_CENTRE,
				StandardEntityURN.FIRE_STATION,
				StandardEntityURN.POLICE_OFFICE, StandardEntityURN.ROAD,
				StandardEntityURN.BUILDING, StandardEntityURN.REFUGE);
		// 可能な限りEntityに関するEntityIDを取得しておく．
		this.getEachEntityIDs();

		this.router = new ASter(this.model);
		E mySelf = this.me();
		this.myID = mySelf.getID();
		this.initBelongPlatoon(mySelf);

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

	/**
	 * 自分がAT,PF,FB,AC,PO,FSのどれであるかを調べ，そのEntityIDのリストをbelongPlatoonに代入する．
	 */
	private void initBelongPlatoon(E mySelf) {
		if (mySelf instanceof AmbulanceTeam) {
			this.belongPlatoon = this.ambulanceTeamList;
		} else if (mySelf instanceof PoliceForce) {
			this.belongPlatoon = this.policeForceList;
		} else if (mySelf instanceof FireBrigade) {
			this.belongPlatoon = this.fireBrigadeList;
		} else if (mySelf instanceof AmbulanceCentre) {
			this.belongPlatoon = this.ambulanceCenterList;
		} else if (mySelf instanceof PoliceOffice) {
			this.belongPlatoon = this.policeOfficeList;
		} else if (mySelf instanceof FireBrigade) {
			this.belongPlatoon = this.fireBrigadeList;
		}
		this.roleIndex = this.belongPlatoon.indexOf(this.myID);
	}

	/**
	 * 最初から取得可能なEntityのEntityIDを取得する．
	 */
	private void getEachEntityIDs() {
		this.ambulanceTeamList = this
				.getEntityIDListFrom(StandardEntityURN.AMBULANCE_TEAM);
		this.fireBrigadeList = this
				.getEntityIDListFrom(StandardEntityURN.FIRE_BRIGADE);
		this.policeForceList = this
				.getEntityIDListFrom(StandardEntityURN.POLICE_FORCE);
		this.platoonList = this.getEntityIDListFrom(
				StandardEntityURN.AMBULANCE_TEAM,
				StandardEntityURN.FIRE_BRIGADE, StandardEntityURN.POLICE_FORCE);

		this.ambulanceCenterList = this
				.getEntityIDListFrom(StandardEntityURN.AMBULANCE_CENTRE);
		this.fireStationList = this
				.getEntityIDListFrom(StandardEntityURN.FIRE_STATION);
		this.policeOfficeList = this
				.getEntityIDListFrom(StandardEntityURN.POLICE_OFFICE);
		this.centerList = this
				.getEntityIDListFrom(StandardEntityURN.AMBULANCE_CENTRE,
						StandardEntityURN.FIRE_STATION,
						StandardEntityURN.POLICE_OFFICE);
		this.roadList = this.getEntityIDListFrom(StandardEntityURN.ROAD);
		this.buildingList = this
				.getEntityIDListFrom(StandardEntityURN.BUILDING);
		this.refugeList = this.getEntityIDListFrom(StandardEntityURN.REFUGE);
		this.areaList = this.getEntityIDListFrom(
				StandardEntityURN.AMBULANCE_CENTRE,
				StandardEntityURN.FIRE_STATION,
				StandardEntityURN.POLICE_OFFICE, StandardEntityURN.ROAD,
				StandardEntityURN.BUILDING, StandardEntityURN.REFUGE);
		this.rescueList = this
				.getEntityIDListFrom(StandardEntityURN.AMBULANCE_TEAM,
						StandardEntityURN.FIRE_BRIGADE,
						StandardEntityURN.POLICE_FORCE,
						StandardEntityURN.AMBULANCE_CENTRE,
						StandardEntityURN.FIRE_STATION,
						StandardEntityURN.POLICE_OFFICE);
	}

	/**
	 * 指定されたStandardEntityURN群からEntityIDのリストを生成する．
	 * 
	 * @param urns
	 * @return
	 */
	private List<EntityID> getEntityIDListFrom(StandardEntityURN... urns) {
		return convertToSortedListOfEntityID(this.model.getEntitiesOfType(urns));
	}

	/**
	 * StandardEntityのコレクションからIDでソートされたEntityIDのリストを生成する．
	 * 
	 * @param col
	 *            StandardEntityのコレクション
	 * @return
	 */
	private List<EntityID> convertToSortedListOfEntityID(
			Collection<StandardEntity> col) {
		List<EntityID> res = new ArrayList<EntityID>();
		for (StandardEntity se : col) {
			res.add(se.getID());
		}
		// EntityIDでソート
		Collections.sort(res, new EntityIDComparator());
		return res;
	}

	@Override
	protected void thinking(int arg0, ChangeSet arg1, Collection<Command> arg2) {
		if (arg0 == this.constants.START_ACTION_TIME) {
			int channel = this.getMessageChannel();
			this.sendSubscribe(arg0, channel);
		}
	}

	protected void arrangeMessage(ChangeSet changed) {
		Collection<EntityID> visible = changed.getChangedEntities();
		List<WorldInformation> infoList = new ArrayList<WorldInformation>();
		this.unpassableInfoList = new ArrayList<UnpassableInformation>();
		// 見える情報を削除(送られてきた情報より見える情報の方が鮮度が高い)
		for (RCRSCSMessage message : this.receivedMessageList) {
			if (message instanceof UnpassableInformation) {
				this.unpassableInfoList.add((UnpassableInformation) message);
			}
			if (message instanceof TransferInformation) {
				infoList.add((WorldInformation) message);
			} else if (message instanceof WorldInformation) {
				WorldInformation info = (WorldInformation) message;
				if (!visible.contains(info.getEntityID())) {
					infoList.add(info);
				}
			}
		}
		for (WorldInformation message : infoList) {
			try {
				StandardEntity se = this.model.getEntity(message.getEntityID());
				if (se == null) {
					this.addNewData(message);
				} else {
					this.updateData(message, se);
				}
			} catch (Exception e) {
			}
		}
	}

	protected abstract void addNewData(WorldInformation message);

	protected abstract void updateData(WorldInformation message,
			StandardEntity se);
}
